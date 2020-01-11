package me.oddlyoko.terminator.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.inventories.inv.KickReasonInventory;
import me.oddlyoko.terminator.inventories.inv.MuteReasonInventory;
import me.oddlyoko.terminator.terminator.Kick;
import me.oddlyoko.terminator.terminator.Mute;

public class KickCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("kick".equalsIgnoreCase(command.getName())) {
			// /unban <pseudo> <reason>
			if (!sender.hasPermission("terminator.kick")) {
				sender.sendMessage(error("You don't have permission to use this command"));
				return true;
			}
			if (args.length < 1) {
				sender.sendMessage(syntax("/kick <pseudo> <reason>"));
				return true;
			}
			String pseudo = args[0];
			Player player = Bukkit.getPlayer(pseudo);
			if (player == null || !player.isOnline()) {
				sender.sendMessage(error("Player " + pseudo + " is not connected"));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isBypass(player.getUniqueId())) {
				sender.sendMessage(error("Cannot kick " + pseudo));
				return true;
			}
			UUID player2Uuid = (sender instanceof Player) ? ((Player) sender).getUniqueId() : null;
			StringBuilder reason = new StringBuilder();
			String StringReason = "";
			if(args.length>=2) {
				//The player speciefied a reason
				for (int i = 1; i < args.length; i++)
					reason.append(args[i]).append(" ");
				// Remove last space
				reason.setLength(reason.length() - 1);
				StringReason = reason.toString();
			}
			if(StringReason.equals("") && sender instanceof Player) {
				Kick kick=  new Kick(player.getUniqueId(), player2Uuid, StringReason);
				Terminator.get().getInventorymanager().openInventory(new KickReasonInventory(), (Player)sender , i->{
					i.put(KickReasonInventory.KICK , kick);
				});
			}else {
				Terminator.get().getTerminatorManager().kick(player.getUniqueId(), player2Uuid, StringReason
				);
			}
		
		}
		return false;
	}
}
