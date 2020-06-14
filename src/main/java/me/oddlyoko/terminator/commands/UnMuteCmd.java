package me.oddlyoko.terminator.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.config.MessageManager;

public class UnMuteCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("unmute".equalsIgnoreCase(command.getName())) {
			// /unban <pseudo> <reason>
			if (!sender.hasPermission("terminator.unmute")) {
				sender.sendMessage(error(MessageManager.get("commands.perm")));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isLoading()) {
				sender.sendMessage(error(MessageManager.get("commands.notloaded")));
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(syntax(MessageManager.get("commands.unmute.syntax")));
				return true;
			}
			String pseudo = args[0];

			StringBuilder reason = new StringBuilder();
			for (int i = 1; i < args.length; i++)
				reason.append(args[i]).append(" ");
			// Remove last space
			reason.setLength(reason.length() - 1);

			UUID uuid = UUIDs.get(pseudo);
			if (uuid == null) {
				sender.sendMessage(error(MessageManager.get("commands.playerNotFound").replace("{player}", pseudo)));
				return true;
			}
			if (!Terminator.get().getTerminatorManager().isMuted(uuid)) {
				sender.sendMessage(error(MessageManager.get("commands.unmute.notMuted").replace("{player}", pseudo)));
				return true;
			}
			Terminator.get().getTerminatorManager().unmute(uuid, reason.toString(),
					(sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
		}
		return false;
	}
}
