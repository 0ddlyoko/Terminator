package me.oddlyoko.terminator;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

public class Listener implements org.bukkit.event.Listener {

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		UUIDs.register(uuid, e.getPlayer().getName());
	}
}
