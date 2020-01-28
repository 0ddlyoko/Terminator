package me.oddlyoko.terminator;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import me.oddlyoko.terminator.commands.BanCmd;
import me.oddlyoko.terminator.commands.BanIpCmd;
import me.oddlyoko.terminator.commands.BypassCmd;
import me.oddlyoko.terminator.commands.HistCmd;
import me.oddlyoko.terminator.commands.KickCmd;
import me.oddlyoko.terminator.commands.MuteCmd;
import me.oddlyoko.terminator.commands.UnMuteCmd;
import me.oddlyoko.terminator.commands.UnbanCmd;
import me.oddlyoko.terminator.commands.UnbanIpCmd;
import me.oddlyoko.terminator.config.ConfigManager;
import me.oddlyoko.terminator.config.MessageManager;
import me.oddlyoko.terminator.config.PlayerConfigManager;
import me.oddlyoko.terminator.database.DatabaseManager;
import me.oddlyoko.terminator.database.DatabaseModel;
import me.oddlyoko.terminator.inventories.InventoryManager;
import me.oddlyoko.terminator.terminator.TerminatorManager;

public class Terminator extends JavaPlugin implements Listener {
	public static Terminator TERMINATOR;
	@Getter
	private DatabaseManager databaseManager;
	@Getter
	private ConfigManager configManager;
	@Getter
	private PlayerConfigManager playerConfigManager;
	@Getter
	private TerminatorManager terminatorManager;
	@Getter
	private InventoryManager inventorymanager;
	@Getter
	private DatabaseModel databaseModel;
	private BukkitTask uuidTask;

	public Terminator() {
		if (TERMINATOR != null)
			throw new IllegalStateException("Cannot redefine a Singleton");
		TERMINATOR = this;
	}

	@Override
	public void onEnable() {
		Bukkit.getLogger().info("Loading Terminator ...");
		// Add filter
		Bukkit.getLogger().setFilter(new Filter() {

			@Override
			public boolean isLoggable(LogRecord record) {
				// Remove BIG message
				// I know this is not an elegant way to remove it but it works ...
				return record.getMessage().indexOf("protocolsupport.protocol.utils.authlib.GameProfile") == -1;
			}
		});
		saveDefaultConfig();
		configManager = new ConfigManager();
		try {
			databaseModel = new DatabaseModel(configManager.getHost(), configManager.getPort(),
					configManager.getDbName(), configManager.getUser(), configManager.getPassword());
		} catch (ClassNotFoundException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "ClassNotFound for database connection: ", ex);
			Bukkit.shutdown();
		}
		MessageManager.load();
		inventorymanager = new InventoryManager();
		inventorymanager.init();
		UUIDs.load();
		for (Player p : Bukkit.getOnlinePlayers())
			UUIDs.register(p.getUniqueId(), p.getName());
		uuidTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			// Save uuids each hours
			UUIDs.save();
		}, 1, 3600);
		databaseManager = new DatabaseManager();
		playerConfigManager = new PlayerConfigManager();
		Bukkit.getPluginManager().registerEvents(terminatorManager = new TerminatorManager(), this);
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("ban").setExecutor(new BanCmd());
		getCommand("unban").setExecutor(new UnbanCmd());
		getCommand("banip").setExecutor(new BanIpCmd());
		getCommand("unbanip").setExecutor(new UnbanIpCmd());
		getCommand("kick").setExecutor(new KickCmd());
		getCommand("mute").setExecutor(new MuteCmd());
		getCommand("unmute").setExecutor(new UnMuteCmd());
		getCommand("hist").setExecutor(new HistCmd());
		getCommand("bypass").setExecutor(new BypassCmd());
		Bukkit.getLogger().info("Terminator loaded");
	}

	@Override
	public void onDisable() {
		Bukkit.getLogger().setFilter(null);
		if (uuidTask != null)
			uuidTask.cancel();
		UUIDs.save();
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		UUIDs.register(p.getUniqueId(), p.getName());
		playerConfigManager.savePlayer(p.getUniqueId(), p.getName(), e.getAddress().getHostAddress());
	}

	public static Terminator get() {
		return TERMINATOR;
	}
}
