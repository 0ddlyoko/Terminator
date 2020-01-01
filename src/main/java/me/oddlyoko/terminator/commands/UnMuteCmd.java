package me.oddlyoko.terminator.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;

public class UnMuteCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("unmute".equalsIgnoreCase(command.getName())) {
			// /unban <pseudo> <reason>
			if (!sender.hasPermission("terminator.unmute")) {
				sender.sendMessage(error("You don't have permission to use this command"));
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(syntax("/unmute <pseudo> <reason>"));
				return true;
			}
			String pseudo = args[0];
			String reason = "";
			for(int i = 1 ; i<args.length ; i++) {
				reason = reason + args[i]+" ";
			}
			UUID uuid = UUIDs.get(pseudo);
			if (uuid == null) {
				sender.sendMessage(error("Player " + pseudo + " hasn't been found"));
				return true;
			}
			if (!Terminator.get().getMuteManager().isMuted(uuid)) {
				sender.sendMessage(error("Player " + pseudo + " isn't muted"));
				return true;
			}
			Terminator.get().getMuteManager().removeMute(uuid, reason,
					(sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
		}
		return false;
	}
}
