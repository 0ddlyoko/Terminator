package me.oddlyoko.terminator.terminator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.__;

public class TerminatorManager implements Listener {
	private HashMap<UUID, TerminatorPlayer> players;
	// Bans
	private List<Ban> bans;
	private List<Kick> kicks;
	private List<Mute> mutes;
	private Set<UUID> bypass;
	private File bypassFile;

	public TerminatorManager() {
		players = new HashMap<>();
		bans = new ArrayList<>();
		kicks = new ArrayList<>();
		mutes = new ArrayList<>();
		bypass = new HashSet<>();
		bypassFile = new File("plugins" + File.separator + __.NAME + File.separator + "bypass");
		try {
			if (!bypassFile.exists() && !bypassFile.createNewFile())
				Bukkit.getLogger().severe(bypassFile.getName() + " cannot be created !");
			else if (!bypassFile.isFile())
				Bukkit.getLogger().severe(bypassFile.getName() + " is not a file !");
			else {
				Bukkit.getLogger().info("Reading bypass file");
				Scanner sc = new Scanner(new FileInputStream(bypassFile));
				String line;
				while (sc.hasNextLine()) {
					line = sc.nextLine();
					try {
						UUID uuid = UUID.fromString(line);
						bypass.add(uuid);
						getPlayer(uuid).setBypass(true);
					} catch (IllegalArgumentException ex) {
						Bukkit.getLogger().warning("Line " + line + " is not a valid UUID");
					}
				}
				Bukkit.getLogger().info("Bypass file Done !");
				sc.close();
			}
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Error: ", ex);
		}
		// TODO Load bans & kicks
	}

	public TerminatorPlayer getPlayer(UUID uuid) {
		TerminatorPlayer tp = players.get(uuid);
		if (tp == null) {
			tp = new TerminatorPlayer(uuid);
			players.put(uuid, tp);
		}
		return tp;
	}

	// ---------------------- BAN ----------------------

	public List<Ban> getBans() {
		return bans;
	}

	public void ban(UUID punishedUuid, UUID punisherUuid, String reason, Date expiration) {
		Ban ban = new Ban(punishedUuid, punisherUuid, reason, expiration);
		TerminatorPlayer player = getPlayer(punishedUuid);
		String playerName = UUIDs.get(punishedUuid);
		CommandSender sender;
		if (punisherUuid != null)
			sender = Bukkit.getPlayer(punisherUuid);
		else
			sender = Bukkit.getConsoleSender();
		bans.add(ban);
		player.addBan(ban);
		Player p = Bukkit.getPlayer(punishedUuid);
		// TODO
		// if (p != null)
		// p.kickPlayer("You have been banned: " + getBanMessage(ban)); \!/\!/
		// Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, getBanMessage(ban),
		// expiration, null);
		// if (p != null && p.isOnline())
		p.kickPlayer(getBanMessage(ban));
		String msg = __.PREFIX + ChatColor.GOLD + playerName + ChatColor.GREEN + " has been banned by " + ChatColor.GOLD
				+ (punisherUuid == null ? "CONSOLE" : UUIDs.get(punisherUuid)) + ChatColor.GREEN + " until "
				+ ChatColor.GOLD
				+ (expiration == null ? "never" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(expiration));
		Bukkit.broadcast(msg, "terminator.ban.see");
	}

	public void unban(UUID punishedUuid, String deleteReason, UUID deletePlayer) {
		CommandSender sender;
		TerminatorPlayer player = getPlayer(punishedUuid);
		if (deletePlayer != null)
			sender = Bukkit.getPlayer(deletePlayer);
		else
			sender = Bukkit.getConsoleSender();
		String playerName = UUIDs.get(punishedUuid);
		Ban ban = player.getBan();
		if (ban == null) {
			sender.sendMessage(__.PREFIX + ChatColor.RED + "Error: Player " + playerName + " isn't banned");
			return;
		}
		ban.setDeleted(true);
		ban.setDeletePlayer(deletePlayer);
		ban.setDeleteReason(deleteReason);
		// Bukkit.getBanList(BanList.Type.NAME).pardon(playerName);
		sender.sendMessage(__.PREFIX + ChatColor.AQUA + playerName + ChatColor.GREEN + " is no longer banned");
	}

	public boolean isBanned(UUID uuid) {
		return getPlayer(uuid).isBanned();
	}

	@EventHandler
	public void onPlayerJoin(PlayerLoginEvent e) {
		TerminatorPlayer p = getPlayer(e.getPlayer().getUniqueId());
		Ban b = p.getBan();
		if (b != null && !p.isBypass())
			e.disallow(PlayerLoginEvent.Result.KICK_BANNED, getBanMessage(b));
	}

	public String getBanMessage(Ban currentBan) {
		StringBuilder sb = new StringBuilder();
		List<String> banMessages = Terminator.get().getConfigManager().getBanMessage();
		String expiration = (currentBan.getExpiration() == null ? "never"
				: new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(currentBan.getExpiration()));
		for (String str : banMessages)
			sb.append(str.replace("{PREFIX}", __.PREFIX).replace("{REASON}", currentBan.getReason())
					.replace("{EXPIRATION}", expiration)).append('\n');
		return sb.toString();
	}

	// ---------------------- KICK ----------------------

	public List<Kick> getKicks() {
		return kicks;
	}

	public void kick(UUID kickedUuid, UUID kickerUuid, String reason) {
		Kick kick = new Kick(kickedUuid, kickerUuid, reason);
		TerminatorPlayer player = getPlayer(kickedUuid);
		String playerName = UUIDs.get(kickedUuid);
		CommandSender sender;
		if (kickerUuid != null)
			sender = Bukkit.getPlayer(kickerUuid);
		else
			sender = Bukkit.getConsoleSender();
		kicks.add(kick);
		player.addKick(kick);
		Player p = Bukkit.getPlayer(kickedUuid);
		if (p != null)
			p.kickPlayer(__.PREFIX + "\n" + ChatColor.GRAY + "You have been kicked for\n " + ChatColor.GOLD + reason);
		Bukkit.broadcast(
				ChatColor.GOLD + playerName + ChatColor.GRAY + " has been kicked by " + ChatColor.GOLD
						+ UUIDs.get(kickerUuid) + ChatColor.GRAY + "§afor " + ChatColor.GOLD + reason,
				"terminator.kick.see");
		// TODO Save in database
	}

	// ---------------------- MUTE ----------------------

	public List<Mute> getMutes() {
		return mutes;
	}

	public void mute(UUID mutedUuid, UUID muterUuid, String reason, Date expiration) {
		Mute mute = new Mute(mutedUuid, muterUuid, reason, expiration);
		TerminatorPlayer player = getPlayer(mutedUuid);
		CommandSender sender;
		if (muterUuid != null)
			sender = Bukkit.getPlayer(muterUuid);
		else
			sender = Bukkit.getConsoleSender();
		// Check if player is already banned
		mutes.add(mute);
		player.addMute(mute);
		Player p = Bukkit.getPlayer(mutedUuid);
		String msg = __.PREFIX + ChatColor.GOLD + UUIDs.get(mutedUuid) + ChatColor.GREEN + " has been muted by "
				+ ChatColor.GOLD + (mutedUuid == null ? "CONSOLE" : UUIDs.get(mutedUuid)) + ChatColor.GREEN + " until "
				+ ChatColor.GOLD
				+ (expiration == null ? "never" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(expiration));
		Bukkit.broadcast(msg, "terminator.mute.see");
	}

	public void unmute(UUID mutedUuid, String deleteReason, UUID deletePlayer) {
		TerminatorPlayer player = getPlayer(mutedUuid);
		CommandSender sender;
		if (deletePlayer != null)
			sender = Bukkit.getPlayer(deletePlayer);
		else
			sender = Bukkit.getConsoleSender();
		Mute mute = getPlayer(mutedUuid).getMute();
		mute.setDeleted(true);
		mute.setDeletePlayer(deletePlayer);
		mute.setDeleteReason(deleteReason);
		sender.sendMessage(__.PREFIX + ChatColor.AQUA + UUIDs.get(mutedUuid) + ChatColor.GREEN + " is no longer muted");
	}

	public boolean isMuted(UUID uuid) {
		return getPlayer(uuid).isMuted();
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		TerminatorPlayer p = getPlayer(e.getPlayer().getUniqueId());
		Mute m = p.getMute();
		if (m != null && !p.isBypass()) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(__.PREFIX + ChatColor.RED + "You can't talk because you're muted");
		}
	}

	// ---------------------- BYPASS ----------------------

	private void saveBypass() {
		try (FileWriter fw = new FileWriter(bypassFile)) {
			Iterator<UUID> it = bypass.iterator();
			while (it.hasNext())
				fw.append(it.next().toString()).append("\n");
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Error: ", ex);
		}
	}

	public void addBypass(UUID uuid) {
		bypass.add(uuid);
		getPlayer(uuid).setBypass(true);
		saveBypass();
	}

	public void removeBypass(UUID uuid) {
		bypass.remove(uuid);
		getPlayer(uuid).setBypass(false);
		saveBypass();
	}

	public boolean isBypass(UUID uuid) {
		return getPlayer(uuid).isBypass();
	}
}
