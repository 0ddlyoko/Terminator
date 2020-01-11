package me.oddlyoko.terminator.commands;

import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.inventories.inv.BanReasonInventory;
import me.oddlyoko.terminator.inventories.inv.MuteReasonInventory;
import me.oddlyoko.terminator.terminator.Ban;
import me.oddlyoko.terminator.terminator.Mute;

public class MuteCmd extends Cmds implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("mute".equalsIgnoreCase(command.getName())) {
			// mute <pseudo> <time> <reason>
			if (!sender.hasPermission("terminator.mute")) {
				sender.sendMessage(error("You don't have permission to use this command"));
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(syntax("/mute <pseudo> <time> <reason>"));
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
			Player p = Bukkit.getPlayer(pseudo);
			UUID playerUuid = p != null ? p.getUniqueId() : UUIDs.get(pseudo);
			if (playerUuid == null) {
				// Player not found
				sender.sendMessage(error("Player " + pseudo + " hasn't been found"));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isMuted(playerUuid)) {
				// Already muted
				sender.sendMessage(error("Player " + pseudo + " is already muted"));
				return true;
			}
			if (Terminator.get().getTerminatorManager().isBypass(playerUuid)) {
				sender.sendMessage(error("Cannot mute " + pseudo));
				return true;
			}
			UUID player2Uuid = (sender instanceof Player) ? ((Player) sender).getUniqueId() : null;
			Calendar now = Calendar.getInstance();
			now.add(Calendar.SECOND, time);
			StringBuilder reason = new StringBuilder();
			String StringReason = "";
			if(args.length>=3) {
				//The player speciefied a reason
				for (int i = 2; i < args.length; i++)
					reason.append(args[i]).append(" ");
				// Remove last space
				reason.setLength(reason.length() - 1);
				StringReason = reason.toString();
			}
			if(StringReason.equals("") && sender instanceof Player) {
				Mute mute=  new Mute(playerUuid, player2Uuid, StringReason,time==0?null: now.getTime());
				Terminator.get().getInventorymanager().openInventory(new MuteReasonInventory(), (Player)sender , i->{
					i.put(MuteReasonInventory.MUTE , mute);
				});
			}else {
				Terminator.get().getTerminatorManager().mute(playerUuid, player2Uuid, StringReason,
						time == 0 ? null : now.getTime());
			}
			
	
		}
		return false;
	}
}
