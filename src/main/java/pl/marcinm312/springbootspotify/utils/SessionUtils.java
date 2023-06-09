package pl.marcinm312.springbootspotify.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@RequiredArgsConstructor
@Slf4j
@Component
public class SessionUtils {

	private final SessionRegistry sessionRegistry;

	public void expireCurrentSession() {
		String currentSessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		SessionInformation sessionInformation = sessionRegistry.getSessionInformation(currentSessionId);
		if (sessionInformation != null) {
			sessionInformation.expireNow();
		}
		log.info("Session {} has been expired", currentSessionId);
	}
}
