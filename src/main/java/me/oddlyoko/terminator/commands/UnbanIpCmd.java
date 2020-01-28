package me.oddlyoko.terminator.commands;

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

public class UnbanIpCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("unbanip".equalsIgnoreCase(command.getName())) {
			// /unbanip <ip> <reason>
			if (!sender.hasPermission("terminator.unbanip")) {
				sender.sendMessage(error(MessageManager.get("commands.perm")));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isLoading()) {
				sender.sendMessage(error(MessageManager.get("commands.notloaded")));
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(syntax(MessageManager.get("commands.unbanip.syntax")));
				return true;
			}
			String ip = args[0];

			StringBuilder reason = new StringBuilder();
			for (int i = 1; i < args.length; i++)
				reason.append(args[i]).append(" ");
			// Remove last space
			reason.setLength(reason.length() - 1);

			if (!isCorrectIp(ip)) {
				String pseudo = ip;
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
								error(MessageManager.get("commands.unbanip.ipNotFound").replace("{player}", pseudo)));
						return true;
					}
					ip = ips.get(ips.size() - 1);
				}
			}
			if (!Terminator.get().getTerminatorManager().isBannedIp(ip)) {
				sender.sendMessage(error(MessageManager.get("commands.unbanip.ip").replace("{ip}", ip)));
				return true;
			}
			Terminator.get().getTerminatorManager().unbanIp(ip, reason.toString(),
					(sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
			return true;
		}
		return false;
	}
}
