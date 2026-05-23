package gg.mmorealms.loader.common.manager.database;

import gg.mmorealms.loader.common.dto.config.DatabaseConfig;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

}