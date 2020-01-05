package me.oddlyoko.terminator.inventories.inv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.UUIDs;
import me.oddlyoko.terminator.__;
import me.oddlyoko.terminator.inventories.ClickableItem;
import me.oddlyoko.terminator.inventories.Inventory;
import me.oddlyoko.terminator.inventories.InventoryProvider;
import me.oddlyoko.terminator.inventories.ItemBuilder;
import me.oddlyoko.terminator.terminator.Kick;
import me.oddlyoko.terminator.terminator.TerminatorPlayer;

public class KickHistInventory implements InventoryProvider {
	public static final String PAGE = "page";
	public static final String USER = "user";
	public static final String UPDATE = "update";
	private final ItemStack NEXT = ItemBuilder.of(Material.PLAYER_HEAD, 1).name(ChatColor.GOLD + "==>")
			.lore(Arrays.asList("", "==>"))
			.texture(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19")
			.build();
	private final ItemStack PREVIOUS = ItemBuilder.of(Material.PLAYER_HEAD, 1).name(ChatColor.GOLD + "<==")
			.lore(Arrays.asList("", "<=="))
			.texture(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
			.build();
	private final ClickableItem BLACKBACKGROUND = ClickableItem
			.of(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build());
	private final ClickableItem GRAYBACKGROUND = ClickableItem
			.of(ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build());
	private final ItemStack BACK = ItemBuilder.of(Material.BARRIER)
			.name(ChatColor.BOLD + ChatColor.RED.toString() + "Go back").build();
	private final ClickableItem VERSION = ClickableItem
			.of(ItemBuilder.of(Material.PAPER).name(__.PREFIX + "§7V1.0").build());

	@Override
	public String title(Inventory inv) {

		return ChatColor.GREEN + "Kick History" + (inv.get(USER) == null ? ""
				: " > " + ChatColor.GOLD + UUIDs.get(((TerminatorPlayer) inv.get(USER)).getUuid()));
	}

	@Override
	public int rows(Inventory inv) {
		return 5;
	}

	@Override
	public void init(Inventory inv) {
		inv.put(UPDATE, true);
		inv.fillRectangle(1, 5, 9, 1, BLACKBACKGROUND);
		inv.fillRectangle(1, 1, 9, 1, GRAYBACKGROUND);
		inv.set(0, ClickableItem.of(BACK, e -> {
			Terminator.get().getTerminatorManager().openMainInventory(inv.getPlayer());
		}));
		inv.set(8, VERSION);
	}

	@Override
	public void update(Inventory inv) {
		boolean update = (boolean) inv.get(UPDATE);
		if (!update)
			return;
		inv.put(UPDATE, false);
		Object oUser = inv.get(USER);
		TerminatorPlayer user = null;
		if (oUser != null)
			user = (TerminatorPlayer) oUser;
		List<Kick> kicks = (user == null) ? Terminator.get().getTerminatorManager().getKicks() : user.getKicks();
		int kickPages = (kicks.size() - 1) / 27 + 1;
		int page = (int) inv.get(PAGE);
		if (page > 1) {
			// Left arrow
			inv.set(1, 5, ClickableItem.of(PREVIOUS, e -> {
				// Update
				inv.put(PAGE, page - 1);
				inv.put(UPDATE, true);
			}));
		} else
			inv.set(1, 5, BLACKBACKGROUND);
		if (page < kickPages) {
			// Right arrow
			inv.set(9, 5, ClickableItem.of(NEXT, e -> {
				// Update
				inv.put(PAGE, page + 1);
				inv.put(UPDATE, true);
			}));
		} else
			inv.set(9, 5, BLACKBACKGROUND);
		if (page < 1) {
			// Wtf ?
			inv.put(PAGE, 1);
			inv.put(UPDATE, true);
			return;
		}
		if (page > kickPages) {
			// Outside
			inv.put(PAGE, kickPages);
			inv.put(UPDATE, true);
			return;
		}
		// Items
		inv.fillRectangle(1, 2, 9, 3, ClickableItem.of(new ItemStack(Material.AIR)));
		int from = (page - 1) * 27;
		for (int i = 0; i < 27; i++) {
			if (from + i >= kicks.size())
				break;
			inv.set(i + 9, ClickableItem.of(getKickSkull(kicks.get(from + i))));
		}
	}

	private ItemStack getKickSkull(Kick kick) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		ItemStack skull = ItemBuilder.of(Material.PLAYER_HEAD).build();
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(kick.getPunishedUuid()));
		meta.setDisplayName(ChatColor.GOLD + Bukkit.getOfflinePlayer(kick.getPunishedUuid()).getName());
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "Kicked by: " + ChatColor.GOLD
				+ (kick.getPunisherUuid() == null ? "CONSOLE" : UUIDs.get(kick.getPunisherUuid())));
		lore.add(ChatColor.YELLOW + "At: " + ChatColor.GOLD + format.format(kick.getCreationDate()));
		lore.add(ChatColor.YELLOW + "Reason: " + ChatColor.GOLD + kick.getReason());
		meta.setLore(lore);
		skull.setItemMeta(meta);
		return skull;
	}
}
