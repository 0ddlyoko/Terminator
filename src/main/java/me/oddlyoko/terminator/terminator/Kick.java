package me.oddlyoko.terminator.terminator;

import java.util.Date;
import java.util.UUID;

public class Kick {
	private long sanctionId;
	private UUID punishedUuid;
	// If punisher is null then the mute didn't come from a player but from the
	// console
	private UUID punisherUuid;
	private String reason;
	private Date creationDate;

	public Kick(UUID punishedUuid, UUID punisherUuid, String reason) {
		this.punishedUuid = punishedUuid;
		this.punisherUuid = punisherUuid;
		this.reason = reason;
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
}
