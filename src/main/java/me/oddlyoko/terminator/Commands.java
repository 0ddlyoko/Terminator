package me.oddlyoko.terminator;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.oddlyoko.bans.Ban;

public class Commands implements CommandExecutor {
	public Terminator main;
	 public Commands(Terminator main) {
	this.main = main;	
	}
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String cmd, String[] args) {
		//ban <pseudo>  <durée> <raison>
		if(sender.hasPermission("terminator.ban")) {
			if(args.length ==0) {
				sender.sendMessage("§cSyntaxe : §7/ban <joueur> <durée> <raison>");
				return false;
			}
			OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
			if(player == null) {
				sender.sendMessage("§7Le joueur n'éxiste pas");
				return false;
			}
			Ban ban = new Ban( player.getName(), player.getUniqueId().toString(),main.getBan_list().size()+1 );
			ban.setDate(new Date());
			/////////////RAISON
		if(args.length >= 0) {	
			if(args.length>=3) {
				ban.setReason(args[2]);
			}
	 /////////////////DUREEE
			if(args.length >=2) {
				String duration =args[1];
				if(args[1].charAt(duration.length()-1)== 'j') {
					duration =duration.replace("j", "");
					if(isInteger(duration)) {
						int duration_int = Integer.parseInt(duration);
						Date exp = new Date();
						exp.setDate(ban.getDate().getDay()+duration_int);
						ban.setExp_date(exp);
					}else {	
						sender.sendMessage(__.PREFIX+ " §7Veuillez entrer une durée valide");
						return false;
					}
				}else
				if(args[1].charAt(duration.length()-1)== 'm') {
					duration = duration.replace("m", "");
					if(isInteger(duration)) {
						int duration_int = Integer.parseInt(duration);
						Date exp = new Date();
						exp.setMinutes(ban.getDate().getMinutes()+duration_int);
						ban.setExp_date(exp);
					}else {
						sender.sendMessage(__.PREFIX+" §7Veuillez entrer une durée valide");
						return false;
					}
				}else		
				if(args[1].charAt(duration.length()-1)== 'h') {
					duration = duration.replace("h", "");
					if(isInteger(duration)) {
						int duration_int = Integer.parseInt(duration);
						Date exp = new Date();
						exp.setHours(ban.getDate().getHours()+duration_int);
						ban.setExp_date(exp);
					}else {
						sender.sendMessage(__.PREFIX +" §7Veuillez entrer une durée valide");
						return false;
					}
				}else
				if(args[1].charAt(duration.length()-1)== 's') {
					duration =duration.replace("s", "");
					if(isInteger(duration)) {
						int duration_int = Integer.parseInt(duration);
						Date exp = new Date();
						exp.setSeconds(ban.getDate().getSeconds()+duration_int);
						ban.setExp_date(exp);
					}else {
						sender.sendMessage(__.PREFIX+" §7Veuillez entrer une durée valide");
						return false;
					}
				}else {
					sender.sendMessage(__.PREFIX +" §7Veuillez entrer une durée valide !");
					return false;
				}
			}
		}
				main.getBan_list().put(player.getUniqueId().toString(), ban);
				if(ban.getReason() ==null) {
					Bukkit.broadcastMessage(__.PREFIX+" §e" + ban.getStaff_name() + " §cVient de bannir " + "§f"+ban.getPlayer_name()
					+ " §cpendant §f" + ban.getDurationMessage());
				}else {
					Bukkit.broadcastMessage(__.PREFIX+ " §e" + ban.getStaff_name() + " §cVient de bannir " + "§f"+ban.getPlayer_name()
					+ " §cpendant §f " + ban.getDurationMessage() + " §cpour §f " + ban.getReason());
				}
				
			
			
		if(args.length == 0)
			sender.sendMessage(__.PREFIX +" §7Veuillez spécifier le nom d'un joueur");
		}
		return false;
	
	}
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
}
