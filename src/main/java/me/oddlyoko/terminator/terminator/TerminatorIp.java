package me.oddlyoko.terminator.terminator;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class TerminatorIp {
	private String ip;
	private List<BanIp> bans;
	private boolean isCurrentlyBanned;

	public TerminatorIp(String ip) {
		this.ip = ip;
		this.bans = new ArrayList<>();
	}

	public BanIp getBan() {
		checkBan();
		if (isCurrentlyBanned)
			return bans.get(bans.size() - 1);
		return null;
	}

	public void addBan(BanIp banIp) {
		bans.add(banIp);
		isCurrentlyBanned = !banIp.isExpired();
	}

	/**
	 * Check if ip is banned by checking if the last ban is still active
	 */
	private void checkBan() {
		if (bans.size() == 0) {
			isCurrentlyBanned = false;
			return;
		}
		BanIp banIp = bans.get(bans.size() - 1);
		// Check if ban is expired
		isCurrentlyBanned = !banIp.isExpired();
	}

	public boolean isBanned() {
		checkBan();
		return isCurrentlyBanned;
	}
}
