package kdrlivestream;

import java.util.Set;

import org.red5.server.api.IConnection;
import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
import org.red5.server.api.scope.IScope;

// This gets called periodically to update last seen info for all users.

public class PeriodicLastSeenUpdater implements IScheduledJob {
	IConnection connection = null;
	IScope scope = null;

	PeriodicLastSeenUpdater(IScope scope) {
		this.scope = scope;
	}
	
	@Override
	public void execute(ISchedulingService service)	throws CloneNotSupportedException {
		Set<IConnection> connections = scope.getClientConnections();
		DBManager dbManager = new DBManager();

		for (IConnection conn : connections) {
			Object connUserIndex = conn.getAttribute("userIndex");
			Object connStreamName = conn.getAttribute("streamName");
			Object connUserIsPublishing = conn.getAttribute("userIsPublishing");
			Object connPublicStream = conn.getAttribute("publicStream");

			if (connUserIsPublishing == null)
				connUserIsPublishing = false;

			if (connPublicStream == null)
				connPublicStream = false;
			
			if (connUserIndex != null && connStreamName != null)
				dbManager.updateLastSeenForUser((String)connStreamName, (int)connUserIndex, (boolean)connUserIsPublishing, (boolean)connPublicStream);
		}
	}
}
