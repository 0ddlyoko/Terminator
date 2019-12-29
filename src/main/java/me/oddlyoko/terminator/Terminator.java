package me.oddlyoko.terminator;

import org.bukkit.plugin.java.JavaPlugin;

public class Terminator extends JavaPlugin {
	public static Terminator TERMINATOR;

	public Terminator() {
		if (TERMINATOR != null)
			throw new IllegalStateException("Cannot redefine a Singleton");
		TERMINATOR = this;
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	public static Terminator get() {
		return TERMINATOR;
	}
}
