CREATE TABLE Terminator_ban (
	sanction_id INT NOT NULL AUTO_INCREMENT,
	punished_uuid CHAR(36) NOT NULL,
	punisher_uuid CHAR(36) NULL,
	reason VARCHAR(255) NOT NULL,
	creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	expiration TIMESTAMP NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
	delete_reason VARCHAR(255) NULL,
	delete_player CHAR(36) NULL,
PRIMARY KEY (sanction_id));

CREATE TABLE Terminator_kick (
	sanction_id INT NOT NULL AUTO_INCREMENT,
	punished_uuid CHAR(36) NOT NULL,
	punisher_uuid CHAR(36) NULL,
	reason VARCHAR(255) NOT NULL,
	creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (sanction_id));

CREATE TABLE Terminator_mute (
	sanction_id INT NOT NULL AUTO_INCREMENT,
	punished_uuid CHAR(36) NOT NULL,
	punisher_uuid CHAR(36) NULL,
	reason VARCHAR(255) NOT NULL,
	creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	expiration TIMESTAMP NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
	delete_reason VARCHAR(255) NULL,
	delete_player CHAR(36) NULL,
PRIMARY KEY (sanction_id));