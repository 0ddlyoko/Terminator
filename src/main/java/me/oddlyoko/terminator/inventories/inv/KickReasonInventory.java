package me.oddlyoko.terminator.inventories.inv;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.__;
import me.oddlyoko.terminator.inventories.ClickableItem;
import me.oddlyoko.terminator.inventories.Inventory;
import me.oddlyoko.terminator.inventories.InventoryProvider;
import me.oddlyoko.terminator.inventories.ItemBuilder;
import me.oddlyoko.terminator.terminator.Ban;
import me.oddlyoko.terminator.terminator.Kick;
import me.oddlyoko.terminator.terminator.TerminatorPlayer;

public class KickReasonInventory implements InventoryProvider {
	public static final String KICK= "kick";
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
	private final ClickableItem INFO = ClickableItem
			.of(ItemBuilder.of(Material.PLAYER_HEAD).name(__.PREFIX + ChatColor.GRAY + "§b§lINFO").lore(Arrays.asList("§6You can customise kick reason by adding reason to your command")).texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDAxYWZlOTczYzU0ODJmZGM3MWU2YWExMDY5ODgzM2M3OWM0MzdmMjEzMDhlYTlhMWEwOTU3NDZlYzI3NGEwZiJ9fX0=").build());	@Override
	public String title(Inventory inv) {
		// TODO Auto-generated method stub
		return "§cChoose Kick reason";
	}

	@Override
	public int rows(Inventory inv) {
		
		return 4;
	}

	@Override	
	public void init(Inventory inv) {
		inv.put(UPDATE, true);
		inv.put(PAGE , 1);
		inv.fillRectangle(0, 9, 1, GRAYBACKGROUND);
		inv.fillRectangle(27, 9, 1, BLACKBACKGROUND);
		inv.set(0, ClickableItem.of(BACK,e->{
			e.getWhoClicked().closeInventory();
		}));
		inv.set(8, INFO	);
		
	}

	@Override
	public void update(Inventory inv) {
		boolean update = (boolean) inv.get(UPDATE);
		if (!update)
			return;
		inv.put(UPDATE, false);
		List<String> messages = Terminator.get().getConfigManager().getKickReason();
		int kickPages = (messages.size() - 1) / 18 + 1;
		int page = (int) inv.get(PAGE);
		if (page > 1) {
			// Left arrow
			inv.set(27, ClickableItem.of(PREVIOUS, e -> {
				// Update
				inv.put(PAGE, page - 1);
				inv.put(UPDATE, true);
			}));
		} else
			inv.set(27,BLACKBACKGROUND);
		if (page < kickPages) {
			// Right arrow
			inv.set(35, ClickableItem.of(NEXT, e -> {
				// Update
				inv.put(PAGE, page + 1);
				inv.put(UPDATE, true);
			}));
		} else
			inv.set(35, BLACKBACKGROUND);
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
		inv.fillRectangle(9,9,2,ClickableItem.of(new ItemStack(Material.AIR)));
		int from = (page - 1) * 18;
		for (int i = 0; i < 18; i++) {
			if (from + i >= messages.size())
				break;
			String reason = messages.get(from + i);
			inv.set(i + 9, ClickableItem.of(getItemKickReason(reason	) , e->{
				e.getWhoClicked().closeInventory();
				Kick kick = (Kick) inv.get(KICK);
				kick.setReason(reason);
				Terminator.get().getTerminatorManager().kick(kick.getPunishedUuid(), kick.getPunisherUuid(), kick.getReason());
			}));
		}
	}

	private ItemStack getItemKickReason(String msg){
		ItemStack stack = ItemBuilder.of(Material.PAPER).build();
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.RED+msg);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
		meta.setLore(Arrays.asList("§bClic to apply"));
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		return stack;
	}
}
