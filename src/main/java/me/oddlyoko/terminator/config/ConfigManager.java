/**
 * 
 */
package me.oddlyoko.terminator.config;

import java.io.File;
import java.util.List;

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
	private List<String> banMessage;

	public ConfigManager() {
		config = new Config(new File("plugins" + File.separator + "Terminator" + File.separator + "config.yml"));
		banMessage = config.getStringList("ban_message");
	}

	public List<String> getBanMessage() {
		return banMessage;
	}
}
