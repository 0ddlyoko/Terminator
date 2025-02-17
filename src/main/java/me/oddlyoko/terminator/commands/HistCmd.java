package me.oddlyoko.terminator.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.config.MessageManager;
import me.oddlyoko.terminator.terminator.TerminatorPlayer;

public class HistCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("hist".equalsIgnoreCase(command.getName())) {
			if (!sender.hasPermission("terminator.hist")) {
				sender.sendMessage(error(MessageManager.get("commands.perm")));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isLoading()) {
				sender.sendMessage(error(MessageManager.get("commands.notloaded")));
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(error(MessageManager.get("commands.notAPlayer")));
				return true;
			}
			Player p = (Player) sender;
			TerminatorPlayer player = null;
			if (args.length == 1) {
				UUID uuid = UUIDs.get(args[0]);
				if (uuid == null) {
					sender.sendMessage(
							error(MessageManager.get("commands.playerNotFound").replace("{player}", args[0])));
					return true;
				}
				player = Terminator.get().getTerminatorManager().getPlayer(uuid);
			}
			Terminator.get().getTerminatorManager().openMainInventory(p, player);
		}
		return false;
	}
}
