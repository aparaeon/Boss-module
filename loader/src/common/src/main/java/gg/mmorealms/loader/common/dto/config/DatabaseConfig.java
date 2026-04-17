package gg.mmorealms.loader.common.dto.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConfig {

	public String connectionURL = "jdbc:mariadb://mariadb:3306/core";
	public String username = "username";
	public String password = "password";
	public String driver = "org.mariadb.jdbc.Driver";
	public String dialect = "org.hibernate.dialect.MariaDBDialect";
	public boolean debug = false;

}
