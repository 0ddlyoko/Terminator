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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.__;
import me.oddlyoko.terminator.inventories.inv.BanHistInventory;
import me.oddlyoko.terminator.inventories.inv.KickHistInventory;
import me.oddlyoko.terminator.inventories.inv.MainInventory;
import me.oddlyoko.terminator.inventories.inv.MuteHistInventory;

public class TerminatorManager implements Listener {
	private HashMap<UUID, TerminatorPlayer> players;
	// Bans
	private List<Ban> bans;
	private List<Kick> kicks;
	private List<Mute> mutes;
	private Set<UUID> bypass;
	private File bypassFile;

	private MainInventory mainInventory;
	private BanHistInventory banHistInventory;
	private KickHistInventory kickHistInventory;
	private MuteHistInventory muteHistInventory;

	public TerminatorManager() {
		players = new HashMap<>();
		bans = new ArrayList<>();
		kicks = new ArrayList<>();
		mutes = new ArrayList<>();
		bypass = new HashSet<>();
		mainInventory = new MainInventory();
		banHistInventory = new BanHistInventory();
		kickHistInventory = new KickHistInventory();
		muteHistInventory = new MuteHistInventory();
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
		bans.add(ban);
		player.addBan(ban);
		Player p = Bukkit.getPlayer(punishedUuid);
		if (p != null && p.isOnline())
			p.kickPlayer(getBanMessage(ban));
		String msg = __.PREFIX + ChatColor.GOLD + playerName + ChatColor.GREEN + " has been banned by " + ChatColor.GOLD
				+ (punisherUuid == null ? "CONSOLE" : UUIDs.get(punisherUuid)) + ChatColor.GREEN + " until "
				+ ChatColor.GOLD
				+ (expiration == null ? "never" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(expiration));
		Bukkit.broadcast(msg, "terminator.ban.see");
	}

	public void unban(UUID punishedUuid, String deleteReason, UUID deletePlayer) {
		TerminatorPlayer player = getPlayer(punishedUuid);
		Ban ban = player.getBan();
		ban.setDeleted(true);
		ban.setDeletePlayer(deletePlayer);
		ban.setDeleteReason(deleteReason);
		Bukkit.broadcast(
				__.PREFIX + ChatColor.AQUA + UUIDs.get(punishedUuid) + ChatColor.GREEN + " is no longer banned",
				"terminator.ban.see");
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

	public void kick(UUID punishedUuid, UUID punisherUuid, String reason) {
		Kick kick = new Kick(punishedUuid, punisherUuid, reason);
		TerminatorPlayer player = getPlayer(punishedUuid);
		String playerName = UUIDs.get(punishedUuid);
		kicks.add(kick);
		player.addKick(kick);
		Player p = Bukkit.getPlayer(punishedUuid);
		if (p != null)
			p.kickPlayer(__.PREFIX + "\n" + ChatColor.GRAY + "You have been kicked for\n " + ChatColor.GOLD + reason);
		Bukkit.broadcast(
				ChatColor.GOLD + playerName + ChatColor.GRAY + " has been kicked by " + ChatColor.GOLD
						+ UUIDs.get(punisherUuid) + ChatColor.GRAY + "§afor " + ChatColor.GOLD + reason,
				"terminator.kick.see");
		// TODO Save in database
	}

	// ---------------------- MUTE ----------------------

	public List<Mute> getMutes() {
		return mutes;
	}

	public void mute(UUID punishedUuid, UUID punisherUuid, String reason, Date expiration) {
		Mute mute = new Mute(punishedUuid, punisherUuid, reason, expiration);
		TerminatorPlayer player = getPlayer(punishedUuid);
		mutes.add(mute);
		player.addMute(mute);
		String msg = __.PREFIX + ChatColor.GOLD + UUIDs.get(punishedUuid) + ChatColor.GREEN + " has been muted by "
				+ ChatColor.GOLD + (punisherUuid == null ? "CONSOLE" : UUIDs.get(punisherUuid)) + ChatColor.GREEN
				+ " until " + ChatColor.GOLD
				+ (expiration == null ? "never" : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(expiration));
		Bukkit.broadcast(msg, "terminator.mute.see");
	}

	public void unmute(UUID punishedUuid, String deleteReason, UUID deletePlayer) {
		Mute mute = getPlayer(punishedUuid).getMute();
		mute.setDeleted(true);
		mute.setDeletePlayer(deletePlayer);
		mute.setDeleteReason(deleteReason);
		Bukkit.broadcast(__.PREFIX + ChatColor.AQUA + UUIDs.get(punishedUuid) + ChatColor.GREEN + " is no longer muted",
				"terminator.mute.see");
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

	// ---------------------- INVENTORY ----------------------

	/**
	 * Open the main inventory
	 * 
	 * @param p
	 *              The player
	 */
	public void openMainInventory(Player p) {
		Terminator.get().getInventorymanager().openInventory(mainInventory, p);
	}

	/**
	 * Open the ban history for specific player at specific page<br />
	 * If player is null, open all bans history
	 * 
	 * @param p
	 *                   The player that open the inventory
	 * @param page
	 *                   The actual page number
	 * @param player
	 *                   The player that we want to show the history. Can be null
	 */
	public void openBanHistInventory(Player p, int page, TerminatorPlayer player) {
		Terminator.get().getInventorymanager().openInventory(banHistInventory, p, inv -> {
			inv.put(BanHistInventory.PAGE, page <= 0 ? 1 : page);
			inv.put(BanHistInventory.USER, player);
		});
	}

	/**
	 * Open the kick history for specific player at specific page<br />
	 * If player is null, open all kicks history
	 * 
	 * @param p
	 *                   The player that open the inventory
	 * @param page
	 *                   The actual page number
	 * @param player
	 *                   The player that we want to show the history. Can be null
	 */
	public void openKickHistInventory(Player p, int page, TerminatorPlayer player) {
		Terminator.get().getInventorymanager().openInventory(kickHistInventory, p, inv -> {
			inv.put(KickHistInventory.PAGE, page <= 0 ? 1 : page);
			inv.put(KickHistInventory.USER, player);
		});
	}

	/**
	 * Open the mute history for specific player at specific page<br />
	 * If player is null, open all mutes history
	 * 
	 * @param p
	 *                   The player that open the inventory
	 * @param page
	 *                   The actual page number
	 * @param player
	 *                   The player that we want to show the history. Can be null
	 */
	public void openMuteHistInventory(Player p, int page, TerminatorPlayer player) {
		Terminator.get().getInventorymanager().openInventory(muteHistInventory, p, inv -> {
			inv.put(KickHistInventory.PAGE, page <= 0 ? 1 : page);
			inv.put(KickHistInventory.USER, player);
		});
	}
}
