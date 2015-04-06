package kdrlivestream;

import java.security.Security;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.red5.server.adapter.ApplicationLifecycle;
import org.red5.server.api.IConnection;
import org.red5.server.api.scheduling.ISchedulingService;
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
		Map<String, Object> connectionParams = conn.getConnectParams();
		UrlQueryStringMap<String, String> queryString;
		log.info("connection params: " + connectionParams);

		if (connectionParams.containsKey("queryString")) {
			String rawQueryString = (String) connectionParams.get("queryString");
			//log.info("queryString: " + rawQueryString);

			// Parse into a usable query string.
			try {
				queryString = UrlQueryStringMap.parse(rawQueryString);
			} catch (ArrayIndexOutOfBoundsException e) {
				return true;
			}

			String userName = queryString.get("u");
			String password = queryString.get("p");

			if (userName != null && password != null) {
				int userIndex = dbManager.getUserIndex(userName);
				if (userIndex < 0) {
					userName.replace("_at_", "@");
					userIndex = dbManager.getUserIndex(userName);
				}
				conn.setAttribute("userName", userName);
				conn.setAttribute("userIndex", userIndex);

				if (dbManager.isUserAuthorized(userName, password)) {
					conn.setAttribute("userAuthorized", true);
					conn.setAttribute("publishAllowed", dbManager.isPublishAllowedForUser(userIndex));
					conn.setAttribute("multipleInstancesAllowed", dbManager.isMultipleInstancesAllowedForUser(userIndex));

					String publicStream = queryString.get("pub");
					if (publicStream != null)
						conn.setAttribute("publicStream", publicStream.equals("1"));
				}
			}
		}

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
