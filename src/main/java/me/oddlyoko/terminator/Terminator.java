package me.oddlyoko.terminator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import me.oddlyoko.terminator.commands.BanCmd;
import me.oddlyoko.terminator.commands.BypassCmd;
import me.oddlyoko.terminator.commands.HistCmd;
import me.oddlyoko.terminator.commands.KickCmd;
import me.oddlyoko.terminator.commands.MuteCmd;
import me.oddlyoko.terminator.commands.UnMuteCmd;
import me.oddlyoko.terminator.commands.UnbanCmd;
import me.oddlyoko.terminator.config.ConfigManager;
import me.oddlyoko.terminator.database.DatabaseManager;
import me.oddlyoko.terminator.inventories.InventoryManager;
import me.oddlyoko.terminator.terminator.TerminatorManager;

public class Terminator extends JavaPlugin implements Listener {
	public static Terminator TERMINATOR;
	@Getter
	private DatabaseManager databaseManager;
	@Getter
	private ConfigManager configManager;
	@Getter
	private TerminatorManager terminatorManager;
	@Getter
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
		Bukkit.getPluginManager().registerEvents(terminatorManager = new TerminatorManager(), this);
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("ban").setExecutor(new BanCmd());
		getCommand("kick").setExecutor(new KickCmd());
		getCommand("mute").setExecutor(new MuteCmd());
		getCommand("unmute").setExecutor(new UnMuteCmd());
		getCommand("unban").setExecutor(new UnbanCmd());
		getCommand("hist").setExecutor(new HistCmd());
		getCommand("bypass").setExecutor(new BypassCmd());
	}

	@Override
	public void onDisable() {
		if (uuidTask != null)
			uuidTask.cancel();
		UUIDs.save();
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		UUIDs.register(p.getUniqueId(), p.getName());
	}

	public static Terminator get() {
		return TERMINATOR;
	}

	public TerminatorManager getTerminatorManager() {
		// TODO Auto-generated method stub
		return terminatorManager;
	}
}
