package me.oddlyoko.terminator.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;

public class UnbanCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("unban".equalsIgnoreCase(command.getName())) {
			// /unban <pseudo> <reason>
			if (!sender.hasPermission("terminator.unban")) {
				sender.sendMessage(error("You don't have permission to use this command"));
				return true;
			}
			if (args.length != 2) {
				sender.sendMessage(syntax("/unban <pseudo> <reason>"));
				return true;
			}
			String pseudo = args[0];
			String reason = args[1];
			UUID uuid = UUIDs.get(pseudo);
			if (uuid == null) {
				sender.sendMessage(error("Player " + pseudo + " hasn't been found"));
				return true;
			}
			if (!Terminator.get().getBanManager().isBanned(uuid)) {
				sender.sendMessage(error("Player " + pseudo + " isn't banned"));
				return true;
			}
			Terminator.get().getBanManager().removeBan(uuid, reason,
					(sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
		}
		return false;
	}
}
