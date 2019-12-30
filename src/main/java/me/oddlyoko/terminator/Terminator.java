package me.oddlyoko.terminator;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.oddlyoko.terminator.database.DatabaseManager;

public class Terminator extends JavaPlugin {
	public static Terminator TERMINATOR;
	@Getter
	private DatabaseManager databaseManager;

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
