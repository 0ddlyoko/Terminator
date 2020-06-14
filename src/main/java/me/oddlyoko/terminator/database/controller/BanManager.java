package me.oddlyoko.terminator.database.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.mysql.jdbc.Statement;

import me.oddlyoko.terminator.database.DatabaseModel;
import me.oddlyoko.terminator.database.model.BanModel;

public class BanManager {
	public static final String TABLE = "Terminator_ban";

	/**
	 * Get ban from id
	 * 
	 * @param sanctionId
	 *                       The id of the ban
	 * @param model
	 *                       The connection
	 * @return The ban associated with the id or null if not found
	 * @throws SQLException
	 *                          If error
	 */
	public BanModel getBan(long sanctionId, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT sanction_id, punished_uuid, punisher_uuid, reason, creation_date, expiration, "
					+ "is_deleted, delete_reason, delete_player FROM " + TABLE + " WHERE sanction_id=?";
			s = c.prepareStatement(sql);
			s.setLong(1, sanctionId);

			rs = s.executeQuery();
			if (rs.next()) {
				// Exist
				UUID punishedUuid = UUID.fromString(rs.getString("punished_uuid"));
				String strPunisherUuid = rs.getString("punisher_uuid");
				UUID punisherUuid = strPunisherUuid == null ? null : UUID.fromString(strPunisherUuid);
				String reason = rs.getString("reason");
				Timestamp creationDate = rs.getTimestamp("creation_date");
				Timestamp expiration = rs.getTimestamp("expiration");
				boolean isDeleted = rs.getBoolean("is_deleted");
				String deleteReason = rs.getString("delete_reason");
				String strDeletePlayer = rs.getString("delete_player");
				UUID deletePlayer = strDeletePlayer == null ? null : UUID.fromString(strDeletePlayer);
				return new BanModel(sanctionId, punishedUuid, punisherUuid, reason, creationDate, expiration, isDeleted,
						deleteReason, deletePlayer);
			} else
				return null;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "getBan: SQLException while retrieving ban " + sanctionId, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Get all bans for specific player
	 * 
	 * @param uuid
	 *                  The UUID of the player
	 * @param model
	 *                  The connection
	 * @return All bans associated with specific player or an empty list if not
	 *         found
	 * @throws SQLException
	 *                          If error
	 */
	public List<BanModel> getPlayerBans(UUID uuid, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT sanction_id, punished_uuid, punisher_uuid, reason, creation_date, expiration, "
					+ "is_deleted, delete_reason, delete_player FROM " + TABLE
					+ " WHERE punished_uuid=? ORDER BY sanction_id";
			s = c.prepareStatement(sql);
			s.setString(1, uuid.toString());

			rs = s.executeQuery();
			List<BanModel> bans = new ArrayList<>();
			while (rs.next()) {
				// Exist
				long sanctionId = rs.getLong("sanction_id");
				UUID punishedUuid = UUID.fromString(rs.getString("punished_uuid"));
				String strPunisherUuid = rs.getString("punisher_uuid");
				UUID punisherUuid = strPunisherUuid == null ? null : UUID.fromString(strPunisherUuid);
				String reason = rs.getString("reason");
				Timestamp creationDate = rs.getTimestamp("creation_date");
				Timestamp expiration = rs.getTimestamp("expiration");
				boolean isDeleted = rs.getBoolean("is_deleted");
				String deleteReason = rs.getString("delete_reason");
				String strDeletePlayer = rs.getString("delete_player");
				UUID deletePlayer = strDeletePlayer == null ? null : UUID.fromString(strDeletePlayer);
				bans.add(new BanModel(sanctionId, punishedUuid, punisherUuid, reason, creationDate, expiration,
						isDeleted, deleteReason, deletePlayer));
			}
			return bans;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "getPlayerBans: SQLException while retrieving bans for player " + uuid,
					ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns all bans that the player has given
	 * 
	 * @param uuid
	 *                  The UUID of the player
	 * @param model
	 *                  The connection
	 * @return All bans associated with specific player or an empty list if not
	 *         found
	 * @throws SQLException
	 *                          If error
	 */
	public List<BanModel> getPlayerGivenBans(UUID uuid, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT sanction_id, punished_uuid, punisher_uuid, reason, creation_date, expiration, "
					+ "is_deleted, delete_reason, delete_player FROM " + TABLE
					+ " WHERE punisher_uuid=? ORDER BY sanction_id";
			s = c.prepareStatement(sql);
			s.setString(1, uuid.toString());

			rs = s.executeQuery();
			List<BanModel> bans = new ArrayList<>();
			while (rs.next()) {
				// Exist
				long sanctionId = rs.getLong("sanction_id");
				UUID punishedUuid = UUID.fromString(rs.getString("punished_uuid"));
				String strPunisherUuid = rs.getString("punisher_uuid");
				UUID punisherUuid = strPunisherUuid == null ? null : UUID.fromString(strPunisherUuid);
				String reason = rs.getString("reason");
				Timestamp creationDate = rs.getTimestamp("creation_date");
				Timestamp expiration = rs.getTimestamp("expiration");
				boolean isDeleted = rs.getBoolean("is_deleted");
				String deleteReason = rs.getString("delete_reason");
				String strDeletePlayer = rs.getString("delete_player");
				UUID deletePlayer = strDeletePlayer == null ? null : UUID.fromString(strDeletePlayer);
				bans.add(new BanModel(sanctionId, punishedUuid, punisherUuid, reason, creationDate, expiration,
						isDeleted, deleteReason, deletePlayer));
			}
			return bans;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"getPlayerGivenBans: SQLException while retrieving bans for player " + uuid, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns the number of bans specific player has
	 * 
	 * @param uuid
	 *                  The UUID of the player
	 * @param model
	 *                  The connection
	 * @return The number of bans specific player has.
	 * @throws SQLException
	 *                          If error
	 */
	public long getBansCount(UUID uuid, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT COUNT(report_id) AS number FROM " + TABLE + " WHERE punished_uuid=?";
			s = c.prepareStatement(sql);
			s.setString(1, uuid.toString());

			rs = s.executeQuery();
			if (rs.next())
				return rs.getLong("number");
			return 0;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"getBansCount: SQLException while retrieving number of bans for player " + uuid, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns the number of bans specific player has given
	 * 
	 * @param uuid
	 *                  The UUID of the player
	 * @param model
	 *                  The connection
	 * @return The number of bans specific player has.
	 * @throws SQLException
	 *                          If error
	 */
	public long getBansGivenCount(UUID uuid, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT COUNT(report_id) AS number FROM " + TABLE + " WHERE punisher_uuid=?";
			s = c.prepareStatement(sql);
			s.setString(1, uuid.toString());

			rs = s.executeQuery();
			if (rs.next())
				return rs.getLong("number");
			return 0;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"getBansGivenCount: SQLException while retrieving number of bans for player " + uuid, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Add a new ban
	 * 
	 * @param banModel
	 *                     The ban model
	 * @param model
	 *                     The connection
	 * @return The id of the ban
	 * @throws SQLException
	 *                          If error
	 */
	public long addBan(BanModel banModel, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "INSERT INTO " + TABLE + " (punished_uuid, punisher_uuid, reason, expiration)"
					+ " VALUES (?, ?, ?, ?)";
			s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			s.setString(1, banModel.getPunishedUuid().toString());
			s.setString(2, banModel.getPunisherUuid() == null ? null : banModel.getPunisherUuid().toString());
			s.setString(3, banModel.getReason());
			s.setTimestamp(4, banModel.getExpiration());

			s.executeUpdate();
			rs = s.getGeneratedKeys();
			if (rs.next())
				return rs.getLong(1);
			return 0;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"addBan: SQLException while adding ban for player " + banModel.getPunishedUuid(), ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Stop an existing ban
	 * 
	 * @param banModel
	 *                     The ban model
	 * @param model
	 *                     The connection
	 * @return The row number that has been affected
	 * @throws SQLException
	 *                          If error
	 */
	public int stopBan(BanModel banModel, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "UPDATE " + TABLE + " SET is_deleted=TRUE, delete_reason=?, delete_player=?"
					+ " WHERE sanction_id=?";
			s = c.prepareStatement(sql);
			s.setString(1, banModel.getDeleteReason());
			s.setString(2, banModel.getDeletePlayer() == null ? null : banModel.getDeletePlayer().toString());
			s.setLong(3, banModel.getSanctionId());

			return s.executeUpdate();
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "stopBan: SQLException while stopping ban " + banModel.getSanctionId(),
					ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns all players' uuid that got banned at least one time
	 * 
	 * @param model
	 *                  The connection
	 * @return All players' uuid that got banned at least one
	 * @throws SQLException
	 *                          If error
	 */
	public List<UUID> getPlayersUUIDBannedOnce(DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT punished_uuid AS punished_uuid FROM " + TABLE + " GROUP BY punished_uuid";
			s = c.prepareStatement(sql);

			rs = s.executeQuery();
			List<UUID> bans = new ArrayList<>();
			while (rs.next()) {
				// Exist
				UUID uuid = UUID.fromString(rs.getString("punished_uuid"));
				bans.add(uuid);
			}
			return bans;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "getPlayersUUIDBannedOnce: SQLException while retrieving bans", ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Close connection
	 */
	public void close(Connection c, PreparedStatement s, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			if (s != null)
				s.close();
			if (c != null)
				c.close();
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Error while closing database c: ", ex);
		}
	}
}
