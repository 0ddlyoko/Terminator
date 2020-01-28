package me.oddlyoko.terminator.commands;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.config.MessageManager;

public class BanIpCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("banip".equalsIgnoreCase(command.getName())) {
			// /banip <pseudo|ip> <time> <reason>
			if (!sender.hasPermission("terminator.banip")) {
				sender.sendMessage(error(MessageManager.get("commands.perm")));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isLoading()) {
				sender.sendMessage(error(MessageManager.get("commands.notloaded")));
				return true;
			}
			if (args.length < 3) {
				sender.sendMessage(syntax(MessageManager.get("commands.banip.syntax")));
				return true;
			}
			String ip = args[0];
			String strTime = args[1];
			int time = 0;
			try {
				time = Integer.parseInt(strTime);
			} catch (Exception ex) {
				time = textToInt(strTime);
			}
			if (time == -1) {
				sender.sendMessage(error(MessageManager.get("commands.banip.parse").replace("{time}", strTime)));
				return false;
			}
			if (time < 0) {
				sender.sendMessage(error(MessageManager.get("commands.banip.outofrange")));
				return false;
			}
			StringBuilder reason = new StringBuilder();
			for (int i = 2; i < args.length; i++)
				reason.append(args[i]).append(" ");
			// Remove last space
			reason.setLength(reason.length() - 1);

			// Save ip as pseudo
			String pseudo = null;
			if (!isCorrectIp(ip)) {
				pseudo = ip;
				// Not correct ip, let's check if it is the name of a player
				UUID uuid = UUIDs.get(pseudo);
				if (uuid == null) {
					sender.sendMessage(
							error(MessageManager.get("commands.playerNotFound").replace("{player}", pseudo)));
					return true;
				}
				// Here we've enter the name of a player, check his ip
				Player p = Bukkit.getPlayer(uuid);
				if (p != null)
					ip = p.getAddress().getAddress().getHostAddress();
				else {
					// The player isn't online, check his last ip
					List<String> ips = Terminator.get().getPlayerConfigManager().getIps(uuid);
					if (ips.isEmpty()) {
						sender.sendMessage(
								error(MessageManager.get("commands.banip.ipNotFound").replace("{player}", pseudo)));
						return true;
					}
					ip = ips.get(ips.size() - 1);
				}
			}
			if (Terminator.get().getTerminatorManager().isBannedIp(ip)) {
				// Already banned
				sender.sendMessage(error(MessageManager.get("commands.banip.alreadyBanned").replace("{ip}", ip)));
				return true;
			}
			UUID player2Uuid = (sender instanceof Player) ? ((Player) sender).getUniqueId() : null;
			Calendar now = Calendar.getInstance();
			now.add(Calendar.SECOND, time);
			Terminator.get().getTerminatorManager().banIp(ip, pseudo, player2Uuid, reason.toString(),
					time == 0 ? null : now.getTime());
			return true;
		}
		return false;
	}
}
