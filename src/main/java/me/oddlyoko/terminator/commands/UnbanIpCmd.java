package me.oddlyoko.terminator.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;

public class UnbanIpCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("unbanip".equalsIgnoreCase(command.getName())) {
			// /unbanip <ip> <reason>
			if (!sender.hasPermission("terminator.unbanip")) {
				sender.sendMessage(error("You don't have permission to use this command"));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isLoading()) {
				sender.sendMessage(error("Please wait until plugin is fully loaded"));
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(syntax("/unbanip <ip> <reason>"));
				return true;
			}
			String ip = args[0];

			StringBuilder reason = new StringBuilder();
			for (int i = 1; i < args.length; i++)
				reason.append(args[i]).append(" ");
			// Remove last space
			reason.setLength(reason.length() - 1);

			if (!isCorrectIp(ip)) {
				sender.sendMessage(error("Ip " + ip + " hasn't been found"));
				return true;
			}
			if (!Terminator.get().getTerminatorManager().isBannedIp(ip)) {
				sender.sendMessage(error("Ip " + ip + " isn't banned"));
				return true;
			}
			Terminator.get().getTerminatorManager().unbanIp(ip, reason.toString(),
					(sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
			return true;
		}
		return false;
	}
}
