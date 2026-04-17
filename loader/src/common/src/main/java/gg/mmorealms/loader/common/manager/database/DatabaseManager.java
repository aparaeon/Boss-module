package gg.mmorealms.loader.common.manager.database;

import gg.mmorealms.loader.common.dto.config.DatabaseConfig;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class DatabaseManager {

	@Getter
	@Accessors(fluent = true)
	private static DatabaseManager instance;

	private final DatabaseConnection connection;

	public DatabaseManager(@NotNull DatabaseConfig config) {
		instance = this;
		this.connection = new DatabaseConnection(config);
	}

	public SessionFactory getSessionFactory() {
		return connection.getSessionFactory();
	}

	public @Nullable SessionFactory getSecondarySessionFactory() {
		return connection.getSessionFactory();
	}

	public @Nullable Class<?> getClassFromTable(@NotNull String table) {
		return connection.getClassFromTable(table);
	}

	public void inTransaction(@NotNull Consumer<StatelessSession> work) {
		try (StatelessSession session = connection.getSessionFactory().openStatelessSession()) {
			session.beginTransaction();
			try {
				work.accept(session);
				session.getTransaction().commit();
			} catch (Exception e) {
				session.getTransaction().rollback();
				throw e;
			}
		}
	}

	public <T> T inTransactionReturn(@NotNull Function<StatelessSession, T> work) {
		try (StatelessSession session = connection.getSessionFactory().openStatelessSession()) {
			session.beginTransaction();
			try {
				T result = work.apply(session);
				session.getTransaction().commit();
				return result;
			} catch (Exception e) {
				session.getTransaction().rollback();
				throw e;
			}
		}
	}

	public <T> void upsert(@NotNull T entity, @NotNull Class<T> clazz, @NotNull Object id) {
		inTransaction(session -> {
			if (session.get(clazz, id) != null) {
				session.update(entity);
			} else {
				session.insert(entity);
			}
		});
	}

	public <T> void delete(@NotNull Class<T> clazz, @NotNull Object id) {
		inTransaction(session -> {
			Object existing = session.get(clazz, id);
			if (existing != null) {
				session.delete(existing);
			}
		});
	}

}