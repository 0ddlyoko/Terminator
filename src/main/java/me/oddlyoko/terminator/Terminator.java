package me.oddlyoko.terminator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.oddlyoko.terminator.bans.BanManager;
import me.oddlyoko.terminator.commands.BanCmd;
import me.oddlyoko.terminator.commands.UnbanCmd;
import me.oddlyoko.terminator.config.ConfigManager;

public class Terminator extends JavaPlugin {
	public static Terminator TERMINATOR;

	private ConfigManager configManager;
	private BanManager banManager;
	private BukkitTask uuidTask;

	public Terminator() {
		if (TERMINATOR != null)
			throw new IllegalStateException("Cannot redefine a Singleton");
		TERMINATOR = this;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		configManager = new ConfigManager();
		UUIDs.load();
		for (Player p : Bukkit.getOnlinePlayers())
			UUIDs.register(p.getUniqueId(), p.getName());
		uuidTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			// Save uuids each hours
			UUIDs.save();
		}, 1, 3600);
		getServer().getPluginManager().registerEvents(banManager = new BanManager(), this);
		getServer().getPluginManager().registerEvents(new Listener(), this);
		getCommand("ban").setExecutor(new BanCmd());
		getCommand("unban").setExecutor(new UnbanCmd());
	}

	@Override
	public void onDisable() {
		if (uuidTask != null)
			uuidTask.cancel();
		UUIDs.save();
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public BanManager getBanManager() {
		return banManager;
	}

	public static Terminator get() {
		return TERMINATOR;
	}
}
