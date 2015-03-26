package kdrlivestream;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.stream.IStreamPlaybackSecurity;

public class AuthPlaySec implements IStreamPlaybackSecurity {
	private static final Log log = LogFactory.getLog(AuthPubSec.class);

	private void closeAlreadyOpenedStreamsForUser(int userIndex, String streamName) {
		Set<IConnection> connections = Red5.getConnectionLocal().getScope().getClientConnections();

		for (IConnection conn : connections) {
			Object connUserIndex = conn.getAttribute("userIndex");
			Object connUserName = conn.getAttribute("userName");
			Object connStreamName = conn.getAttribute("streamName");
			
			if (connUserIndex != null && connUserIndex.equals(userIndex) &&
					connStreamName != null && connStreamName.equals(streamName)) {
				log.info("closing already opened stream for user " + connUserName);
				conn.close();
			}
		}
	}

	private boolean isPublicStream(String streamName) {
		Set<IConnection> connections = Red5.getConnectionLocal().getScope().getClientConnections();

		for (IConnection conn : connections) {
			Object connUserAuthorized = conn.getAttribute("userAuthorized");
			Object connIsPublishing = conn.getAttribute("userIsPublishing");
			Object connPublicStream = conn.getAttribute("publicStream");
			Object connStreamName = conn.getAttribute("streamName");

			if (connUserAuthorized != null && (boolean)connUserAuthorized == true &&
					connIsPublishing != null && (boolean)connIsPublishing == true &&
					connStreamName != null && connStreamName.equals(streamName) &&
					connPublicStream != null && (boolean)connPublicStream == true) {
				return true;
			}
		}
		return false;
	}

	// Called by Red5 to check if user is allowed to play stream.
	@Override
	public boolean isPlaybackAllowed(IScope scope, String name, int start, int length, boolean flushPlaylist) {
		IConnection conn = Red5.getConnectionLocal();
		Object userAuthorized = conn.getAttribute("userAuthorized");
		Object userIndex = conn.getAttribute("userIndex");
		Object userName = conn.getAttribute("userName");

		if (isPublicStream(name)) {
			log.info("stream " + name + " is public, allowing access");
			return true;
		}
		
		if (userIndex == null || userAuthorized == null || (boolean)userAuthorized != true) {
			log.info("user is not authorized and stream " + name + " is not public, closing connection");
			return false;
		}

		try {
			if (KDRLiveStream.config != null && KDRLiveStream.config.getAllowOnlyOneInstancePerUser())
				closeAlreadyOpenedStreamsForUser((int)userIndex, name);
		} catch (ConfigFileErrorException e) {
			log.error("error reading config variable: " + e.getMessage());
			e.printStackTrace();
		}

		// Now we know what stream the client wants to access, so storing it as a connection attribute.
		conn.setAttribute("streamName", name);
		if (userName != null)
			log.info("user " + (String)userName + " is opening stream " + name);

		return true;
	}
}
