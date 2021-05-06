package pl.marcinm312.springbootspotify.utils;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class SessionUtils {

	private final SessionRegistry sessionRegistry;
	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public SessionUtils(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}

	public void expireCurrentSession() {
		String currentSessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		SessionInformation sessionInformation = sessionRegistry.getSessionInformation(currentSessionId);
		sessionInformation.expireNow();
		log.info("Session {} has been expired", currentSessionId);
	}
}
