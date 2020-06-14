package me.oddlyoko.terminator.database.model;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KickModel {
	/* Database Structure
	 
	Table: Terminator_kick
	
	+---------------+--------------+------+-----+---------+----------------+
	| Field         | Type         | Null | Key | Default | Extra          |
	+---------------+--------------+------+-----+---------+----------------+
	| sanction_id   | bigint(18)   | NO   | PRI | NULL    | auto_increment |
	| punished_uuid | binary(16)   | NO   |     | NULL    |                |
	| punisher_uuid | binary(16)   | NO   |     | NULL    |                |
	| reason        | varchar(255) | NO   |     | NULL    |                |
	| creation_date | timestamp    | NO   |     | NOW()   |                |
	+---------------+--------------+------+-----+---------+----------------+
	 */
	
	private long sanctionId;
	private UUID punishedUuid;
	private UUID punisherUuid;
	private String reason;
	private Timestamp creationDate;
}
