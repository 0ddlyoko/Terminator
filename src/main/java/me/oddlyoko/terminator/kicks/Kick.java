package me.oddlyoko.terminator.kicks;

import java.util.Date;
import java.util.UUID;

public class Kick {
	private long sanctionId;
	private UUID kickerUuid;
	private UUID kickedUuid;
	private String reason;
	private Date creationDate;

	public Kick(UUID kickedUuid, UUID kickerUuid, String reason) {
		this.kickedUuid = kickedUuid;
		this.kickerUuid = kickerUuid;
		this.reason = reason;
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

	public UUID getKickerUuid() {
		return kickerUuid;
	}

	public void setKickerUuid(UUID kickerUuid) {
		this.kickerUuid = kickerUuid;
	}

	public UUID getKickedUuid() {
		return kickedUuid;
	}

	public void setKickedUuid(UUID kickedUuid) {
		this.kickedUuid = kickedUuid;
	}

}
