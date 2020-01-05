package me.oddlyoko.terminator.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.__;

public class BypassCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("bypass".equalsIgnoreCase(command.getName())) {
			// /bypass <add|remove> <pseudo>
			if (!sender.hasPermission("terminator.bypass")) {
				sender.sendMessage(error("You don't have permission to use this command"));
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(syntax("/bypass <add|remove> <pseudo>"));
				return true;
			}
			String pseudo = args[1];
			UUID playerUuid = UUIDs.get(pseudo);
			if (playerUuid == null) {
				// Player not found
				sender.sendMessage(error("Player " + pseudo + " hasn't been found"));
				return true;
			}
			if ("add".equalsIgnoreCase(args[0])) {
				// Add
				Terminator.get().getTerminatorManager().addBypass(playerUuid);
				sender.sendMessage(__.PREFIX + ChatColor.GREEN + pseudo + " is now bypassable");
			} else if ("remove".equalsIgnoreCase(args[0])) {
				// Remove
				Terminator.get().getTerminatorManager().removeBypass(playerUuid);
				sender.sendMessage(__.PREFIX + ChatColor.GREEN + pseudo + " is not longer bypassable");
			} else {
				sender.sendMessage(syntax("/bypass <add|remove> <pseudo>"));
			}
			return true;
		}
		return false;
	}
}
