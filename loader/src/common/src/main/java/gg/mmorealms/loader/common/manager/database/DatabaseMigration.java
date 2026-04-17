package gg.mmorealms.loader.common.manager.database;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
public class DatabaseMigration {

	private final DatabaseConnection source;
	private final DatabaseConnection target;

	public void start() {
		ScheduleUtils.runTaskAsync(() -> {
			try {
				Logger.log("[Migration] Starting migration of " + this.source.getTableToClassMap().size() + " tables...");
				migrateAllEntitiesTo();
			} catch (Exception exception) {
				Logger.error(exception);
				Logger.log("[Migration] Migration failed with an error. Check the logs above.");
			} finally {
				this.source.close();
				target.close();
				Logger.log("[Migration] Connections closed.");
			}
		});
	}

	private void migrateAllEntitiesTo() {
		List<Map.Entry<String, Class<?>>> entries = new ArrayList<>(this.source.getTableToClassMap().entrySet());
		int totalTables = entries.size();
		int CHUNK_SIZE = 25_000;
		int THREAD_COUNT = 16;

		// Scale up connection pools for parallel migration (pools are closed when migration ends)
		resizePool(this.source.getSessionFactory(), THREAD_COUNT);
		resizePool(target.getSessionFactory(), THREAD_COUNT);

		long wallStart = System.currentTimeMillis();

		// Count rows per table and split into chunks
		//noinspection resource
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
		List<Future<?>> futures = new ArrayList<>();
		List<TableTracker> trackers = new ArrayList<>();
		int totalChunks = 0;
		long totalRowsAll = 0;
		AtomicLong globalMigratedCount = new AtomicLong(0);

		for (int i = 0; i < totalTables; i++) {
			Map.Entry<String, Class<?>> entry = entries.get(i);
			Class<?> clazz = entry.getValue();
			Entity entityAnnotation = clazz.getAnnotation(Entity.class);
			if (entityAnnotation == null) continue;
			String entityName = entityAnnotation.name();
			int tableIdx = i + 1;

			long totalRows;
			try (StatelessSession session = this.source.getSessionFactory().openStatelessSession()) {
				totalRows = session.createQuery("SELECT COUNT(*) FROM " + entityName, Long.class).uniqueResult();
			}

			if (totalRows == 0) {
				Logger.log("[Migration] [" + tableIdx + "/" + totalTables + "] " + entityName + ": Empty, skipping");
				continue;
			}

			totalRowsAll += totalRows;
			int chunkCount = (int) Math.ceil((double) totalRows / CHUNK_SIZE);
			totalChunks += chunkCount;
			TableTracker tracker = new TableTracker(entityName, totalRows, chunkCount);
			trackers.add(tracker);

			Logger.log("[Migration] [" + tableIdx + "/" + totalTables + "] " + entityName + ": "
					+ fmt(totalRows) + " rows -> " + chunkCount + " chunk(s)");

			for (int c = 0; c < chunkCount; c++) {
				int chunkOffset = c * CHUNK_SIZE;
				int chunkIdx = c + 1;
				futures.add(executor.submit(() -> migrateChunk(
						clazz, target.getSessionFactory(), entityName,
						chunkOffset, CHUNK_SIZE, tracker,
						globalMigratedCount,
						tableIdx, totalTables, chunkIdx, chunkCount
				)));
			}
		}

		Logger.log("[Migration] " + totalChunks + " chunks queued across " + totalTables
				+ " tables (" + fmt(totalRowsAll) + " total rows), " + THREAD_COUNT + " threads");

		// Periodic global progress logger — ETA based on currently-running tables only
		long totalRowsFinal = totalRowsAll;
		Thread progressLogger = new Thread(() -> {
			Logger.log("[Migration] Progress tracker started (reporting every 5s)");
			while (!Thread.currentThread().isInterrupted()) {
				try {
					//noinspection BusyWait
					Thread.sleep(5_000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
				long migrated = globalMigratedCount.get();
				double pct = (double) migrated / totalRowsFinal * 100.0;

				// Compute ETA from currently-running tables only (finished tables excluded)
				long now = System.currentTimeMillis();
				long activeRemaining = 0;
				double activeThroughput = 0;
				int running = 0;
				int done = 0;
				for (TableTracker t : trackers) {
					if (t.isDone()) {
						done++;
						continue;
					}
					running++;
					long tMigrated = t.migrated.get();
					activeRemaining += t.totalRows - tMigrated;
					long tStart = t.startTimeMs.get();
					if (tStart > 0 && tMigrated > 0) {
						activeThroughput += (double) tMigrated / (now - tStart);
					}
				}
				String eta = activeThroughput > 0
						? "" + Time.milliseconds((long) (activeRemaining / activeThroughput))
						: "calculating...";

				String elapsed = "" + Time.milliseconds(now - wallStart);

				Logger.log("[Migration] Progress: " + fmt(migrated) + " / " + fmt(totalRowsFinal)
						+ " entries (" + String.format("%.1f", pct) + "%)"
						+ " | " + running + " tables active, " + done + " done"
						+ " | ETA " + eta + " | Elapsed " + elapsed);
			}
			Logger.log("[Migration] Progress tracker stopped");
		}, "migration-progress");
		progressLogger.setDaemon(true);
		progressLogger.start();

		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (Exception exception) {
				Logger.error(exception);
			}
		}

		progressLogger.interrupt();
		executor.shutdown();

		// Finalize target database for production use
		resetPostgresSequences(target.getSessionFactory());
		analyzePostgresTables(target.getSessionFactory());

		Logger.log("[Migration] All " + totalTables + " tables done. " + fmt(globalMigratedCount.get()) + " rows migrated.");
		Logger.log("[Migration] Wall time: " + Time.milliseconds(System.currentTimeMillis() - wallStart));
	}

	@SuppressWarnings("unchecked")
	private <T> void migrateChunk(@NotNull Class<?> rawClazz, @NotNull SessionFactory targetFactory,
	                              @NotNull String entityName, int offset, int limit,
	                              @NotNull TableTracker tracker,
	                              @NotNull AtomicLong globalMigratedCount,
	                              int tableIndex, int totalTables, int chunkIndex, int totalChunks) {
		Class<T> clazz = (Class<T>) rawClazz;
		tracker.markStarted();
		int PAGE_SIZE = 500;
		int JDBC_BATCH_SIZE = 500;
		String label = "[" + tableIndex + "/" + totalTables + "] " + entityName
				+ " chunk " + chunkIndex + "/" + totalChunks;

		try {
			StatelessSession targetSession = targetFactory.openStatelessSession();
			targetSession.setJdbcBatchSize(JDBC_BATCH_SIZE);

			// Disable constraint checks for faster bulk inserts
			setConstraintChecks(targetSession, false);

			try {
				long chunkMigrated = 0;

				try (StatelessSession sourceSession = this.source.getSessionFactory().openStatelessSession()) {
					// Phase 1: Read IDs only — scalar query has no eager JOINs,
					// so OFFSET/LIMIT counts actual entities, not inflated JOIN rows
					List<Object> ids = sourceSession.createQuery("SELECT e.id FROM " + entityName + " e", Object.class)
							.setReadOnly(true)
							.setCacheable(false)
							.setFirstResult(offset)
							.setMaxResults(limit)
							.list();

					// Phase 2: Load entities by ID in pages and insert
					for (int i = 0; i < ids.size(); i += PAGE_SIZE) {
						List<Object> pageIds = ids.subList(i, Math.min(i + PAGE_SIZE, ids.size()));
						List<T> page = sourceSession.createQuery(
										"FROM " + entityName + " e WHERE e.id IN (:ids)", clazz)
								.setReadOnly(true)
								.setCacheable(false)
								.setParameter("ids", pageIds)
								.list();

						// Clear eagerly-loaded collections — they are migrated as their own tables
						// and their presence causes StatelessSession insert failures
						for (T entity : page) {
							clearEagerCollections(entity);
						}

						int pageInserted = insertPage(targetSession, page, label);
						chunkMigrated += pageInserted;
						globalMigratedCount.addAndGet(pageInserted);
					}
				}

				long tableTotal = tracker.migrated.addAndGet(chunkMigrated);
				double pct = (double) tableTotal / tracker.totalRows * 100.0;
				Logger.log("[Migration] " + label + " done - " + fmt(chunkMigrated) + " rows"
						+ " (table: " + fmt(tableTotal) + "/" + fmt(tracker.totalRows)
						+ " " + String.format("%.1f", pct) + "%)");

			} catch (Exception exception) {
				if (targetSession.getTransaction() != null && targetSession.getTransaction().isActive()) {
					targetSession.getTransaction().rollback();
				}
				throw exception;
			} finally {
				// Re-enable constraint checks before returning connection to pool
				setConstraintChecks(targetSession, true);
				targetSession.close();
			}
		} catch (Exception exception) {
			Logger.error(exception);
			Logger.warn("[Migration] " + label + ": FAILED");
		} finally {
			tracker.doneChunks.incrementAndGet();
		}
	}

	private static class TableTracker {
		final String name;
		final long totalRows;
		final AtomicLong migrated = new AtomicLong(0);
		final int totalChunks;
		final AtomicInteger doneChunks = new AtomicInteger(0);
		final AtomicLong startTimeMs = new AtomicLong(0);

		TableTracker(String name, long totalRows, int totalChunks) {
			this.name = name;
			this.totalRows = totalRows;
			this.totalChunks = totalChunks;
		}

		void markStarted() {
			startTimeMs.compareAndSet(0, System.currentTimeMillis());
		}

		boolean isDone() {
			return doneChunks.get() >= totalChunks;
		}
	}

	private static void clearEagerCollections(@NotNull Object entity) {
		for (Class<?> clazz = entity.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
			for (Field field : clazz.getDeclaredFields()) {
				if (!Collection.class.isAssignableFrom(field.getType())) continue;
				if (!field.isAnnotationPresent(OneToMany.class)
						&& !field.isAnnotationPresent(ManyToMany.class)) continue;

				field.setAccessible(true);
				try {
					Collection<?> col = (Collection<?>) field.get(entity);
					if (col != null) col.clear();
				} catch (IllegalAccessException ignored) {
				}
			}
		}
	}

	private static <T> int insertPage(@NotNull StatelessSession targetSession, @NotNull List<T> page, @NotNull String label) {
		int pageInserted = 0;
		try {
			targetSession.beginTransaction();
			for (T entity : page) {
				targetSession.insert(entity);
			}
			targetSession.getTransaction().commit();
			pageInserted = page.size();
		} catch (Exception batchEx) {
			// Batch failed — rollback and clear the stale JDBC batch
			if (targetSession.getTransaction() != null && targetSession.getTransaction().isActive()) {
				targetSession.getTransaction().rollback();
			}
			if (targetSession instanceof org.hibernate.engine.spi.SharedSessionContractImplementor impl) {
				impl.getJdbcCoordinator().abortBatch();
			}

			// Retry one-by-one without batching
			Integer originalBatchSize = targetSession.getJdbcBatchSize();
			targetSession.setJdbcBatchSize(1);
			int skipped = 0;
			Exception firstException = null;
			for (T entity : page) {
				try {
					targetSession.beginTransaction();
					targetSession.insert(entity);
					targetSession.getTransaction().commit();
					pageInserted++;
				} catch (Exception entityEx) {
					if (targetSession.getTransaction() != null && targetSession.getTransaction().isActive()) {
						targetSession.getTransaction().rollback();
					}
					if (firstException == null) firstException = entityEx;
					skipped++;
				}
			}
			targetSession.setJdbcBatchSize(originalBatchSize);
			if (skipped > 0) {
				Logger.warn("[Migration] " + label + ": " + skipped + "/" + page.size() + " entities failed"
						+ " | batch error: " + batchEx.getMessage());
				Logger.warn("[Migration] " + label + " first entity error: " + firstException.getMessage());
			}
		}
		return pageInserted;
	}

	private static void resizePool(@NotNull SessionFactory factory, int newSize) {
		try {
			var sfi = (org.hibernate.engine.spi.SessionFactoryImplementor) factory;
			var cp = sfi.getServiceRegistry()
					.getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class);
			if (cp == null) return;

			// Unwrap as javax.sql.DataSource to avoid shaded class references
			var ds = cp.unwrap(javax.sql.DataSource.class);
			if (ds == null) return;

			// Use reflection to call HikariCP methods (works regardless of package shading)
			var clazz = ds.getClass();

			// 1) Resize pool
			try {
				var getName = clazz.getMethod("getPoolName");
				var getMax = clazz.getMethod("getMaximumPoolSize");
				var setMax = clazz.getMethod("setMaximumPoolSize", int.class);
				var setMin = clazz.getMethod("setMinimumIdle", int.class);
				Logger.log("[Migration] Resizing pool " + getName.invoke(ds) + ": " + getMax.invoke(ds) + " -> " + newSize);
				setMax.invoke(ds, newSize);
				setMin.invoke(ds, newSize);
			} catch (Exception e) {
				Logger.warn("[Migration] Could not resize pool: " + e.getMessage());
			}

			// 2) Disable leak detection — must set on the RUNNING pool, not just the config
			//    HikariDataSource.setLeakDetectionThreshold only updates config;
			//    the live HikariPool has its own copy that must be updated separately.
			try {
				// Set on config level (for any future pool restarts)
				var setLeak = clazz.getMethod("setLeakDetectionThreshold", long.class);
				setLeak.invoke(ds, 0L);
			} catch (Exception ignored) {
			}

			try {
				// Set on live pool via HikariPoolMXBean — this is what actually stops leak warnings
				var getPool = clazz.getMethod("getHikariPoolMXBean");
				var pool = getPool.invoke(ds);
				if (pool != null) {
					var poolSetLeak = pool.getClass().getMethod("setLeakDetectionThreshold", long.class);
					poolSetLeak.invoke(pool, 0L);
					Logger.log("[Migration] Leak detection disabled on live pool");
				}
			} catch (Exception e) {
				// Last resort: access the pool field directly
				try {
					var poolField = clazz.getDeclaredField("pool");
					poolField.setAccessible(true);
					var pool = poolField.get(ds);
					if (pool != null) {
						var poolSetLeak = pool.getClass().getMethod("setLeakDetectionThreshold", long.class);
						poolSetLeak.invoke(pool, 0L);
						Logger.log("[Migration] Leak detection disabled via pool field");
					}
				} catch (Exception e2) {
					Logger.warn("[Migration] Could not disable leak detection: " + e2.getMessage());
				}
			}
		} catch (Exception exception) {
			Logger.warn("[Migration] Could not access connection pool: " + exception.getMessage());
		}
	}

	private static void setConstraintChecks(@NotNull StatelessSession session, boolean enabled) {
		try {
			session.doWork(connection -> {
				String dbName = connection.getMetaData().getDatabaseProductName().toLowerCase();
				try (var stmt = connection.createStatement()) {
					if (dbName.contains("postgre")) {
						//noinspection SqlNoDataSourceInspection
						stmt.execute("SET session_replication_role = '" + (enabled ? "origin" : "replica") + "'");
						//noinspection SqlNoDataSourceInspection
						stmt.execute("SET synchronous_commit = '" + (enabled ? "on" : "off") + "'");
					} else {
						//noinspection SqlNoDataSourceInspection
						stmt.execute("SET FOREIGN_KEY_CHECKS=" + (enabled ? 1 : 0));
						//noinspection SqlNoDataSourceInspection
						stmt.execute("SET UNIQUE_CHECKS=" + (enabled ? 1 : 0));
					}
				}
			});
		} catch (Exception ignored) {
		}
	}

	private static void resetPostgresSequences(@NotNull SessionFactory targetFactory) {
		try (StatelessSession session = targetFactory.openStatelessSession()) {
			session.doWork(connection -> {
				if (!connection.getMetaData().getDatabaseProductName().toLowerCase().contains("postgre")) return;

				try (var queryStmt = connection.createStatement();
				     var updateStmt = connection.createStatement()) {
					//noinspection SqlNoDataSourceInspection
					var rs = queryStmt.executeQuery(
							"SELECT s.relname AS seq_name, t.relname AS table_name, a.attname AS column_name " +
									"FROM pg_class s " +
									"JOIN pg_depend d ON d.objid = s.oid AND d.deptype IN ('a', 'i') " +
									"JOIN pg_class t ON d.refobjid = t.oid " +
									"JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = d.refobjsubid " +
									"WHERE s.relkind = 'S'"
					);
					int count = 0;
					while (rs.next()) {
						String seqName = rs.getString("seq_name");
						String tableName = rs.getString("table_name");
						String columnName = rs.getString("column_name");
						try {
							//noinspection SqlNoDataSourceInspection
							updateStmt.execute(
									"SELECT setval('\"" + seqName + "\"', GREATEST(COALESCE((SELECT MAX(\"" + columnName + "\") FROM \"" + tableName + "\"), 0), 1))"
							);
							count++;
						} catch (Exception e) {
							Logger.warn("[Migration] Failed to reset sequence " + seqName + ": " + e.getMessage());
						}
					}
					Logger.log("[Migration] Reset " + count + " PostgreSQL sequence(s)");
				}
			});
		} catch (Exception e) {
			Logger.warn("[Migration] Could not reset PostgreSQL sequences: " + e.getMessage());
		}
	}

	private static void analyzePostgresTables(@NotNull SessionFactory targetFactory) {
		try (StatelessSession session = targetFactory.openStatelessSession()) {
			session.doWork(connection -> {
				if (!connection.getMetaData().getDatabaseProductName().toLowerCase().contains("postgre")) {
					return;
				}

				try (var stmt = connection.createStatement()) {
					//noinspection SqlNoDataSourceInspection
					stmt.execute("ANALYZE");
					Logger.log("[Migration] ANALYZE completed — query planner statistics updated");
				}
			});
		} catch (Exception e) {
			Logger.warn("[Migration] Could not ANALYZE target database: " + e.getMessage());
		}
	}

	private static String fmt(long n) {
		return String.format("%,d", n).replace(',', '.');
	}

}
