package me.oddlyoko.terminator;

import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import me.oddlyoko.bans.Ban;
import me.oddlyoko.bans.BanManager;



public class Terminator extends JavaPlugin {
	public HashMap<String, Ban> ban_list = new HashMap<>();
	

	public static Terminator TERMINATOR;

	public Terminator() {
		if (TERMINATOR != null)
			throw new IllegalStateException("Cannot redefine a Singleton");
		TERMINATOR = this;
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BanManager(this), this);
		getCommand("ban").setExecutor(new Commands(this));
	}

	@Override
	public void onDisable() {
	}

	public static Terminator get() {
		return TERMINATOR;
	}
	public HashMap<String, Ban> getBan_list() {
		return ban_list;
	}

	public void setBan_list(HashMap<String, Ban> ban_list) {
		this.ban_list = ban_list;
	}
}
