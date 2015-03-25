package kdrlivestream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.adapter.IApplication;
import org.red5.server.api.IConnection;
import org.red5.server.api.scheduling.ISchedulingService;
import org.red5.server.api.scope.IScope;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class KDRLiveStream extends ApplicationAdapter implements ApplicationContextAware {
	private static final Log log = LogFactory.getLog(KDRLiveStream.class);
	private ApplicationContext applicationContext;

	public static Config config = null;

	public KDRLiveStream() {
		try {
			config = new Config();
		} catch (ConfigFileErrorException e) {
			log.error("error opening config file: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean appStart(IScope app) {
		ISchedulingService scheduler = null;

		log.info("kdrlivestream app started.");

		addListener((IApplication)applicationContext.getBean("authHandler"));
		registerStreamPublishSecurity(new AuthPubSec());
		registerStreamPlaybackSecurity(new AuthPlaySec());

		scheduler = (ISchedulingService)app.getContext().getBean(ISchedulingService.BEAN_NAME);
		scheduler.addScheduledJob(5000, new PeriodicLastSeenUpdater());

		return super.appStart(app);
	}

	// Called when a client connects.
	@Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
		log.info("new connection attempt from: " + conn.getRemoteAddress());

		return super.connect(conn, scope, params);
	}

	// Called when a client disconnects.
	@Override
	public void disconnect(IConnection conn, IScope scope) {
		log.info("connection closed: " + conn.getRemoteAddress());

		super.disconnect(conn, scope);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
