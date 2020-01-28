package me.oddlyoko.terminator.commands;

import java.util.Calendar;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.config.MessageManager;

public class BanCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("ban".equalsIgnoreCase(command.getName())) {
			// /ban <pseudo> <time> <reason>
			if (!sender.hasPermission("terminator.ban")) {
				sender.sendMessage(error(MessageManager.get("commands.perm")));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isLoading()) {
				sender.sendMessage(error(MessageManager.get("commands.notloaded")));
				return true;
			}
			if (args.length < 3) {
				sender.sendMessage(syntax(MessageManager.get("commands.ban.syntax")));
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
				sender.sendMessage(error(MessageManager.get("commands.ban.parse").replace("{time}", strTime)));
				return false;
			}
			if (time < 0) {
				sender.sendMessage(error(MessageManager.get("commands.ban.outofrange")));
				return false;
			}
			StringBuilder reason = new StringBuilder();
			for (int i = 2; i < args.length; i++)
				reason.append(args[i]).append(" ");
			// Remove last space
			reason.setLength(reason.length() - 1);

			UUID playerUuid = UUIDs.get(pseudo);
			if (playerUuid == null) {
				// Player not found
				sender.sendMessage(error(MessageManager.get("commands.playerNotFound").replace("{player}", pseudo)));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isBanned(playerUuid)) {
				// Already banned
				sender.sendMessage(error(MessageManager.get("commands.ban.alreadyBanned").replace("{player}", pseudo)));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isBypass(playerUuid)) {
				sender.sendMessage(error(MessageManager.get("commands.ban.bypass").replace("{player}", pseudo)));
				return true;
			}
			UUID player2Uuid = (sender instanceof Player) ? ((Player) sender).getUniqueId() : null;
			Calendar now = Calendar.getInstance();
			now.add(Calendar.SECOND, time);
			Terminator.get().getTerminatorManager().ban(playerUuid, player2Uuid, reason.toString(),
					time == 0 ? null : now.getTime());
		}
		return false;
	}
}
