package pl.marcinm312.springbootspotify.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.marcinm312.springbootspotify.model.SpotifyAlbum;
import pl.marcinm312.springbootspotify.model.dto.SpotifyAlbumDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpotifyAlbumClient {

	private final RestTemplate restTemplate;
	private final OAuth2AuthorizedClientService authorizedClientService;

	@Autowired
	public SpotifyAlbumClient(RestTemplate restTemplate, OAuth2AuthorizedClientService authorizedClientService) {
		this.restTemplate = restTemplate;
		this.authorizedClientService = authorizedClientService;
	}

	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	public List<SpotifyAlbumDto> getAlbumsByAuthor(OAuth2AuthenticationToken authenticationToken, String authorName) {

		if (StringUtils.isEmpty(authorName)) {
			return new ArrayList<>();
		}

		String jwt = null;
		if (authenticationToken != null) {
			String authenticationName = authenticationToken.getName();
			log.info("user={}", authenticationName);
			OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient
					(authenticationToken.getAuthorizedClientRegistrationId(), authenticationName);
			jwt = client.getAccessToken().getTokenValue();
		}

		ResponseEntity<SpotifyAlbum> exchange = getSpotifyAlbumResponseEntity(authorName, jwt);
		log.info("status={}", exchange.getStatusCode());

		SpotifyAlbum spotifyAlbum = exchange.getBody();

		if (spotifyAlbum == null) {
			return new ArrayList<>();
		}

		return spotifyAlbum.getTracks().getItems()
				.stream()
				.map(item -> new SpotifyAlbumDto(item.getName(), item.getAlbum().getImages().get(0).getUrl()))
				.collect(Collectors.toList());
	}

	private ResponseEntity<SpotifyAlbum> getSpotifyAlbumResponseEntity(String authorName, String jwt) {
		HttpHeaders httpHeaders = new HttpHeaders();
		if (jwt != null) {
			httpHeaders.add("Authorization", "Bearer " + jwt);
		}
		HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

		String url = "https://api.spotify.com/v1/search?q=" + authorName + "&type=track&market=PL&limit=50&offset=0";
		log.info("url={}", url);

		return restTemplate.exchange(
				url,
				HttpMethod.GET,
				httpEntity,
				SpotifyAlbum.class);
	}
}
