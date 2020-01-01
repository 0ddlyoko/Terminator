package me.oddlyoko.terminator.hist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mysql.fabric.xmlrpc.base.Array;
import com.sun.xml.internal.ws.api.pipe.NextAction;

import me.oddlyoko.terminator.Terminator;
import me.oddlyoko.terminator.__;
import me.oddlyoko.terminator.bans.Ban;
import me.oddlyoko.terminator.inventories.ClickableItem;
import me.oddlyoko.terminator.inventories.Inventory;
import me.oddlyoko.terminator.inventories.InventoryProvider;
import me.oddlyoko.terminator.inventories.ItemBuilder;
import me.oddlyoko.terminator.kicks.Kick;

public class KickHistInventory implements InventoryProvider{
	 private final ItemStack INEXT = ItemBuilder.of(Material.PLAYER_HEAD, 1).name(ChatColor.AQUA + "Page suivante")
			    .lore(Arrays.asList(new String[] { ""
			        })).texture(
			      "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19")
			    .build();
			  private final ItemStack IPREVIOUS = ItemBuilder.of(Material.PLAYER_HEAD, 1).name(ChatColor.AQUA + "Page précédente")
			    .lore(Arrays.asList(new String[] { ""
			        })).texture(
			      "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
			    .build();
			  public static final String PAGE = "page";
			  private int maxPage;
			  private ClickableItem NEXT = null;
			  private ClickableItem PREVIOUS = null;	
	@Override
	public String title(Inventory inv) {
		
		return "§cHistorique de kicks";
	}

	@Override
	public int rows(Inventory inv) {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public void init(Inventory inv) {
		List<Kick> kicks = Terminator.get().getBanManager().getSortedKickHistory();
		int page = (int) inv.get(PAGE);
		NEXT = ClickableItem.of(INEXT , e ->{
			int pagee = (int) inv.get(PAGE);
			inv.put(PAGE, pagee+1);
			loadHeads(inv, kicks, (int)inv.get(PAGE));
			ChangeButtons(inv);
		});
		PREVIOUS = ClickableItem.of(IPREVIOUS ,e ->{
			int pagee = (int) inv.get(PAGE);
			inv.put(PAGE, pagee -1);
			loadHeads(inv, kicks, (int) inv.get(PAGE));
			ChangeButtons(inv);
		});
		inv.fillRectangle(36, 9, 1, ClickableItem.of(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build()));
		inv.fillRectangle(0, 9, 1, ClickableItem.of(ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));
		inv.set(0, ClickableItem.of(ItemBuilder.of(Material.BARRIER).name("§b§lRETOUR").build()));
		inv.set(8, ClickableItem.of(ItemBuilder.of(Material.PAPER).name(__.PREFIX + "§7V1.0").build()));
		//L'entier au dessus du double
		Double d = (double) (kicks.size()/27);
		maxPage = d.intValue()+1;
		loadHeads(inv, kicks, (int)page);
		ChangeButtons(inv);
	}

	@Override
	public void update(Inventory inv) {
		
		
	}
	public void ChangeButtons(Inventory inv) {
		int page = (int) inv.get(PAGE);
		if(maxPage <=1) {
			return;
		}
		if(page==1) {
					inv.set(44 , NEXT);
					inv.set(36, ClickableItem.of(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).build()));
			}
		if(page==maxPage) {
				inv.set(36, PREVIOUS);	
				inv.set(44, ClickableItem.of(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).build()));
			}
		if(page!= maxPage && page != 1) {
			inv.set(36, PREVIOUS);	
			inv.set(44 , NEXT);
		}
		return;
			
	}
	
	private ItemStack getBanSkull(UUID uuid , Kick kick) {
		//Nom du joueur
		//Banni par 
		//le 
		//Jusqu'au
		//Pour 
		//Status
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		ItemStack skull = ItemBuilder.of(Material.PLAYER_HEAD).build();
		SkullMeta meta =(SkullMeta) skull.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		meta.setDisplayName("§b"+Bukkit.getOfflinePlayer(kick.getKickedUuid()).getName());
		List<String> lore = new ArrayList<>();
		lore.add("§eBanni par §f"+Bukkit.getOfflinePlayer(kick.getKickerUuid()).getName());
		lore.add("§eLe §f" + format.format(kick.getCreationDate()));
		lore.add("§ePour §f" + kick.getReason());
		meta.setLore(lore);
		skull.setItemMeta(meta);
		return skull;
		
	}
	
	public void loadHeads(Inventory inv , List<Kick> kicks , int page) {
		inv.fillRectangle(9, 9, 3, ClickableItem.of(ItemBuilder.of(Material.AIR).build()));
		int checkout = kicks.size()<=27 ? kicks.size() : 27*page;
		for(int i = page*27-27 ; i<checkout ; i++) {
			Kick kick =kicks.get(i);
			int pos = i-((page-1)*27)+9;
			inv.set(pos, ClickableItem.of(getBanSkull(kick.getKickedUuid(), kick)));
			if(i==kicks.size()-1) {
				}
			}
	}
}


