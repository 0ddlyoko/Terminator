package me.oddlyoko.terminator.hist;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.__;
import me.oddlyoko.terminator.bans.Ban;
import me.oddlyoko.terminator.inventories.ClickableItem;
import me.oddlyoko.terminator.inventories.Inventory;
import me.oddlyoko.terminator.inventories.InventoryProvider;
import me.oddlyoko.terminator.inventories.ItemBuilder;
	
public class MainInventory implements InventoryProvider {

		public static final org.bukkit.inventory.ItemStack MuteHead = ItemBuilder.of(Material.PLAYER_HEAD).name("§c§lMUTES").lore(Arrays.asList("§bVoir l'historique des mutes"
				)).texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM0ZDg4M2ZjNjk4NTVlMjI0ZWY1NmQyMTY0MzM1MDBjNmRmMTY3ZjgzOWQ5NDA0NTMyMDIzZmE5Y2JhNDBiYyJ9fX0=").build();
		public static final org.bukkit.inventory.ItemStack BanHead = ItemBuilder.of(Material.PLAYER_HEAD).name("§c§lBANS").lore(Arrays.asList("§bVoir l'historique des bans"
				)).texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0MjI5Mjk0MGJkMGJjYzBlZDM4Yjk3MjkyYzk0M2IwMjc1YzRiNmM5NTEyYzFhZjc2MzZmZGI3ZWJkNzMyMiJ9fX0=").build();
		public static final org.bukkit.inventory.ItemStack KickHead = ItemBuilder.of(Material.PLAYER_HEAD).name("§c§lKICKS").lore(Arrays.asList("§bVoir l'historique des bans"
				)).texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM2MTM3MzE5OTRiYzMwZGViMTZiMTJlZjE1OTkzOTY0NDNlNDZhMzI1NzliYzlkYjNjYzU3NGI1YWRlNzZjNyJ9fX0=").build();
		  public static final String PAGE = "page";
	@Override
	public String title(Inventory inv) {
		String title = "§cMain menu";
		return title;
	}

	@Override
	public int rows(Inventory inv) {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public void init(Inventory inv) {
		inv.fillRectangle(0, 9, 3, ClickableItem.of(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build()));
		inv.set(11, ClickableItem.of(BanHead , e->{
			Player p =(Player) e.getWhoClicked();
			Terminator.get().getInventorymanager().openInventory(new BanHistInventorry(), p, i->{
				i.put(PAGE, 1);
			});
		}));
		inv.set(13, ClickableItem.of(MuteHead , e->{
			Player p =(Player) e.getWhoClicked();
			Terminator.get().getInventorymanager().openInventory(new MuteHistInventorry(), p, i->{
				i.put(PAGE, 1);
			});
		}
		));
		inv.set(15, ClickableItem.of(KickHead,e->{
			Player p =(Player) e.getWhoClicked();
			Terminator.get().getInventorymanager().openInventory(new KickHistInventory(), p, i->{
				i.put(PAGE, 1);
			});
		}
));
		inv.set(0, ClickableItem.of(ItemBuilder.of(Material.PAPER).name(__.PREFIX + "§7V1.0").build()));
		List<Ban> list = Terminator.get().getBanManager().getSortedBanHistory();
		for(int i =0; i<list.size() ; i++) {
			System.out.println(list.get(i).getCreationDate());
		}
	}

	@Override
	public void update(Inventory inv) {
		// TODO Auto-generated method stub

	}

}
