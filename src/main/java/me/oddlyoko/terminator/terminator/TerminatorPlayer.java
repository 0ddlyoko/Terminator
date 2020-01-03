package me.oddlyoko.terminator.terminator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TerminatorPlayer {
	private UUID uuid;
	private List<Ban> bans;
	private boolean isCurrentlyBanned;
	private List<Kick> kicks;
	private List<Mute> mutes;
	private boolean isCurrentlyMuted;
	private boolean bypass;

	public TerminatorPlayer(UUID uuid) {
		this.uuid = uuid;
		this.bans = new ArrayList<>();
		this.kicks = new ArrayList<>();
		this.mutes = new ArrayList<>();
		this.bypass = false;
	}

	public UUID getUuid() {
		return uuid;
	}

	public List<Ban> getBans() {
		return bans;
	}

	public Ban getBan() {
		checkBan();
		if (isCurrentlyBanned)
			return bans.get(bans.size() - 1);
		return null;
	}

	public void addBan(Ban ban) {
		bans.add(ban);
		isCurrentlyBanned = !ban.isDeleted() && (ban.getExpiration() == null || new Date().before(ban.getExpiration()));
	}

	/**
	 * Recheck if player is banned by checking if the last ban is still active
	 */
	private void checkBan() {
		if (bans.size() == 0) {
			isCurrentlyBanned = false;
			return;
		}
		Ban ban = bans.get(bans.size() - 1);
		// Check if ban is expired
		isCurrentlyBanned = !ban.isDeleted() && (ban.getExpiration() == null || new Date().before(ban.getExpiration()));
	}

	public boolean isBanned() {
		checkBan();
		return isCurrentlyBanned;
	}

	public List<Kick> getKicks() {
		return kicks;
	}

	public void addKick(Kick kick) {
		kicks.add(kick);
	}

	public List<Mute> getMutes() {
		return mutes;
	}

	public Mute getMute() {
		checkMute();
		if (isCurrentlyMuted)
			return mutes.get(mutes.size() - 1);
		return null;
	}

	public void addMute(Mute mute) {
		mutes.add(mute);
		isCurrentlyMuted = !mute.isDeleted()
				&& (mute.getExpiration() == null || new Date().before(mute.getExpiration()));
	}

	private void checkMute() {
		// Do not check if he's not muted
		if (!isCurrentlyMuted)
			return;
		if (mutes.size() == 0) {
			isCurrentlyMuted = false;
			return;
		}
		Mute mute = mutes.get(mutes.size() - 1);
		// Check if mute is expired
		isCurrentlyMuted = !mute.isDeleted()
				&& (mute.getExpiration() == null || new Date().before(mute.getExpiration()));
	}

	public boolean isMuted() {
		checkMute();
		return isCurrentlyMuted;
	}

	public boolean isBypass() {
		return bypass;
	}

	public void setBypass(boolean bypass) {
		this.bypass = bypass;
	}
}
