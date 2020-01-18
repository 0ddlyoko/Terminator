/**
 * 
 */
package me.oddlyoko.terminator.config;

import java.io.File;
import java.util.List;

import lombok.Getter;

/**
 * Farm Copyright (C) 2019 0ddlyoko
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * @author 0ddlyoko
 */
public class ConfigManager {
	private Config config;
	@Getter
	private String host;
	@Getter
	private int port;
	@Getter
	private String dbName;
	@Getter
	private String user;
	@Getter
	private String password;
	@Getter
	private boolean mysql;
	@Getter
	private List<String> banMessage;
	@Getter
	private List<String> banIpMessage;

	public ConfigManager() {
		config = new Config(new File("plugins" + File.separator + "Terminator" + File.separator + "config.yml"));
		host = config.getString("host");
		port = config.getInt("port");
		dbName = config.getString("dbname");
		user = config.getString("user");
		password = config.getString("password");
		mysql = config.getBoolean("mysql");
		banMessage = config.getStringList("ban_message");
		banIpMessage = config.getStringList("banip_message");
	}
}
