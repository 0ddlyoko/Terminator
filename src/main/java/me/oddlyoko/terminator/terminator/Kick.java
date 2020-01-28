package me.oddlyoko.terminator.terminator;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Kick {
	@Setter
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
}
