package gg.mmorealms.loader.common.manager.database;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgsLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.config.DatabaseConfig;
import gg.mmorealms.loader.common.manager.database.hibernate.GsonJsonFormatMapper;
import jakarta.persistence.Entity;
import lombok.Getter;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.HikariCPSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.query.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

@Getter
public class DatabaseConnection {

	private static int nextConnectionID = 1;

	private final HashMap<String, Class<?>> tableToClassMap = new HashMap<>();
	private int connectionID = -1;
	private int maxPoolSize = 10;
	private SessionFactory sessionFactory;

	public DatabaseConnection(@NotNull DatabaseConfig config) {
		this(config, 10, 2);
	}

	public DatabaseConnection(@NotNull DatabaseConfig config, int maxPoolSize, int minIdle) {
		if (CommonLoader.DUMMY_MODE) {
			return;
		}
		this.connectionID = nextConnectionID;
		connect(config, maxPoolSize, minIdle);
		nextConnectionID += 1;
	}

	public @Nullable Class<?> getClassFromTable(@NotNull String table) {
		return tableToClassMap.get(table);
	}

	public void connect(DatabaseConfig config) {
		connect(config, 10, 2);
	}

	public void connect(DatabaseConfig config, int maxPoolSize, int minIdle) {
		this.maxPoolSize = maxPoolSize;
		Logger.log("[DatabaseManager] Connecting to database " + config.getConnectionURL() + ". Username: " + config.getUsername());

		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
		registryBuilder.applySetting(JdbcSettings.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
		String jdbcUrl = config.getConnectionURL();
		String driver = config.getDriver();
		if (driver != null) {
			if ((driver.contains("mariadb") || driver.contains("mysql")) && !jdbcUrl.contains("rewriteBatchedStatements")) {
				jdbcUrl += (jdbcUrl.contains("?") ? "&" : "?") + "rewriteBatchedStatements=true";
			} else if (driver.contains("postgresql") && !jdbcUrl.contains("reWriteBatchedInserts")) {
				jdbcUrl += (jdbcUrl.contains("?") ? "&" : "?") + "reWriteBatchedInserts=true";
			}
		}
		registryBuilder.applySetting("hibernate.hikari.jdbcUrl", jdbcUrl);
		registryBuilder.applySetting("hibernate.hikari.username", config.getUsername());
		registryBuilder.applySetting("hibernate.hikari.password", config.getPassword());
		registryBuilder.applySetting("hibernate.hikari.driverClassName", config.getDriver());
		registryBuilder.applySetting(HikariCPSettings.HIKARI_POOL_NAME, "HikariPool-MMORealms-" + this.connectionID);
		registryBuilder.applySetting("hibernate.globally_quoted_identifiers", "true");
		registryBuilder.applySetting(HikariCPSettings.HIKARI_KEEPALIVE_TIME, "60000");
		registryBuilder.applySetting(HikariCPSettings.HIKARI_LEAK_TIMEOUT, "60000");
		registryBuilder.applySetting(HikariCPSettings.HIKARI_IDLE_TIMEOUT, "120000");
		registryBuilder.applySetting("hibernate.hikari.maximumPoolSize", String.valueOf(maxPoolSize));
		registryBuilder.applySetting("hibernate.hikari.minimumIdle", String.valueOf(minIdle));
		registryBuilder.applySetting("hibernate.hikari.leakDetectionThreshold", "60000");
		registryBuilder.applySetting(JdbcSettings.DIALECT, config.dialect);
		registryBuilder.applySetting(JdbcSettings.AUTOCOMMIT, Boolean.TRUE.toString());
		registryBuilder.applySetting(SchemaToolingSettings.HBM2DDL_AUTO, "update");
		registryBuilder.applySetting(JdbcSettings.SHOW_SQL, String.valueOf(config.isDebug()));
		registryBuilder.applySetting(JdbcSettings.FORMAT_SQL, String.valueOf(config.isDebug()));
		registryBuilder.applySetting(JdbcSettings.HIGHLIGHT_SQL, String.valueOf(config.isDebug()));
		registryBuilder.applySetting(AvailableSettings.JSON_FORMAT_MAPPER, GsonJsonFormatMapper.class.getName());
		registryBuilder.applySetting("hibernate.order_inserts", "true");
		registryBuilder.applySetting("hibernate.jdbc.batch_versioned_data", "true");

		StandardServiceRegistry serviceRegistry = registryBuilder.build();
		MetadataSources metadataSources = new MetadataSources(serviceRegistry);

		for (Class<?> clazz : CommonLoader.instance().getReflectionsCrawler().getClassesAnnotatedWith(Entity.class, false)) {
			Entity entity = clazz.getAnnotation(Entity.class);
			if (entity == null) {
				Logger.error("Class " + clazz.getName() + " is not annotated with @Entity. This error should not happen. There is something wrong with the reflection engine.");
				continue;
			}
			Logger.debug(new MessageBuilder("Registering entity {entity}")
					.parse("entity", clazz.getName())
					.parse());
			tableToClassMap.put(entity.name(), clazz);
			metadataSources.addAnnotatedClass(clazz);
		}

		org.hibernate.boot.MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();

		sessionFactory = metadataBuilder.build().buildSessionFactory();
	}

	public <T> void executeComplex(
			@NotNull ReturnArgLambda<Query<T>, StatelessSession> dataQueryCreator,
			@NotNull ReturnArgLambda<Query<Long>, StatelessSession> countQueryCreator,
			@NotNull ArgLambda<T> resultParser,
			@NotNull ArgsLambda<Long, Long> processCallback,
			@NotNull Lambda finishedCallback,
			int throttleThreshold,
			@NotNull Time throttleTimeout
	) {
		ScheduleUtils.runTaskAsync(() ->
				executeComplexSync(
						dataQueryCreator,
						countQueryCreator,
						resultParser,
						processCallback,
						finishedCallback,
						throttleThreshold,
						throttleTimeout
				)
		);
	}

	private <T> void executeComplexSync(
			@NotNull ReturnArgLambda<Query<T>, StatelessSession> queryCreator,
			@NotNull ReturnArgLambda<Query<Long>, StatelessSession> countQueryCreator,
			@NotNull ArgLambda<T> resultParser,
			@NotNull ArgsLambda<Long, Long> processCallback,
			@NotNull Lambda finishedCallback,
			int throttleThreshold,
			@NotNull Time throttleTimeout
	) {
		Long totalCount;

		try (StatelessSession session = this.sessionFactory.openStatelessSession()) {
			totalCount = countQueryCreator.run(session).uniqueResult();
		} catch (Exception exception) {
			Logger.error(exception);
			return;
		}

		long currentCount = 0L;

		try (StatelessSession session = this.sessionFactory.openStatelessSession()) {
			try (ScrollableResults<T> results = queryCreator.run(session)
					.setReadOnly(true)
					.setCacheable(false)
					.scroll()) {
				while (results.next()) {
					T result = results.get();
					resultParser.run(result);

					currentCount++;
					if (currentCount % throttleThreshold == 0) {
						processCallback.run(currentCount, totalCount);
						try {
							//noinspection BusyWait
							Thread.sleep(throttleTimeout.toMilliseconds());
						} catch (InterruptedException exception) {
							Logger.error(exception);
							Thread.currentThread().interrupt();
						}
					}
				}
			}

			finishedCallback.run();
		}
	}

	public void close() {
		if (sessionFactory != null && sessionFactory.isOpen()) {
			sessionFactory.close();
		}
	}

}
