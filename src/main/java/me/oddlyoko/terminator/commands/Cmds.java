package me.oddlyoko.terminator.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
				case 'a':
					totalSecs += (current * 31536000);
					current = 0;
					break;
				case 'M':
					totalSecs += (current * 2592000);
					current = 0;
					break;
				case 'd':
				case 'j':
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

	private static final String IPv4_REGEX = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
			+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
			+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	private static final Pattern IPv4_PATTERN = Pattern.compile(IPv4_REGEX);

	public static boolean isCorrectIp(String ip) {
		if (ip == null)
			return false;
		Matcher matcher = IPv4_PATTERN.matcher(ip);

		return matcher.matches();
	}
}
