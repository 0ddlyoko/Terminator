package me.oddlyoko.terminator.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.oddlyoko.terminator.__;

public class PlayerConfigManager {
	private File directory;

	public PlayerConfigManager() {
		directory = new File("plugins" + File.separator + __.NAME + File.separator + "players");
		if (!directory.exists())
			directory.mkdirs();
	}

	public Config getPlayer(UUID uuid) {
		File f = new File(directory, uuid.toString() + ".yml");
		if (f == null || !f.exists())
			return null;
		return new Config(f);
	}

	public Config getPlayerOrCreateFile(UUID uuid) {
		File f = new File(directory, uuid.toString() + ".yml");
		if (f == null || !f.exists())
			try {
				f.createNewFile();
			} catch (IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Cannot create config file for player " + uuid, ex);
				return null;
			}
		return new Config(f);
	}

	public void savePlayer(UUID uuid, String name, String ip) {
		Config c = getPlayerOrCreateFile(uuid);
		if (c == null)
			throw new IllegalStateException("File for player " + uuid + " hasn't been created");
		List<String> oldNames = c.getStringList("name");
		if (oldNames == null)
			oldNames = new ArrayList<>();
		// Check if last name isn't the same
		if (oldNames.isEmpty() || !oldNames.get(oldNames.size() - 1).equals(name))
			oldNames.add(name);
		c.set("name", oldNames);

		List<String> oldIps = c.getStringList("ip");
		if (oldIps == null)
			oldIps = new ArrayList<>();
		// Check if last ip isn't the same
		if (oldIps.isEmpty() || !oldIps.get(oldIps.size() - 1).equals(ip))
			oldIps.add(ip);
		c.set("ip", oldIps);
	}

	public List<String> getNames(UUID uuid) {
		Config c = getPlayer(uuid);
		if (c == null)
			return new ArrayList<>();
		return c.getStringList("name");
	}

	public List<String> getIps(UUID uuid) {
		Config c = getPlayer(uuid);
		if (c == null)
			return new ArrayList<>();
		return c.getStringList("ip");
	}
}
