package me.oddlyoko.terminator.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.config.MessageManager;

public class KickCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("kick".equalsIgnoreCase(command.getName())) {
			// /unban <pseudo> <reason>
			if (!sender.hasPermission("terminator.kick")) {
				sender.sendMessage(error(MessageManager.get("commands.perm")));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isLoading()) {
				sender.sendMessage(error(MessageManager.get("commands.notloaded")));
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(syntax(MessageManager.get("commands.kick.syntax")));
				return true;
			}
			String pseudo = args[0];
			StringBuilder reason = new StringBuilder();
			for (int i = 1; i < args.length; i++)
				reason.append(args[i]).append(" ");
			// Remove last space
			reason.setLength(reason.length() - 1);
			Player player = Bukkit.getPlayer(pseudo);
			if (player == null || !player.isOnline()) {
				sender.sendMessage(
						error(MessageManager.get("commands.playerNotConnected").replace("{player}", pseudo)));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isBypass(player.getUniqueId())) {
				sender.sendMessage(error(MessageManager.get("commands.kick.bypass").replace("{player}", pseudo)));
				return true;
			}
			UUID player2Uuid = (sender instanceof Player) ? ((Player) sender).getUniqueId() : null;
			Terminator.get().getTerminatorManager().kick(player.getUniqueId(), player2Uuid, reason.toString());
		}
		return false;
	}
}
