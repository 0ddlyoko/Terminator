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

	protected int textToInt(String str) {
		char[] cars = str.toCharArray();
		int totalSecs = 0;
		int current = 0;
		for (int i = 0; i < cars.length; i++) {
			char c = cars[i];
			// y M d h m s
			if (c >= '0' && c <= '9') {
				current *= 10;
				current += (c - '0');
			} else {
				switch (c) {
				case 'y':
					totalSecs += (current * 31536000);
					current = 0;
					break;
				case 'M':
					totalSecs += (current * 2592000);
					current = 0;
					break;
				case 'd':
					totalSecs += (current * 86400);
					current = 0;
					break;
				case 'h':
					totalSecs += (current * 3600);
					current = 0;
					break;
				case 'm':
					totalSecs += (current * 60);
					current = 0;
					break;
				case 's':
					totalSecs += current;
					current = 0;
					break;
				default:
					return -1;
				}
			}
		}
		return totalSecs;
	}
}
