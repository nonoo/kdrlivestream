package kdrlivestream;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBManager {
	private static final Log log = LogFactory.getLog(DBManager.class);
	private static BasicDataSource basicDS = null;

	static {
		if (KDRLiveStream.config == null) {
			log.error("config not initialized, can't initialize database access");
		} else {
			basicDS = new BasicDataSource();
			basicDS.setDriverClassName("com.mysql.jdbc.Driver");
			try {
				basicDS.setUsername(KDRLiveStream.config.getMySQLDBUser());
				basicDS.setPassword(KDRLiveStream.config.getMySQLDBPassword());
				basicDS.setUrl("jdbc:mysql://" + KDRLiveStream.config.getMySQLDBHost() + "/" + KDRLiveStream.config.getMySQLDBName());
			} catch (ConfigFileErrorException e) {
				log.error("error reading config variable: " + e.getMessage());
				e.printStackTrace();
			}
			basicDS.setInitialSize(1); // Set the number of concurrent DB connections.
		}
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();

		for (byte b : hash)
			formatter.format("%02x", b);

		String result = formatter.toString();
		formatter.close();
		
		return result;
	}

	private static String calculateSHA1(String password) {
		String sha1 = "";

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(password.getBytes("UTF-8"));
			sha1 = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sha1;
	}

	public int getUserIndex(String userName) {
		Connection dbConnection = null;
		int result = -1;
		PreparedStatement dbStatement = null;
		ResultSet dbResultSet = null;

		log.info("getting user index for user " + userName + "...");

		if (basicDS == null) {
			log.error("database not initialized!");
			return -1;
		}

		try {
			dbConnection = basicDS.getConnection();
			dbStatement = dbConnection.prepareStatement("SELECT `index` FROM `" + KDRLiveStream.config.getMySQLDBTablePrefix() + "users` WHERE `email` = (?)");
			dbStatement.setString(1, userName);
			dbResultSet = dbStatement.executeQuery();

			if (dbResultSet.next()) // ResultSet not empty?
				result = dbResultSet.getInt(1);
		} catch (ConfigFileErrorException e) {
			log.error("error reading config variable: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dbResultSet != null)
					dbResultSet.close();
				if (dbStatement != null)
					dbStatement.close();
				if (dbConnection != null)
					dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (result >= 0) {
			log.info("user " + userName + " has user index " + result);
		} else {
			log.error("user index for user " + userName + " not found.");
		}
		
		return result;
	}

	public boolean isUserAuthorized(String userName, String password) {
		Connection dbConnection = null;
		boolean result = false;
		PreparedStatement dbStatement = null;
		ResultSet dbResultSet = null;

		log.info("authorizing user " + userName + "...");

		if (basicDS == null) {
			log.error("database not initialized!");
			return false;
		}

		try {
			dbConnection = basicDS.getConnection();
			dbStatement = dbConnection.prepareStatement("SELECT `passhash` FROM `" + KDRLiveStream.config.getMySQLDBTablePrefix() + "users` WHERE `email` = (?) && `enabled` = 1");
			dbStatement.setString(1, userName);
			dbResultSet = dbStatement.executeQuery();

			if (dbResultSet.next() && dbResultSet.getString(1).equals(calculateSHA1(password)))
				result = true;
		} catch (ConfigFileErrorException e) {
			log.error("error reading config variable: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dbResultSet != null)
					dbResultSet.close();
				if (dbStatement != null)
					dbStatement.close();
				if (dbConnection != null)
					dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (result) {
			log.info("user " + userName + " authorized.");
		} else {
			log.error("user " + userName + " not authorized.");
		}

		return result;
	}

	public boolean isPublishAllowedForUser(int userIndex) {
		Connection dbConnection = null;
		boolean result = false;
		PreparedStatement dbStatement = null;
		ResultSet dbResultSet = null;

		log.info("checking if user with index " + userIndex + " can publish...");

		if (basicDS == null) {
			log.error("database not initialized");
			return false;
		}

		try {
			dbConnection = basicDS.getConnection();
			dbStatement = dbConnection.prepareStatement("SELECT `canpublish` FROM `" + KDRLiveStream.config.getMySQLDBTablePrefix() + "users` WHERE `index` = (?)");
			dbStatement.setInt(1, userIndex);
			dbResultSet = dbStatement.executeQuery();

			if (dbResultSet.next()) { // ResultSet not empty?
				result = dbResultSet.getBoolean(1);
			}
		} catch (ConfigFileErrorException e) {
			log.error("error reading config variable: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dbResultSet != null)
					dbResultSet.close();
				if (dbStatement != null)
					dbStatement.close();
				if (dbConnection != null)
					dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (result) {
			log.info("user with index " + userIndex + " can publish.");
		} else {
			log.error("user with index " + userIndex + " can't publish.");
		}
		
		return result;
	}

	public boolean isMultipleInstancesAllowedForUser(int userIndex) {
		Connection dbConnection = null;
		boolean result = false;
		PreparedStatement dbStatement = null;
		ResultSet dbResultSet = null;

		log.info("checking if user with index " + userIndex + " can have multiple instances...");

		if (basicDS == null) {
			log.error("database not initialized");
			return false;
		}

		try {
			dbConnection = basicDS.getConnection();
			dbStatement = dbConnection.prepareStatement("SELECT `allowmultipleinstances` FROM `" + KDRLiveStream.config.getMySQLDBTablePrefix() + "users` WHERE `index` = (?)");
			dbStatement.setInt(1, userIndex);
			dbResultSet = dbStatement.executeQuery();

			if (dbResultSet.next()) { // ResultSet not empty?
				result = dbResultSet.getBoolean(1);
			}
		} catch (ConfigFileErrorException e) {
			log.error("error reading config variable: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dbResultSet != null)
					dbResultSet.close();
				if (dbStatement != null)
					dbStatement.close();
				if (dbConnection != null)
					dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (result) {
			log.info("user with index " + userIndex + " can have multiple instances.");
		} else {
			log.error("user with index " + userIndex + " can't have multiple instances.");
		}
		
		return result;
	}

	public void updateLastSeenForUser(String streamName, int userIndex, boolean userIsPublishing, boolean publicStream) {
		Connection dbConnection = null;
		int result = 0;
		PreparedStatement dbStatement = null;
		ResultSet dbResultSet = null;

		//log.info("updating last seen info for user with index " + userIndex + "...");

		if (basicDS == null) {
			log.error("database not initialized");
			return;
		}

		try {
			dbConnection = basicDS.getConnection();
			dbStatement = dbConnection.prepareStatement("REPLACE INTO `" + KDRLiveStream.config.getMySQLDBTablePrefix() + "lastseen` (`streamname`, `userindex`, `userispublishing`, `public`, `lastseen`) values ((?), (?), (?), (?), NOW())");
			dbStatement.setString(1, streamName);
			dbStatement.setInt(2, userIndex);
			dbStatement.setBoolean(3, userIsPublishing);
			dbStatement.setBoolean(4, publicStream);
			result = dbStatement.executeUpdate(); // Note: result will be non-zero if update was successful.
		} catch (ConfigFileErrorException e) {
			log.error("error reading config variable: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dbResultSet != null)
					dbResultSet.close();
				if (dbStatement != null)
					dbStatement.close();
				if (dbConnection != null)
					dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (result == 0) {
			log.error("last seen info update for user with index " + userIndex + " failed.");
		} else {
			//log.info("last seen info for user with index " + userIndex + " updated.");
		}
	}
}
