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
import me.oddlyoko.terminator.database.model.KickModel;

public class KickManager {
	public static final String TABLE = "Terminator_kick";

	/**
	 * Get kick from id
	 * 
	 * @param sanctionId
	 *                       The id of the kick
	 * @param model
	 *                       The connection
	 * @return The kick associated with the id or null if not found
	 * @throws SQLException
	 *                          If error
	 */
	public KickModel getKick(long sanctionId, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT sanction_id, punished_uuid, punisher_uuid, reason, creation_date FROM " + TABLE
					+ " WHERE sanction_id=? ORDER BY sanction_id";
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
				return new KickModel(sanctionId, punishedUuid, punisherUuid, reason, creationDate);
			} else
				return null;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "getKick: SQLException while retrieving kick " + sanctionId, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Get all kicks for specific player
	 * 
	 * @param uuid
	 *                  The UUID of the player
	 * @param model
	 *                  The connection
	 * @return All kicks associated with specific player or an empty list if not
	 *         found
	 * @throws SQLException
	 *                          If error
	 */
	public List<KickModel> getPlayerKicks(UUID uuid, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT sanction_id, punished_uuid, punisher_uuid, reason, creation_date FROM " + TABLE
					+ " WHERE punished_uuid=? ORDER BY sanction_id";
			s = c.prepareStatement(sql);
			s.setString(1, uuid.toString());

			rs = s.executeQuery();
			List<KickModel> kicks = new ArrayList<>();
			while (rs.next()) {
				// Exist
				long sanctionId = rs.getLong("sanction_id");
				UUID punishedUuid = UUID.fromString(rs.getString("punished_uuid"));
				String strPunisherUuid = rs.getString("punisher_uuid");
				UUID punisherUuid = strPunisherUuid == null ? null : UUID.fromString(strPunisherUuid);
				String reason = rs.getString("reason");
				Timestamp creationDate = rs.getTimestamp("creation_date");
				kicks.add(new KickModel(sanctionId, punishedUuid, punisherUuid, reason, creationDate));
			}
			return kicks;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"getPlayerKicks: SQLException while retrieving kicks for player " + uuid, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns all kicks that the player has given
	 * 
	 * @param uuid
	 *                  The UUID of the player
	 * @param model
	 *                  The connection
	 * @return All kicks associated with specific player or an empty list if not
	 *         found
	 * @throws SQLException
	 *                          If error
	 */
	public List<KickModel> getPlayerGivenKicks(UUID uuid, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT sanction_id, punished_uuid, punisher_uuid, reason, creation_date FROM " + TABLE
					+ " WHERE punisher_uuid=?";
			s = c.prepareStatement(sql);
			s.setString(1, uuid.toString());

			rs = s.executeQuery();
			List<KickModel> kicks = new ArrayList<>();
			while (rs.next()) {
				// Exist
				long sanctionId = rs.getLong("sanction_id");
				UUID punishedUuid = UUID.fromString(rs.getString("punished_uuid"));
				String strPunisherUuid = rs.getString("punisher_uuid");
				UUID punisherUuid = strPunisherUuid == null ? null : UUID.fromString(strPunisherUuid);
				String reason = rs.getString("reason");
				Timestamp creationDate = rs.getTimestamp("creation_date");
				kicks.add(new KickModel(sanctionId, punishedUuid, punisherUuid, reason, creationDate));
			}
			return kicks;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"getPlayerGivenKicks: SQLException while retrieving kicks for player " + uuid, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns the number of kicks specific player has
	 * 
	 * @param uuid
	 *                  The UUID of the player
	 * @param model
	 *                  The connection
	 * @return The number of kicks specific player has.
	 * @throws SQLException
	 *                          If error
	 */
	public long getKicksCount(UUID uuid, DatabaseModel model) throws SQLException {
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
					"getKicksCount: SQLException while retrieving number of kicks for player " + uuid, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns the number of kicks specific player has given
	 * 
	 * @param uuid
	 *                  The UUID of the player
	 * @param model
	 *                  The connection
	 * @return The number of kicks specific player has.
	 * @throws SQLException
	 *                          If error
	 */
	public long getKicksGivenCount(UUID uuid, DatabaseModel model) throws SQLException {
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
					"getKicksGivenCount: SQLException while retrieving number of kicks for player " + uuid, ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Add a new kick
	 * 
	 * @param kickModel
	 *                      The kick model
	 * @param model
	 *                      The connection
	 * @return The id of the kick
	 * @throws SQLException
	 *                          If error
	 */
	public long addKick(KickModel kickModel, DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "INSERT INTO " + TABLE + " (punished_uuid, punisher_uuid, reason)" + " VALUES (?, ?, ?)";
			s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			s.setString(1, kickModel.getPunishedUuid().toString());
			s.setString(2, kickModel.getPunisherUuid() == null ? null : kickModel.getPunisherUuid().toString());
			s.setString(3, kickModel.getReason());

			s.executeUpdate();
			rs = s.getGeneratedKeys();
			if (rs.next())
				return rs.getLong(1);
			return 0;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"addKick: SQLException while adding kick for player " + kickModel.getPunishedUuid(), ex);
			throw ex;
		} finally {
			close(c, s, rs);
		}
	}

	/**
	 * Returns all players' uuid that got kicked at least one time
	 * 
	 * @param model
	 *                  The connection
	 * @return All players' uuid that got banned at least one
	 * @throws SQLException
	 *                          If error
	 */
	public List<UUID> getPlayersUUIDKickedOnce(DatabaseModel model) throws SQLException {
		Connection c = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			c = model.getConnection();

			String sql = "SELECT punished_uuid FROM " + TABLE + " GROUP BY punished_uuid";
			s = c.prepareStatement(sql);

			rs = s.executeQuery();
			List<UUID> kicks = new ArrayList<>();
			while (rs.next()) {
				// Exist
				UUID uuid = UUID.fromString(rs.getString("punished_uuid"));
				kicks.add(uuid);
			}
			return kicks;
		} catch (SQLException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "getPlayersUUIDKickedOnce: SQLException while retrieving kicks", ex);
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
