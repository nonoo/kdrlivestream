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

	String getMySQLDBHost() throws ConfigFileErrorException {
		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");
		
		return ini.get("mysqldb", "host");
	}

	String getMySQLDBName() throws ConfigFileErrorException {
		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");
		
		return ini.get("mysqldb", "dbname");
	}

	String getMySQLDBUser() throws ConfigFileErrorException {
		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");
		
		return ini.get("mysqldb", "user");
	}

	String getMySQLDBPassword() throws ConfigFileErrorException {
		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");
		
		return ini.get("mysqldb", "password");
	}

	String getMySQLDBTablePrefix() throws ConfigFileErrorException {
		if (ini == null)
			throw new ConfigFileErrorException("config file not opened");

		return ini.get("mysqldb", "tableprefix");
	}
}
