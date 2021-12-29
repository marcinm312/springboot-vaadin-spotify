package pl.marcinm312.springbootspotify.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/").authenticated()
				.antMatchers("/log-out", "/login**").permitAll()
				.and().oauth2Login()
				.and().csrf().disable()
				.sessionManagement().maximumSessions(10000).maxSessionsPreventsLogin(false)
				.expiredUrl("/oauth2/authorization/spotify").sessionRegistry(sessionRegistry());
	}

	@Bean
	SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
	}
}
