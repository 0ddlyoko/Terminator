package me.oddlyoko.terminator.mutes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.__;

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
