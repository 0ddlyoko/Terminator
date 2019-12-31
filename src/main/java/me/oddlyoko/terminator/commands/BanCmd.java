package me.oddlyoko.terminator.commands;

import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;

public class BanCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("ban".equalsIgnoreCase(command.getName())) {
			// /ban <pseudo> <time> <reason>
			if (!sender.hasPermission("terminator.ban")) {
				sender.sendMessage(error("You don't have permission to use this command"));
				return true;
			}
			if (args.length != 3) {
				sender.sendMessage(syntax("/ban <pseudo> <time> <reason>"));
				return true;
			}
			String pseudo = args[0];
			String strTime = args[1];
			int time = 0;
			try {
				time = Integer.parseInt(strTime);
			} catch (Exception ex) {
				time = textToInt(strTime);
			}
			if (time == -1) {
				sender.sendMessage(error("Cannot parse " + strTime + " to a correct time"));
				return false;
			}
			if (time < 0) {
				sender.sendMessage(error("Time must be greater or equals to 0"));
				return false;
			}
			String reason = args[2];
			Player p = Bukkit.getPlayer(pseudo);
			UUID playerUuid = p != null ? p.getUniqueId() : UUIDs.get(pseudo);
			if (playerUuid == null) {
				// Player not found
				sender.sendMessage(error("Player " + pseudo + " hasn't been found"));
				return true;
			}
			if (Terminator.get().getBanManager().isBanned(playerUuid)) {
				// Already banned
				sender.sendMessage(error("Player " + pseudo + " is already banned"));
				return true;
			}
			UUID player2Uuid = (sender instanceof Player) ? ((Player) sender).getUniqueId() : null;
			Calendar now = Calendar.getInstance();
			now.add(Calendar.SECOND, time);
			Terminator.get().getBanManager().addBan(playerUuid, player2Uuid, reason, time == 0 ? null : now.getTime());
		}
		return false;
	}

	private int textToInt(String str) {
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
