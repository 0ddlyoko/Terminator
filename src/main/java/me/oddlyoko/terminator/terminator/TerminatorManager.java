package me.oddlyoko.terminator.terminator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import lombok.Getter;
import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.__;
import me.oddlyoko.terminator.database.model.BanModel;
import me.oddlyoko.terminator.database.model.KickModel;
import me.oddlyoko.terminator.database.model.MuteModel;
import me.oddlyoko.terminator.inventories.inv.BanHistInventory;
import me.oddlyoko.terminator.inventories.inv.KickHistInventory;
import me.oddlyoko.terminator.inventories.inv.MainInventory;
import me.oddlyoko.terminator.inventories.inv.MuteHistInventory;

public class TerminatorManager implements Listener {
	private HashMap<UUID, TerminatorPlayer> players;
	// Bans
	@Getter
	private List<Ban> bans;
	@Getter
	private List<Kick> kicks;
	@Getter
	private List<Mute> mutes;
	private Set<UUID> bypass;
	private File bypassFile;

	private MainInventory mainInventory;
	private BanHistInventory banHistInventory;
	private KickHistInventory kickHistInventory;
	private MuteHistInventory muteHistInventory;
	@Getter
	private boolean loading = true;

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
		Bukkit.getScheduler().runTaskAsynchronously(Terminator.get(), () -> {
			Bukkit.getLogger().info(__.PREFIX + "Loading bans ...");
			// Bans
			List<Ban> bans = new ArrayList<>();
			try {
				List<UUID> banUuids = Terminator.get().getDatabaseManager().getBanManager()
						.getPlayersUUIDBannedOnce(Terminator.get().getDatabaseModel());
				System.out.println("Got " + banUuids.size() + " different uuids");
				for (UUID uuid : banUuids) {
					// Get bans for specific uuid
					try {
						TerminatorPlayer p = getPlayer(uuid);
						List<BanModel> banModels = Terminator.get().getDatabaseManager().getBanManager()
								.getPlayerBans(uuid, Terminator.get().getDatabaseModel());
						for (BanModel banModel : banModels) {
							Ban ban = new Ban(banModel.getSanctionId(), banModel.getPunishedUuid(),
									banModel.getPunisherUuid(), banModel.getReason(),
									banModel.getCreationDate() == null ? null
											: new Date(banModel.getCreationDate().getTime()),
									banModel.getExpiration() == null ? null
											: new Date(banModel.getExpiration().getTime()),
									banModel.isDeleted(), banModel.getDeleteReason(), banModel.getDeletePlayer());
							bans.add(ban);
							p.addBan(ban);
						}
					} catch (Exception ex) {
						// Whut
						Bukkit.getLogger().log(Level.SEVERE, "Exception while retrieving bans for " + uuid, ex);
						Bukkit.shutdown();
					}
				}
				this.bans = bans;
			} catch (Exception ex) {
				Bukkit.getLogger().log(Level.SEVERE, "An error has occured while retrieving bans", ex);
				Bukkit.shutdown();
			}
			Bukkit.getLogger().info(__.PREFIX + bans.size() + " bans loaded");
			Collections.sort(bans, new Comparator<Ban>() {

				@Override
				public int compare(Ban o1, Ban o2) {
					return (int) (o1.getSanctionId() - o2.getSanctionId());
				}

			});
			Bukkit.getLogger().info(__.PREFIX + "Loading kicks ...");

			// Kicks
			List<Kick> kicks = new ArrayList<>();
			try {
				List<UUID> kickUuids = Terminator.get().getDatabaseManager().getKickManager()
						.getPlayersUUIDKickedOnce(Terminator.get().getDatabaseModel());
				System.out.println("Got " + kickUuids.size() + " different uuids");
				for (UUID uuid : kickUuids) {
					// Get kicks for specific uuid
					try {
						TerminatorPlayer p = getPlayer(uuid);
						List<KickModel> kickModels = Terminator.get().getDatabaseManager().getKickManager()
								.getPlayerKicks(uuid, Terminator.get().getDatabaseModel());
						for (KickModel kickModel : kickModels) {
							Kick kick = new Kick(kickModel.getSanctionId(), kickModel.getPunishedUuid(),
									kickModel.getPunisherUuid(), kickModel.getReason(),
									new Date(kickModel.getCreationDate().getTime()));
							kicks.add(kick);
							p.addKick(kick);
						}
					} catch (Exception ex) {
						// Whut
						Bukkit.getLogger().log(Level.SEVERE, "Exception while retrieving kicks for " + uuid, ex);
						Bukkit.shutdown();
					}
				}
				this.kicks = kicks;
			} catch (Exception ex) {
				Bukkit.getLogger().log(Level.SEVERE, "An error has occured while retrieving kicks", ex);
				Bukkit.shutdown();
			}

			Bukkit.getLogger().info(__.PREFIX + kicks.size() + " kicks loaded");
			Collections.sort(kicks, new Comparator<Kick>() {

				@Override
				public int compare(Kick o1, Kick o2) {
					return (int) (o1.getSanctionId() - o2.getSanctionId());
				}

			});
			Bukkit.getLogger().info(__.PREFIX + "Loading mutes ...");

			// Bans
			List<Mute> mutes = new ArrayList<>();
			try {
				List<UUID> muteUuids = Terminator.get().getDatabaseManager().getMuteManager()
						.getPlayersUUIDMutedOnce(Terminator.get().getDatabaseModel());
				System.out.println("Got " + muteUuids.size() + " different uuids");
				for (UUID uuid : muteUuids) {
					// Get mutes for specific uuid
					try {
						TerminatorPlayer p = getPlayer(uuid);
						List<MuteModel> muteModels = Terminator.get().getDatabaseManager().getMuteManager()
								.getPlayerMutes(uuid, Terminator.get().getDatabaseModel());
						for (MuteModel muteModel : muteModels) {
							Mute mute = new Mute(muteModel.getSanctionId(), muteModel.getPunishedUuid(),
									muteModel.getPunisherUuid(), muteModel.getReason(),
									muteModel.getCreationDate() == null ? null
											: new Date(muteModel.getCreationDate().getTime()),
									muteModel.getExpiration() == null ? null
											: new Date(muteModel.getExpiration().getTime()),
									muteModel.isDeleted(), muteModel.getDeleteReason(), muteModel.getDeletePlayer());
							mutes.add(mute);
							p.addMute(mute);
						}
					} catch (Exception ex) {
						// Whut
						Bukkit.getLogger().log(Level.SEVERE, "Exception while retrieving mutes for " + uuid, ex);
						Bukkit.shutdown();
					}
				}
				this.mutes = mutes;
			} catch (Exception ex) {
				Bukkit.getLogger().log(Level.SEVERE, "An error has occured while retrieving mutes", ex);
				Bukkit.shutdown();
			}

			Bukkit.getLogger().info(__.PREFIX + mutes.size() + " mutes loaded");
			Collections.sort(mutes, new Comparator<Mute>() {

				@Override
				public int compare(Mute o1, Mute o2) {
					return (int) (o1.getSanctionId() - o2.getSanctionId());
				}

			});
			loading = false;
		});
		// TODO Load bans, mutes & kicks
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

	public void ban(UUID punishedUuid, UUID punisherUuid, String reason, Date expiration) {
		Ban ban = new Ban(punishedUuid, punisherUuid, reason, expiration);
		TerminatorPlayer player = getPlayer(punishedUuid);
		String playerName = UUIDs.get(punishedUuid);
		bans.add(ban);
		player.addBan(ban);
		Player p = Bukkit.getPlayer(punishedUuid);
		Bukkit.getScheduler().runTaskAsynchronously(Terminator.get(), () -> {
			try {
				Bukkit.getScheduler().runTask(Terminator.get(), () -> {
					if (p != null && p.isOnline())
						p.kickPlayer(getBanMessage(ban));
				});
				long id = Terminator.get().getDatabaseManager().getBanManager()
						.addBan(new BanModel(0, punishedUuid, punisherUuid, reason, null,
								new Timestamp(expiration.getTime()), false, null, null),
								Terminator.get().getDatabaseModel());
				ban.setSanctionId(id);
				String msg = __.PREFIX + ChatColor.GOLD + playerName + ChatColor.GREEN + " has been banned by "
						+ ChatColor.GOLD + (punisherUuid == null ? "CONSOLE" : UUIDs.get(punisherUuid))
						+ ChatColor.GREEN + " until " + ChatColor.GOLD + (expiration == null ? "never"
								: new SimpleDateFormat("yyyy-MM-dd hh:MM:ss").format(expiration));
				Bukkit.broadcast(msg, "terminator.ban.see");
			} catch (Exception ex) {
				Bukkit.broadcast(__.PREFIX + ChatColor.RED + "Exception while saving ban for player " + ChatColor.GOLD
						+ playerName + ChatColor.RED + ", ask ODD to fix that", "terminator.ban.see");
				Bukkit.getLogger().log(Level.SEVERE,
						__.PREFIX + ChatColor.RED + "SQLException while saving ban for player " + ChatColor.GOLD
								+ playerName + ChatColor.RED + ", ask ODD to fix that",
						ex);
			}
		});
	}

	public void unban(UUID punishedUuid, String deleteReason, UUID deletePlayer) {
		TerminatorPlayer player = getPlayer(punishedUuid);
		String playerName = UUIDs.get(punishedUuid);
		Ban ban = player.getBan();
		ban.setDeleted(true);
		ban.setDeletePlayer(deletePlayer);
		ban.setDeleteReason(deleteReason);
		try {
			if (ban.getSanctionId() == 0) {
				Bukkit.broadcast(
						__.PREFIX + ChatColor.AQUA + playerName + ChatColor.GREEN
								+ " is no longer banned but cannot update database because id is not set :(",
						"terminator.ban.see");
				return;
			}
			Terminator.get().getDatabaseManager().getBanManager().stopBan(
					new BanModel(ban.getSanctionId(), null, null, null, null, null, true, deleteReason, deletePlayer),
					Terminator.get().getDatabaseModel());
			Bukkit.broadcast(__.PREFIX + ChatColor.AQUA + playerName + ChatColor.GREEN + " is no longer banned",
					"terminator.ban.see");
		} catch (Exception ex) {
			Bukkit.broadcast(__.PREFIX + ChatColor.RED + "Exception while saving unban for player " + ChatColor.GOLD
					+ playerName + ChatColor.RED + ", ask ODD to fix that", "terminator.ban.see");
			Bukkit.getLogger().log(Level.SEVERE,
					__.PREFIX + ChatColor.RED + "SQLException while saving unban for player " + ChatColor.GOLD
							+ playerName + ChatColor.RED + ", ask ODD to fix that",
					ex);
		}
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
				: new SimpleDateFormat("yyyy-MM-dd hh:MM:ss").format(currentBan.getExpiration()));
		for (String str : banMessages)
			sb.append(str.replace("{PREFIX}", __.PREFIX).replace("{REASON}", currentBan.getReason())
					.replace("{EXPIRATION}", expiration)).append('\n');
		return sb.toString();
	}

	// ---------------------- KICK ----------------------

	public void kick(UUID punishedUuid, UUID punisherUuid, String reason) {
		Kick kick = new Kick(punishedUuid, punisherUuid, reason);
		TerminatorPlayer player = getPlayer(punishedUuid);
		String playerName = UUIDs.get(punishedUuid);
		kicks.add(kick);
		player.addKick(kick);
		Player p = Bukkit.getPlayer(punishedUuid);
		if (p != null)
			p.kickPlayer(__.PREFIX + "\n" + ChatColor.GRAY + "You have been kicked for\n " + ChatColor.GOLD + reason);
		try {
			long id = Terminator.get().getDatabaseManager().getKickManager().addKick(
					new KickModel(0, punishedUuid, punisherUuid, reason, null), Terminator.get().getDatabaseModel());
			kick.setSanctionId(id);
			Bukkit.broadcast(
					ChatColor.GOLD + playerName + ChatColor.GRAY + " has been kicked by " + ChatColor.GOLD
							+ UUIDs.get(punisherUuid) + ChatColor.GRAY + "for " + ChatColor.GOLD + reason,
					"terminator.kick.see");
		} catch (Exception ex) {
			Bukkit.broadcast(__.PREFIX + ChatColor.RED + "Exception while saving kick for player " + ChatColor.GOLD
					+ playerName + ChatColor.RED + ", ask ODD to fix that", "terminator.kick.see");
			Bukkit.getLogger().log(Level.SEVERE,
					__.PREFIX + ChatColor.RED + "SQLException while saving kick for player " + ChatColor.GOLD
							+ playerName + ChatColor.RED + ", ask ODD to fix that",
					ex);
		}
	}

	// ---------------------- MUTE ----------------------

	public void mute(UUID punishedUuid, UUID punisherUuid, String reason, Date expiration) {
		Mute mute = new Mute(punishedUuid, punisherUuid, reason, expiration);
		TerminatorPlayer player = getPlayer(punishedUuid);
		String playerName = UUIDs.get(punishedUuid);
		mutes.add(mute);
		player.addMute(mute);
		try {
			long id = Terminator.get().getDatabaseManager().getMuteManager()
					.addMute(
							new MuteModel(0, punishedUuid, punisherUuid, reason, null,
									new Timestamp(expiration.getTime()), false, null, null),
							Terminator.get().getDatabaseModel());
			mute.setSanctionId(id);
			String msg = __.PREFIX + ChatColor.GOLD + playerName + ChatColor.GREEN + " has been muted by "
					+ ChatColor.GOLD + (punisherUuid == null ? "CONSOLE" : playerName) + ChatColor.GREEN + " until "
					+ ChatColor.GOLD
					+ (expiration == null ? "never" : new SimpleDateFormat("yyyy-MM-dd hh:MM:ss").format(expiration));
			Bukkit.broadcast(msg, "terminator.mute.see");
		} catch (Exception ex) {
			Bukkit.broadcast(__.PREFIX + ChatColor.RED + "Exception while saving mute for player " + ChatColor.GOLD
					+ playerName + ChatColor.RED + ", ask ODD to fix that", "terminator.mute.see");
			Bukkit.getLogger().log(Level.SEVERE,
					__.PREFIX + ChatColor.RED + "SQLException while saving mute for player " + ChatColor.GOLD
							+ playerName + ChatColor.RED + ", ask ODD to fix that",
					ex);
		}
	}

	public void unmute(UUID punishedUuid, String deleteReason, UUID deletePlayer) {
		Mute mute = getPlayer(punishedUuid).getMute();
		String playerName = UUIDs.get(punishedUuid);
		mute.setDeleted(true);
		mute.setDeletePlayer(deletePlayer);
		mute.setDeleteReason(deleteReason);
		try {
			if (mute.getSanctionId() == 0) {
				Bukkit.broadcast(
						__.PREFIX + ChatColor.AQUA + playerName + ChatColor.GREEN
								+ " is no longer muted but cannot update database because id is not set :(",
						"terminator.mute.see");
				return;
			}
			Terminator.get().getDatabaseManager().getMuteManager().stopMute(new MuteModel(mute.getSanctionId(), null,
					null, null, null, null, false, deleteReason, deletePlayer), Terminator.get().getDatabaseModel());
			Bukkit.broadcast(
					__.PREFIX + ChatColor.AQUA + UUIDs.get(punishedUuid) + ChatColor.GREEN + " is no longer muted",
					"terminator.mute.see");
		} catch (Exception ex) {
			Bukkit.broadcast(__.PREFIX + ChatColor.RED + "Exception while saving unmute for player " + ChatColor.GOLD
					+ playerName + ChatColor.RED + ", ask ODD to fix that", "terminator.mute.see");
			Bukkit.getLogger().log(Level.SEVERE,
					__.PREFIX + ChatColor.RED + "SQLException while saving unmute for player " + ChatColor.GOLD
							+ playerName + ChatColor.RED + ", ask ODD to fix that",
					ex);
		}
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
	 * Open the main inventory for specific player at specific page<br />
	 * If player is null, open main menu
	 * 
	 * @param p
	 *                   The player
	 * @param player
	 *                   The player that we want to show the history. Can be null
	 */
	public void openMainInventory(Player p, TerminatorPlayer player) {
		Terminator.get().getInventorymanager().openInventory(mainInventory, p, inv -> {
			inv.put(MainInventory.USER, player);
		});
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
