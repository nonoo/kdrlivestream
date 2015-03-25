package kdrlivestream;

import java.io.File;
import java.io.IOException;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class Config {
	private static Wini ini = null;

	Config() throws ConfigFileErrorException {
		File iniFile = new File(System.getProperty("red5.config_root") + "/kdrlivestream.ini");

		if (!iniFile.exists()) {
		     throw new ConfigFileErrorException("config file not found at " + iniFile.getAbsolutePath());
		} else {
			try {
				ini = new Wini(iniFile);
			} catch (InvalidFileFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	boolean getAllowOnlyOneInstancePerUser() throws ConfigFileErrorException {
		String value;

		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");

		value = ini.get("general", "allowonlyoneinstanceperuser");
		if (value == null)
			return true;
		
		return (value.equals("1") || value.equals("yes"));
	}

	boolean getStoreLastSeenInfoInDB() throws ConfigFileErrorException {
		String value;

		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");

		value = ini.get("general", "storelastseeninfoindb");
		if (value == null)
			return false;
		
		return (value.equals("1") || value.equals("yes"));
	}

	String getMySQLDBHost() throws ConfigFileErrorException {
		String value;

		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");
		
		value = ini.get("mysqldb", "host");
		if (value == null)
			return "";

		return value;
	}

	String getMySQLDBName() throws ConfigFileErrorException {
		String value;

		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");
		
		value = ini.get("mysqldb", "dbname");
		if (value == null)
			return "";

		return value;
	}

	String getMySQLDBUser() throws ConfigFileErrorException {
		String value;

		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");
		
		value = ini.get("mysqldb", "user");
		if (value == null)
			return "";

		return value;
	}

	String getMySQLDBPassword() throws ConfigFileErrorException {
		String value;

		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");
		
		value = ini.get("mysqldb", "password");
		if (value == null)
			return "";

		return value;
	}

	String getMySQLDBTablePrefix() throws ConfigFileErrorException {
		String value;

		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");

		value = ini.get("mysqldb", "tableprefix");
		if (value == null)
			return "kdrlivestream-";

		return value;
	}
}
