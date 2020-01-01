package me.oddlyoko.terminator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.oddlyoko.terminator.bans.BanManager;
import me.oddlyoko.terminator.commands.BanCmd;
import me.oddlyoko.terminator.commands.HistCmd;
import me.oddlyoko.terminator.commands.KickCmd;
import me.oddlyoko.terminator.commands.MuteCmd;
import me.oddlyoko.terminator.commands.UnMuteCmd;
import me.oddlyoko.terminator.commands.UnbanCmd;
import me.oddlyoko.terminator.config.ConfigManager;
import me.oddlyoko.terminator.inventories.InventoryManager;
import me.oddlyoko.terminator.mutes.MuteManager;

public class Terminator extends JavaPlugin {
	public static Terminator TERMINATOR;

	private ConfigManager configManager;
	private BanManager banManager;
	private MuteManager muteManager;
	private InventoryManager inventorymanager;
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
		inventorymanager = new InventoryManager();
		inventorymanager.init();
		UUIDs.load();
		for (Player p : Bukkit.getOnlinePlayers())
			UUIDs.register(p.getUniqueId(), p.getName());
		uuidTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			// Save uuids each hours
			UUIDs.save();
		}, 1, 3600);
		getServer().getPluginManager().registerEvents(banManager = new BanManager(), this);
		getServer().getPluginManager().registerEvents(muteManager = new MuteManager(), this);
		getCommand("ban").setExecutor(new BanCmd());
		getCommand("kick").setExecutor(new KickCmd());
		getCommand("mute").setExecutor(new MuteCmd());
		getCommand("unmute").setExecutor(new UnMuteCmd());
		getCommand("unban").setExecutor(new UnbanCmd());
		getCommand("hist").setExecutor(new HistCmd());
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

	public MuteManager getMuteManager() {
		return muteManager;
	}

	public InventoryManager getInventorymanager() {
		return inventorymanager;
	}

	public void setInventorymanager(InventoryManager inventorymanager) {
		this.inventorymanager = inventorymanager;
	}

	
}
