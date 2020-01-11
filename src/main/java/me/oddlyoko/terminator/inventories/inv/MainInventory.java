package me.oddlyoko.terminator.inventories.inv;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.__;
import me.oddlyoko.terminator.inventories.ClickableItem;
import me.oddlyoko.terminator.inventories.Inventory;
import me.oddlyoko.terminator.inventories.InventoryProvider;
import me.oddlyoko.terminator.inventories.ItemBuilder;
import me.oddlyoko.terminator.terminator.TerminatorPlayer;

public class MainInventory implements InventoryProvider {
	public static final String USER = "user";

	private final org.bukkit.inventory.ItemStack banHead = ItemBuilder.of(Material.PLAYER_HEAD)
			.name(ChatColor.BOLD + ChatColor.RED.toString() + "BANS")
			.lore(Arrays.asList(ChatColor.BLUE + "See ban history"))
			.texture(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0MjI5Mjk0MGJkMGJjYzBlZDM4Yjk3MjkyYzk0M2IwMjc1YzRiNmM5NTEyYzFhZjc2MzZmZGI3ZWJkNzMyMiJ9fX0=")
			.build();
	private final org.bukkit.inventory.ItemStack muteHead = ItemBuilder.of(Material.PLAYER_HEAD)
			.name(ChatColor.BOLD + ChatColor.RED.toString() + "MUTES")
			.lore(Arrays.asList(ChatColor.BLUE + "See mute history"))
			.texture(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM0ZDg4M2ZjNjk4NTVlMjI0ZWY1NmQyMTY0MzM1MDBjNmRmMTY3ZjgzOWQ5NDA0NTMyMDIzZmE5Y2JhNDBiYyJ9fX0=")
			.build();
	private final org.bukkit.inventory.ItemStack kickHead = ItemBuilder.of(Material.PLAYER_HEAD)
			.name(ChatColor.BOLD + ChatColor.RED.toString() + "KICKS")
			.lore(Arrays.asList(ChatColor.BLUE + "See kick history"))
			.texture(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM2MTM3MzE5OTRiYzMwZGViMTZiMTJlZjE1OTkzOTY0NDNlNDZhMzI1NzliYzlkYjNjYzU3NGI1YWRlNzZjNyJ9fX0=")
			.build();
	private final ClickableItem BLACKBACKGROUND = ClickableItem
			.of(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build());
	private final ClickableItem VERSION = ClickableItem
			.of(ItemBuilder.of(Material.PAPER).name(__.PREFIX + ChatColor.GRAY + "V1.0").build());

	@Override
	public String title(Inventory inv) {
		return "§cMain menu";
	}

	@Override
	public int rows(Inventory inv) {
		return 3;
	}

	@Override
	public void init(Inventory inv) {
		Object oUser = inv.get(USER);
		final TerminatorPlayer user = oUser == null ? null : (TerminatorPlayer) oUser;
		inv.fillRectangle(0, 9, 3, BLACKBACKGROUND);
		if (user != null) {
			ItemStack skull = ItemBuilder.of(Material.PLAYER_HEAD).build();
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setOwningPlayer(Bukkit.getOfflinePlayer(user.getUuid()));
			meta.setDisplayName(ChatColor.GOLD + UUIDs.get(user.getUuid()));
			skull.setItemMeta(meta);
			inv.set(9, 1, ClickableItem.of(skull));
		}
		inv.set(11, ClickableItem.of(banHead, e -> {
			Player p = (Player) e.getWhoClicked();
			Terminator.get().getTerminatorManager().openBanHistInventory(p, 1, user);
		}));
		inv.set(13, ClickableItem.of(muteHead, e -> {
			Player p = (Player) e.getWhoClicked();
			Terminator.get().getTerminatorManager().openMuteHistInventory(p, 1, user);
		}));
		inv.set(15, ClickableItem.of(kickHead, e -> {
			Player p = (Player) e.getWhoClicked();
			Terminator.get().getTerminatorManager().openKickHistInventory(p, 1, user);
		}));
		inv.set(0, VERSION);
	}

	@Override
	public void update(Inventory inv) {
		// Nothing to update
	}
}
