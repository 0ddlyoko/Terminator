package me.oddlyoko.terminator.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;

public class HistCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("hist".equalsIgnoreCase(command.getName())) {
			if (!(sender.hasPermission("terminator.hist")))
				sender.sendMessage("You don't have permission to use this command");
			if (!(sender instanceof Player))
				return true;
			Player p = (Player) sender;
			Terminator.get().getTerminatorManager().openMainInventory(p);
		}
		return false;
	}
}
