package kdrlivestream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.stream.IStreamPublishSecurity;

public class AuthPubSec implements IStreamPublishSecurity {
	private static final Log log = LogFactory.getLog(AuthPubSec.class);

	// Called by Red5 to check if user is allowed to publish.
	@Override
	public boolean isPublishAllowed(IScope scope, String name, String mode) {
		IConnection conn = Red5.getConnectionLocal();
		Object publishAllowed = conn.getAttribute("publishAllowed");
		Object userName = conn.getAttribute("userName");

		// Now we know what stream the client wants to access, so storing it as a connection attribute.
		conn.setAttribute("streamName", name);

		if (publishAllowed == null)
			return false;
		
		if (userName != null)
			log.info("user " + (String)userName + " authorized to publish stream " + name + ": " + (publishAllowed.equals(true) ? "yes" : "no"));

		conn.setAttribute("userIsPublishing", true);
		
		return publishAllowed.equals(true);
	}

}
