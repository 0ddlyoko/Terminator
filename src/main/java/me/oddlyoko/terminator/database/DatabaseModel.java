package me.oddlyoko.terminator.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
		Class.forName("com.mysql.jdbc.Driver");
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc://mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false",
				login, password);
	}
}
