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
import me.oddlyoko.terminator.database.model.BanIpModel;

public class BanIpManager {
	public static final String TABLE = "Terminator_banip";

	/**
	 * Get ban from id
	 * 
	 * @param sanctionId
	 *                       The id of the banip
	 * @param model
	 *                       The connection
	 * @return The banip associated with the id or null if not found
	 * @throws SQLException
	 *                          If error
	 */
	public BanIpModel getIpBan(long sanctionId, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT sanction_id, punished_ip, punisher_uuid, reason, creation_date, expiration, "
					+ "is_deleted, delete_reason, delete_player FROM " + TABLE + " WHERE sanction_id=?";
			s = c.prepareStatement(sql);
			s.setLong(1, sanctionId);

			rs = s.executeQuery();
			if (rs.next()) {
				// Exist
				String punishedIp = rs.getString("punished_ip");
				String strPunisherUuid = rs.getString("punisher_uuid");
				UUID punisherUuid = strPunisherUuid == null ? null : UUID.fromString(strPunisherUuid);
				String reason = rs.getString("reason");
				Timestamp creationDate = rs.getTimestamp("creation_date");
				Timestamp expiration = rs.getTimestamp("expiration");
				boolean isDeleted = rs.getBoolean("is_deleted");
				String deleteReason = rs.getString("delete_reason");
				String strDeletePlayer = rs.getString("delete_player");
				UUID deletePlayer = strDeletePlayer == null ? null : UUID.fromString(strDeletePlayer);
				return new BanIpModel(sanctionId, punishedIp, punisherUuid, reason, creationDate, expiration, isDeleted,
						deleteReason, deletePlayer);
			} else
				return null;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "getIpBan: SQLException while retrieving banip " + sanctionId, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Get all banips for specific ip
	 * 
	 * @param ip
	 *                  The ip banned
	 * @param model
	 *                  The connection
	 * @return All banips associated with specific ip or an empty list if not found
	 * @throws SQLException
	 *                          If error
	 */
	public List<BanIpModel> getIpBans(String ip, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT sanction_id, punished_ip, punisher_uuid, reason, creation_date, expiration, "
					+ "is_deleted, delete_reason, delete_player FROM " + TABLE
					+ " WHERE punished_ip=? ORDER BY sanction_id";
			s = c.prepareStatement(sql);
			s.setString(1, ip);

			rs = s.executeQuery();
			List<BanIpModel> bans = new ArrayList<>();
			while (rs.next()) {
				// Exist
				long sanctionId = rs.getLong("sanction_id");
				String punishedIp = rs.getString("punished_ip");
				String strPunisherUuid = rs.getString("punisher_uuid");
				UUID punisherUuid = strPunisherUuid == null ? null : UUID.fromString(strPunisherUuid);
				String reason = rs.getString("reason");
				Timestamp creationDate = rs.getTimestamp("creation_date");
				Timestamp expiration = rs.getTimestamp("expiration");
				boolean isDeleted = rs.getBoolean("is_deleted");
				String deleteReason = rs.getString("delete_reason");
				String strDeletePlayer = rs.getString("delete_player");
				UUID deletePlayer = strDeletePlayer == null ? null : UUID.fromString(strDeletePlayer);
				bans.add(new BanIpModel(sanctionId, punishedIp, punisherUuid, reason, creationDate, expiration,
						isDeleted, deleteReason, deletePlayer));
			}
			return bans;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "getIpBans: SQLException while retrieving bans for ip " + ip, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns all banips that the player has given
	 * 
	 * @param uuid
	 *                  The UUID of the player
	 * @param model
	 *                  The connection
	 * @return All banips associated with specific player or an empty list if not
	 *         found
	 * @throws SQLException
	 *                          If error
	 */
	public List<BanIpModel> getPlayerGivenIpBans(UUID uuid, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT sanction_id, punished_ip, punisher_uuid, reason, creation_date, expiration, "
					+ "is_deleted, delete_reason, delete_player FROM " + TABLE
					+ " WHERE punisher_uuid=? ORDER BY sanction_id";
			s = c.prepareStatement(sql);
			s.setString(1, uuid.toString());

			rs = s.executeQuery();
			List<BanIpModel> bans = new ArrayList<>();
			while (rs.next()) {
				// Exist
				long sanctionId = rs.getLong("sanction_id");
				String punishedIp = rs.getString("punished_ip");
				String strPunisherUuid = rs.getString("punisher_uuid");
				UUID punisherUuid = strPunisherUuid == null ? null : UUID.fromString(strPunisherUuid);
				String reason = rs.getString("reason");
				Timestamp creationDate = rs.getTimestamp("creation_date");
				Timestamp expiration = rs.getTimestamp("expiration");
				boolean isDeleted = rs.getBoolean("is_deleted");
				String deleteReason = rs.getString("delete_reason");
				String strDeletePlayer = rs.getString("delete_player");
				UUID deletePlayer = strDeletePlayer == null ? null : UUID.fromString(strDeletePlayer);
				bans.add(new BanIpModel(sanctionId, punishedIp, punisherUuid, reason, creationDate, expiration,
						isDeleted, deleteReason, deletePlayer));
			}
			return bans;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"getPlayerGivenIpBans: SQLException while retrieving ipbans for player " + uuid, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns the number of banips specific ip has
	 * 
	 * @param ip
	 *                  The ip
	 * @param model
	 *                  The connection
	 * @return The number of banips specific ip has.
	 * @throws SQLException
	 *                          If error
	 */
	public long getIpBansCount(String ip, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT COUNT(report_id) AS number FROM " + TABLE + " WHERE punished_ip=?";
			s = c.prepareStatement(sql);
			s.setString(1, ip);

			rs = s.executeQuery();
			if (rs.next())
				return rs.getLong("number");
			return 0;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"getIpBansCount: SQLException while retrieving number of bans for ip " + ip, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns the number of banips specific ip has given
	 * 
	 * @param uuid
	 *                  The UUID of the player
	 * @param model
	 *                  The connection
	 * @return The number of bans specific player has.
	 * @throws SQLException
	 *                          If error
	 */
	public long getIpBansGivenCount(UUID uuid, DatabaseModel model) throws SQLException {
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
					"getIpBansCount: SQLException while retrieving number of ip bans for player " + uuid, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Add a new ban
	 * 
	 * @param banIpModel
	 *                       The banIp model
	 * @param model
	 *                       The connection
	 * @return The id of the ban
	 * @throws SQLException
	 *                          If error
	 */
	public long addIpBan(BanIpModel banIpModel, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "INSERT INTO " + TABLE + " (punished_ip, punisher_uuid, reason, expiration)"
					+ " VALUES (?, ?, ?, ?)";
			s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			s.setString(1, banIpModel.getPunishedIp());
			s.setString(2, banIpModel.getPunisherUuid() == null ? null : banIpModel.getPunisherUuid().toString());
			s.setString(3, banIpModel.getReason());
			s.setTimestamp(4, banIpModel.getExpiration());

			s.executeUpdate();
			rs = s.getGeneratedKeys();
			if (rs.next())
				return rs.getLong(1);
			return 0;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"addIpBan: SQLException while adding ip ban for ip " + banIpModel.getPunishedIp(), ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Stop an existing banip
	 * 
	 * @param banIpModel
	 *                       The banIp model
	 * @param model
	 *                       The connection
	 * @return The row number that has been affected
	 * @throws SQLException
	 *                          If error
	 */
	public int stopIpBan(BanIpModel banIpModel, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "UPDATE " + TABLE + " SET is_deleted=TRUE, delete_reason=?, delete_player=?"
					+ " WHERE sanction_id=?";
			s = c.prepareStatement(sql);
			s.setString(1, banIpModel.getDeleteReason());
			s.setString(2, banIpModel.getDeletePlayer() == null ? null : banIpModel.getDeletePlayer().toString());
			s.setLong(3, banIpModel.getSanctionId());

			return s.executeUpdate();
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"stopIpBan: SQLException while stopping ip ban " + banIpModel.getSanctionId(), ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns all ip bans that are present in database
	 * 
	 * @param model
	 *                  The connection
	 * @return All ips that are present in database
	 * @throws SQLException
	 *                          If error
	 */
	public List<BanIpModel> getIpsBanned(DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT sanction_id, punished_ip, punisher_uuid, reason, creation_date, expiration, "
					+ "is_deleted, delete_reason, delete_player FROM " + TABLE + " GROUP BY punished_ip";
			s = c.prepareStatement(sql);

			rs = s.executeQuery();
			List<BanIpModel> ipBans = new ArrayList<>();
			while (rs.next()) {
				// Exist
				long sanctionId = rs.getLong("sanction_id");
				String punishedIp = rs.getString("punished_ip");
				String strPunisherUuid = rs.getString("punisher_uuid");
				UUID punisherUuid = strPunisherUuid == null ? null : UUID.fromString(strPunisherUuid);
				String reason = rs.getString("reason");
				Timestamp creationDate = rs.getTimestamp("creation_date");
				Timestamp expiration = rs.getTimestamp("expiration");
				boolean isDeleted = rs.getBoolean("is_deleted");
				String deleteReason = rs.getString("delete_reason");
				String strDeletePlayer = rs.getString("delete_player");
				UUID deletePlayer = strDeletePlayer == null ? null : UUID.fromString(strDeletePlayer);
				ipBans.add(new BanIpModel(sanctionId, punishedIp, punisherUuid, reason, creationDate, expiration,
						isDeleted, deleteReason, deletePlayer));
			}
			return ipBans;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "getIpsBanned: SQLException while retrieving ip bans", ex);
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
