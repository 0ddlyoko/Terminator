package me.oddlyoko.terminator.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.oddlyoko.terminator.Terminator;

public class DatabaseModel {
	private String host;
	private int port;
	private String dbName;
	private String login;
	private String password;

	public DatabaseModel(String host, String dbName, String login, String password) throws ClassNotFoundException {
		this(host, 3306, dbName, login, password);
	}

	public DatabaseModel(String host, int port, String dbName, String login, String password)
			throws ClassNotFoundException {
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.login = login;
		this.password = password;
		loadDriver();
	}

	private void loadDriver() throws ClassNotFoundException {
		if (Terminator.get().getConfigManager().isMysql())
			Class.forName("com.mysql.jdbc.Driver");
		else
			Class.forName("org.mariadb.jdbc.Driver");
	}

	public Connection getConnection() throws SQLException {
		String use = "mysql";
		if (!Terminator.get().getConfigManager().isMysql())
			use = "mariadb";
		return DriverManager.getConnection("jdbc:" + use + "://" + host + ":" + port + "/" + dbName + "?useSSL=false",
				login, password);
	}
}
