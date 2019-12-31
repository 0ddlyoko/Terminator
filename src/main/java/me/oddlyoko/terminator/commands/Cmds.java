package me.oddlyoko.terminator.commands;

import org.bukkit.ChatColor;

import me.oddlyoko.terminator.__;

public class Cmds {

	protected String syntax(String syntax) {
		return error("Syntax: " + syntax);
	}

	protected String error(String error) {
		return __.PREFIX + ChatColor.RED + error;
	}
}
