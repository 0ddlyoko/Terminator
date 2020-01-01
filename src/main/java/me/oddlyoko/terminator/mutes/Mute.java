package me.oddlyoko.terminator.mutes;

import java.util.Date;
import java.util.UUID;

public class Mute {
	private long sanctionId;
	private UUID muterUuid;
	private UUID mutedUuid;
	private String reason;
	private Date creationDate;
	private Date expiration;
	private boolean isDeleted;
	private String deleteReason;
	private UUID deletePlayer;

	public Mute(UUID mutedUuid, UUID muterUuid, String reason, Date expiration) {
		this.mutedUuid = mutedUuid;
		this.muterUuid = muterUuid;
		this.reason = reason;
		this.expiration = expiration;
	}

	public long getSanctionId() {
		return sanctionId;
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

	public UUID getMuterUuid() {
		return muterUuid;
	}

	public void setMuterUuid(UUID muterUuid) {
		this.muterUuid = muterUuid;
	}

	public UUID getMutedUuid() {
		return mutedUuid;
	}

	public void setMutedUuid(UUID mutedUuid) {
		this.mutedUuid = mutedUuid;
	}
}
