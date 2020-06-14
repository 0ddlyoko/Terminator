package me.oddlyoko.terminator.terminator;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class BanIp {
	@Setter
	private long sanctionId;
	private String punishedIp;
	// If punisher is null then the ban didn't come from a player but from the
	// console
	private UUID punisherUuid;
	private String reason;
	private Date creationDate;
	private Date expiration;
	@Setter
	private boolean isDeleted;
	@Setter
	private String deleteReason;
	@Setter
	private UUID deletePlayer;

	public BanIp(String punishedIp, UUID punisherUuid, String reason, Date expiration) {
		this.punishedIp = punishedIp;
		this.punisherUuid = punisherUuid;
		this.reason = reason;
		this.expiration = expiration;
		this.creationDate = new Date();
	}

	/**
	 * @return true if this ban has expired
	 */
	public boolean isExpired() {
		return isDeleted || (expiration != null && new Date().after(expiration));
	}
}
