package pl.marcinm312.springbootspotify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class BeansConfig {

	@Value("${spotify.timeout}")
	private Long spotifyTimeout;

	@Bean
	public RestTemplate restTemplate() {

		return new RestTemplateBuilder()
				.setConnectTimeout(Duration.ofSeconds(spotifyTimeout))
				.setReadTimeout(Duration.ofSeconds(spotifyTimeout))
				.build();
	}
}
