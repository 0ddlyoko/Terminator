package me.oddlyoko.terminator.mutes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.oddlyoko.terminator.DateComparator;
import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.__;
import me.oddlyoko.terminator.bans.Ban;

public class MuteManager implements Listener {
	private HashMap<UUID, List<Mute>> mutes;
	private HashMap<UUID, Mute> currentMutes;

	public MuteManager() {
		mutes = new HashMap<>();
		currentMutes = new HashMap<>();
		// TODO load players that are currently banned
		for (Player p : Bukkit.getOnlinePlayers()) {
			// TODO load bans
		}
	}

	public void addMute(UUID mutedUuid, UUID muterUuid, String reason, Date expiration) {
		checkMute(mutedUuid);
		Mute mute = new Mute(mutedUuid, muterUuid, reason, expiration);
		mute.setCreationDate(new Date());
		CommandSender sender;
		if (muterUuid != null)
			sender = Bukkit.getPlayer(muterUuid);
		else
			sender = Bukkit.getConsoleSender();
		// Check if player is already banned
		List<Mute> mutes = this.mutes.get(muterUuid);
		if (mutes == null)
			mutes = new ArrayList<>();
		mutes.add(mute);
		this.mutes.put(mutedUuid, mutes);
		this.currentMutes.put(muterUuid, mute);
		Player p = Bukkit.getPlayer(mutedUuid);
		String msg = __.PREFIX + ChatColor.AQUA + UUIDs.get(mutedUuid) + ChatColor.GREEN + " has been muted by "
				+ ChatColor.AQUA + (mutedUuid == null ? "CONSOLE" : UUIDs.get(mutedUuid)) + ChatColor.GREEN + " until "
				+ ChatColor.AQUA
				+ (expiration == null ? "never" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(expiration));
		Bukkit.broadcast(msg, "terminator.mute.see");
		// TODO Save in database
	}

	public void removeMute(UUID mutedUuid, String deleteReason, UUID deletePlayer) {
		checkMute(mutedUuid);
		CommandSender sender;
		if (deletePlayer != null)
			sender = Bukkit.getPlayer(deletePlayer);
		else
			sender = Bukkit.getConsoleSender();
		Mute mute = currentMutes.get(mutedUuid);
		mute.setDeleted(true);
		mute.setDeletePlayer(deletePlayer);
		mute.setDeleteReason(deleteReason);
		currentMutes.remove(mutedUuid);
		sender.sendMessage(__.PREFIX + ChatColor.AQUA + UUIDs.get(mutedUuid) + ChatColor.GREEN + " is no longer muted");
	}

	public boolean isMuted(UUID uuid) {
		checkMute(uuid);
		return currentMutes.containsKey(uuid);
	}

	private void checkMute(UUID uuid) {
		Mute currentMute = currentMutes.get(uuid);
		if (currentMute != null) {
			// Player was muted, check if mute is expired
			if (currentMute.isDeleted()
					|| (currentMute.getExpiration() != null && new Date().after(currentMute.getExpiration())))
				// mute was deleted or expired
				currentMutes.remove(uuid);
		}
	}
	public List<Mute> getSortedMuteHistory(){
		//Get all bans and sort them in a List
		//Get all bans
		List<Mute> allmutes = new ArrayList<>();

		for(Entry<UUID, List<Mute>> 	entry : mutes.entrySet()) {
			for(int i =0; i<entry.getValue().size() ; i++) {
				allmutes.add(entry.getValue().get(i));
			}
		}
		//Sort all bans
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		DateComparator dc = new DateComparator("dd-MM-yyyy HH:mm:ss");
		HashMap<String, Mute> mixedMap = new HashMap<>();
		List<String> allDates = new ArrayList<>();
		for(int i = 0 ; i<allmutes.size() ; i++) {
			Mute mute = allmutes.get(i);
			if(mute!=null) {
				System.out.println("aa" + mute.getCreationDate());
				allDates.add(format.format(mute.getCreationDate()));
				mixedMap.put(format.format(mute.getCreationDate()), mute);
			}
		}
        Collections.sort(allDates , dc);
        //Preparing HashMap Ban&date
        List<Mute> SortMute = new ArrayList<>();
        for(int i = 0; i<allDates.size() ; i++) {
        String date = allDates.get(i);
        if(date!=null) {
        	Mute mute = mixedMap.get(allDates.get(i));
        	if(mute!=null) {
        		SortMute.add(mute);
        		
        		}
        	}
        }
        Collections.reverse(SortMute);
        return SortMute;
        
	}
	@EventHandler(priority = EventPriority.HIGHEST)

	public void OnChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		checkMute(player.getUniqueId());
		Mute mute = currentMutes.get(player.getUniqueId());
		if (mute == null)
			return;
		String expiration = (mute.getExpiration() == null ? "never"
				: new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(mute.getExpiration()));
		event.setCancelled(true);
		OfflinePlayer muter = Bukkit.getOfflinePlayer(mute.getMuterUuid());
		player.sendMessage(__.PREFIX + "§aYou were muted by §b" + muter.getName() + " §auntil §b" + expiration);
	}

}
