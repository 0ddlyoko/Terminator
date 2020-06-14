package me.oddlyoko.terminator.database;

import lombok.Getter;
import me.oddlyoko.terminator.database.controller.BanIpManager;
import me.oddlyoko.terminator.database.controller.BanManager;
import me.oddlyoko.terminator.database.controller.KickManager;
import me.oddlyoko.terminator.database.controller.MuteManager;

@Getter
public class DatabaseManager {
	private BanManager banManager;
	private MuteManager muteManager;
	private KickManager kickManager;
	private BanIpManager banIpManager;

	public DatabaseManager() {
		banManager = new BanManager();
		muteManager = new MuteManager();
		kickManager = new KickManager();
		banIpManager = new BanIpManager();
	}
}
