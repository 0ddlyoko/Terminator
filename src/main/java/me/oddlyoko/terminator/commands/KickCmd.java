package me.oddlyoko.terminator.commands;

import java.util.UUID;
import javax.sound.midi.SysexMessage;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.kicks.Kick;

public class KickCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("kick".equalsIgnoreCase(command.getName())) {
			// /unban <pseudo> <reason>
			if (!sender.hasPermission("terminator.unmute")) {
				sender.sendMessage(error("You don't have permission to use this command"));
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(syntax("/kick <pseudo> <reason>"));
				return true;
			}
			String pseudo = args[0];
			String reason = "";
			for (int i = 1; i < args.length; i++) {
				reason = reason + args[i] + " ";
			}
			Player player = Bukkit.getPlayer(pseudo);
			if (player == null) {
				sender.sendMessage(error("Player " + pseudo + " hasn't been found"));
				return true;
			}
			UUID player2Uuid = (sender instanceof Player) ? ((Player) sender).getUniqueId() : null;
			Terminator.get().getBanManager().addKick(player.getUniqueId(), player2Uuid, reason);

		}
		return false;
	}
}
