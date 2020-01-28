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
public class BanIpModel {
	/* Database Structure
	 
	Table: Terminator_banip
	
	+---------------+--------------+------+-----+---------+----------------+
	| Field         | Type         | Null | Key | Default | Extra          |
	+---------------+--------------+------+-----+---------+----------------+
	| sanction_id   | bigint(18)   | NO   | PRI | NULL    | auto_increment |
	| punished_ip   | varchar(15)  | NO   |     | NULL    |                |
	| punisher_uuid | binary(16)   | NO   |     | NULL    |                |
	| reason        | varchar(255) | NO   |     | NULL    |                |
	| creation_date | timestamp    | NO   |     | NOW()   |                |
	| expiration    | timestamp    | NO   |     | NULL    |                | -> 0 = never
	| is_deleted    | boolean(1)   | NO   |     | FALSE   |                |
	| delete_reason | varchar(255) | YES  |     | NULL    |                |
	| delete_player | binary(16)   | YES  |     | NULL    |                |
	+---------------+--------------+------+-----+---------+----------------+
	 */
	
	private long sanctionId;
	private String punishedIp;
	private UUID punisherUuid;
	private String reason;
	private Timestamp creationDate;
	private Timestamp expiration;
	private boolean isDeleted;
	private String deleteReason;
	private UUID deletePlayer;
}
