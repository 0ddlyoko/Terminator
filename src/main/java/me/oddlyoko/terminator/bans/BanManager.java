package me.oddlyoko.terminator.bans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.__;

public class BanManager implements Listener {
	// An HashMap containing as key the UUID of the player and as value a list
	// containing all bans
	// This HashMap contains ban history for online players
	private HashMap<UUID, List<Ban>> bans;
	// An HashMap containing as key the UUID of the player and as value the current
	// ban
	// This HashMap contains players that are currently banned
	private HashMap<UUID, Ban> currentBans;

	public BanManager() {
		bans = new HashMap<>();
		currentBans = new HashMap<>();
		// TODO load players that are currently banned
		for (Player p : Bukkit.getOnlinePlayers()) {
			// TODO load bans
		}
	}

	public void addBan(UUID punishedUuid, UUID punisherUuid, String reason, Date expiration) {
		checkBan(punishedUuid);
		Ban ban = new Ban(punishedUuid, punisherUuid, reason, expiration);
		CommandSender sender;
		if (punisherUuid != null)
			sender = Bukkit.getPlayer(punisherUuid);
		else
			sender = Bukkit.getConsoleSender();
		// Check if player is already banned
		List<Ban> bans = this.bans.get(punishedUuid);
		if (bans == null)
			bans = new ArrayList<>();
		bans.add(ban);
		this.bans.put(punishedUuid, bans);
		this.currentBans.put(punishedUuid, ban);
		Player p = Bukkit.getPlayer(punishedUuid);
		// TODO
		// if (p != null)
		// p.kickPlayer("Your banned: " + reason);
		String msg = __.PREFIX + ChatColor.AQUA + UUIDs.get(punishedUuid) + ChatColor.GREEN + " has been banned by "
				+ ChatColor.AQUA + (punisherUuid == null ? "CONSOLE" : UUIDs.get(punisherUuid)) + ChatColor.GREEN
				+ " until " + ChatColor.AQUA
				+ (expiration == null ? "never" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(expiration));
		Bukkit.broadcast(msg, "terminator.ban.see");
		// TODO Save in database
	}

	public void removeBan(UUID punishedUuid, String deleteReason, UUID deletePlayer) {
		checkBan(punishedUuid);
		CommandSender sender;
		if (deletePlayer != null)
			sender = Bukkit.getPlayer(deletePlayer);
		else
			sender = Bukkit.getConsoleSender();
		Ban ban = currentBans.get(punishedUuid);
		ban.setDeleted(true);
		ban.setDeletePlayer(deletePlayer);
		ban.setDeleteReason(deleteReason);
		currentBans.remove(punishedUuid);
		sender.sendMessage(
				__.PREFIX + ChatColor.AQUA + UUIDs.get(punishedUuid) + ChatColor.GREEN + " is no longer banned");
	}

	public boolean isBanned(UUID uuid) {
		checkBan(uuid);
		return currentBans.containsKey(uuid);
	}

	private void checkBan(UUID uuid) {
		Ban currentBan = currentBans.get(uuid);
		if (currentBan != null) {
			// Player was banned, check if ban is expired
			if (currentBan.isDeleted()
					|| (currentBan.getExpiration() != null && new Date().after(currentBan.getExpiration())))
				// Ban was deleted or expired
				currentBans.remove(uuid);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		checkBan(uuid);
		Ban currentBan = currentBans.get(uuid);
		if (currentBan != null) {
			// He's banned, kick him
			// TODO Custom message
			List<String> banMessages = Terminator.get().getConfigManager().getBanMessage();
			String expiration = (currentBan.getExpiration() == null ? "never"
					: new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(currentBan.getExpiration()));
			StringBuilder sb = new StringBuilder();
			for (String str : banMessages)
				sb.append(str.replace("{PREFIX}", __.PREFIX).replace("{REASON}", currentBan.getReason())
						.replace("{EXPIRATION}", expiration)).append('\n');
			p.kickPlayer(sb.toString());
			return;
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		// Unload
		bans.remove(uuid);
	}
}
