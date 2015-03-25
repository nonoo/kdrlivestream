package kdrlivestream;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;

// This gets called periodically to update last seen info for all users.

public class PeriodicLastSeenUpdater implements IScheduledJob {
	private static final Log log = LogFactory.getLog(PeriodicLastSeenUpdater.class);
	IConnection connection = null;

	@Override
	public void execute(ISchedulingService service)	throws CloneNotSupportedException {
		Set<IConnection> connections = Red5.getConnectionLocal().getScope().getClientConnections();
		DBManager dbManager = new DBManager();

		for (IConnection conn : connections) {
			Object connUserIndex = conn.getAttribute("userIndex");
			Object connUserName = conn.getAttribute("userName");
			Object connStreamName = conn.getAttribute("streamName");
			
			if (connUserIndex != null && connStreamName != null) {
				if (connUserName != null)
					log.info("updating last seen info for user " + (String)connUserName);
				dbManager.updateLastSeenForUser((String)connStreamName, (int)connUserIndex);
			}
		}
	}
}
