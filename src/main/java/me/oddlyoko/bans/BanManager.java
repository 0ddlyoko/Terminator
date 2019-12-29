package me.oddlyoko.bans;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.oddlyoko.terminator.Terminator;

public class BanManager implements Listener {

	Terminator main;
	public BanManager(Terminator main) {
		this.main = main;
	}
	
	@EventHandler
	
	public void OnJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
			if(main.getBan_list().containsKey(player.getUniqueId().toString())) {
				Ban ban = main.getBan_list().get(player.getUniqueId());
				player.sendMessage("§cVous avez été bannis le §e" + ban.getStringDate() + " §c par §f "+ ban.getStaff_name() + 
						" §cjusqu'au §e" + ban.getStringExpDate() + " §cPour §f " + ban.getReason());
			}
	}
	
	
}
