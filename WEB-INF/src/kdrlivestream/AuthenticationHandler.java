package kdrlivestream;

import java.security.Security;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.red5.server.adapter.ApplicationLifecycle;
import org.red5.server.api.IConnection;
import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
import org.red5.server.exception.ClientRejectedException;
import org.red5.server.util.UrlQueryStringMap;

public class AuthenticationHandler extends ApplicationLifecycle {
	private static final Log log = LogFactory.getLog(AuthenticationHandler.class);
	private static DBManager dbManager = new DBManager();

	static {
		// Get security provider
		Security.addProvider(new BouncyCastleProvider());
	}

	// Called when a client connects to our app.
	public boolean appConnect(IConnection conn, Object[] params) {
		ISchedulingService scheduler = null;

		Map<String, Object> connectionParams = conn.getConnectParams();
		log.info("connection params: " + connectionParams);

		if (!connectionParams.containsKey("queryString")) {
			log.error("no connection query parameters given");
			throw new ClientRejectedException();
		}

		String rawQueryString = (String) connectionParams.get("queryString");
		log.info("queryString: " + rawQueryString);

		// Parse into a usable query string.
		UrlQueryStringMap<String, String> queryString = UrlQueryStringMap.parse(rawQueryString);

		// Get the values we want.
		String userName = queryString.get("u");
		//log.info("username: " + userName);

		String password = queryString.get("p");
		//log.info("password: " + password);

		int userIndex = dbManager.getUserIndex(userName);
		if (userIndex < 0)
			throw new ClientRejectedException();

		if (dbManager.isUserAuthorized(userIndex, password)) {
			conn.setAttribute("userIndex", userIndex);
			conn.setAttribute("publishAllowed", dbManager.isPublishAllowedForUser(userIndex));
			conn.setAttribute("scheduledJob", new PeriodicUpdater(conn));
			scheduler = (ISchedulingService)conn.getScope().getContext().getBean(ISchedulingService.BEAN_NAME);
			conn.setAttribute("scheduledPeriodicUpdate", scheduler.addScheduledJob(5000, (IScheduledJob)conn.getAttribute("scheduledJob")));
		} else
			throw new ClientRejectedException();

		return true;
	}

	public void appDisconnect(IConnection conn) {
		String scheduledPeriodicUpdate = (String) conn.getAttribute("scheduledPeriodicUpdate");
		ISchedulingService scheduler = (ISchedulingService)conn.getScope().getContext().getBean(ISchedulingService.BEAN_NAME);

		if (scheduledPeriodicUpdate != null) {
			log.info("removing scheduled periodic update job from user with index " + conn.getAttribute("userIndex"));
			scheduler.removeScheduledJob(scheduledPeriodicUpdate);
		}
	}
}
