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
import me.oddlyoko.terminator.terminator.Mute;
import me.oddlyoko.terminator.terminator.TerminatorPlayer;

public class MuteHistInventory implements InventoryProvider {
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
			.of(ItemBuilder.of(Material.PAPER).name(__.PREFIX + ChatColor.GRAY + "V1.0").build());

	@Override
	public String title(Inventory inv) {
		return ChatColor.GREEN + "Mute History" + (inv.get(USER) == null ? ""
				: " > " + ChatColor.GOLD + UUIDs.get(((TerminatorPlayer) inv.get(USER)).getUuid()));
	}

	@Override
	public int rows(Inventory inv) {
		return 5;
	}

	@Override
	public void init(Inventory inv) {
		Object oUser = inv.get(USER);
		inv.put(UPDATE, true);
		inv.fillRectangle(1, 5, 9, 1, BLACKBACKGROUND);
		inv.fillRectangle(1, 1, 9, 1, GRAYBACKGROUND);
		inv.set(0, ClickableItem.of(BACK, e -> {
			Terminator.get().getTerminatorManager().openMainInventory(inv.getPlayer(),
					oUser == null ? null : (TerminatorPlayer) oUser);
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
		List<Mute> mutes = (user == null) ? Terminator.get().getTerminatorManager().getMutes() : user.getMutes();
		int mutePages = (mutes.size() - 1) / 27 + 1;
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
		if (page < mutePages) {
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
		if (page > mutePages) {
			// Outside
			inv.put(PAGE, mutePages);
			inv.put(UPDATE, true);
			return;
		}
		// Items
		inv.fillRectangle(1, 2, 9, 3, ClickableItem.of(new ItemStack(Material.AIR)));
		int from = Math.min(page * 27, mutes.size());
		int to = 27 * (page - 1);
		for (int i = 0; i < 27; i++) {
			if (from - i <= to)
				break;
			inv.set(i + 9, ClickableItem.of(getMuteSkull(mutes.get(from - i - 1))));
		}
	}

	private ItemStack getMuteSkull(Mute mute) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		ItemStack skull = ItemBuilder.of(Material.PLAYER_HEAD).build();
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(mute.getPunishedUuid()));
		meta.setDisplayName(ChatColor.GOLD + Bukkit.getOfflinePlayer(mute.getPunishedUuid()).getName());
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "Muted by: " + ChatColor.GOLD
				+ (mute.getPunisherUuid() == null ? "CONSOLE" : UUIDs.get(mute.getPunisherUuid())));
		lore.add(ChatColor.YELLOW + "At: " + ChatColor.GOLD + format.format(mute.getCreationDate()));
		lore.add(ChatColor.YELLOW + "Until: " + ChatColor.GOLD
				+ (mute.getExpiration() == null ? "NEVER" : format.format(mute.getExpiration())));
		lore.add(ChatColor.YELLOW + "Reason: " + ChatColor.GOLD + mute.getReason());
		lore.add(ChatColor.BOLD + (mute.isExpired() ? ChatColor.GREEN + "[Expired]" : ChatColor.RED + "[On]"));
		meta.setLore(lore);
		skull.setItemMeta(meta);
		return skull;
	}
}
