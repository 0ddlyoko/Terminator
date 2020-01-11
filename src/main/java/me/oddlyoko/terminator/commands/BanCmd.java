package me.oddlyoko.terminator.commands;

import java.util.Calendar;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.inventories.inv.BanReasonInventory;
import me.oddlyoko.terminator.terminator.Ban;

public class BanCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("ban".equalsIgnoreCase(command.getName())) {
			// /ban <pseudo> <time> <reason>
			if (!sender.hasPermission("terminator.ban")) {
				sender.sendMessage(error("You don't have permission to use this command"));
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(syntax("/ban <pseudo> <time> <reason>"));
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
				sender.sendMessage(error("Cannot parse " + strTime + " to a correct time"));
				return false;
			}
			if (time < 0) {
				sender.sendMessage(error("Time must be greater or equals to 0"));
				return false;
			}
			UUID playerUuid = UUIDs.get(pseudo);
			if (playerUuid == null) {
				// Player not found
				sender.sendMessage(error("Player " + pseudo + " hasn't been found"));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isBanned(playerUuid)) {
				// Already banned
				sender.sendMessage(error("Player " + pseudo + " is already banned"));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isBypass(playerUuid)) {
				sender.sendMessage(error("Cannot ban " + pseudo));
				return true;
			}
			UUID player2Uuid = (sender instanceof Player) ? ((Player) sender).getUniqueId() : null;
			Calendar now = Calendar.getInstance();
			now.add(Calendar.SECOND, time);
			String StringReason = "";
			if(args.length>=3) {
				//The player speciefied a reason
				StringBuilder reason = new StringBuilder();
				for (int i = 2; i < args.length; i++)
					reason.append(args[i]).append(" ");
				// Remove last space
				reason.setLength(reason.length() - 1);
				StringReason = reason.toString();
			}
			System.out.println("AA" +StringReason);
			if(StringReason.equals("") && sender instanceof Player) {
				Ban ban =  new Ban(playerUuid, player2Uuid, StringReason,time==0?null: now.getTime());
				Terminator.get().getInventorymanager().openInventory(new BanReasonInventory(), (Player)sender , i->{
					i.put(BanReasonInventory.BAN,ban);
				});
			}else {
				Terminator.get().getTerminatorManager().ban(playerUuid, player2Uuid, StringReason,
						time == 0 ? null : now.getTime());
			}
		}
		return false;
	}
}
