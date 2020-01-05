package me.oddlyoko.terminator.terminator;

import java.util.Date;
import java.util.UUID;

public class Mute {
	private long sanctionId;
	private UUID punishedUuid;
	// If punisher is null then the mute didn't come from a player but from the
	// console
	private UUID punisherUuid;
	private String reason;
	private Date creationDate;
	private Date expiration;
	private boolean isDeleted;
	private String deleteReason;
	private UUID deletePlayer;

	public Mute(UUID punishedUuid, UUID punisherUuid, String reason, Date expiration) {
		this.punishedUuid = punishedUuid;
		this.punisherUuid = punisherUuid;
		this.reason = reason;
		this.expiration = expiration;
		this.creationDate = new Date();
	}

	public long getSanctionId() {
		return sanctionId;
	}

	public UUID getPunishedUuid() {
		return punishedUuid;
	}

	public UUID getPunisherUuid() {
		return punisherUuid;
	}

	public String getReason() {
		return reason;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getExpiration() {
		return expiration;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}

	public UUID getDeletePlayer() {
		return deletePlayer;
	}

	public void setDeletePlayer(UUID deletePlayer) {
		this.deletePlayer = deletePlayer;
	}

	/**
	 * @return true if this ban has expired
	 */
	public boolean isExpired() {
		return isDeleted || (expiration != null && new Date().after(expiration));
	}
}
