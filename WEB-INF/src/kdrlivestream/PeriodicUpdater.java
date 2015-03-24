package kdrlivestream;

import org.red5.server.api.IConnection;
import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;

// This should be called periodically to update last seen info for the given user.

public class PeriodicUpdater implements IScheduledJob {
	IConnection connection = null;

	PeriodicUpdater(IConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public void execute(ISchedulingService service)	throws CloneNotSupportedException {
		Object streamName = null;
		Object userIndex = null;
		DBManager dbManager = new DBManager();

		if (connection == null)
			return;

		streamName = connection.getAttribute("streamName");
		userIndex = connection.getAttribute("userIndex");

		if (streamName == null || userIndex == null)
			return;

		dbManager.updateLastSeenForUser((String)streamName, (int)userIndex);
	}
}
