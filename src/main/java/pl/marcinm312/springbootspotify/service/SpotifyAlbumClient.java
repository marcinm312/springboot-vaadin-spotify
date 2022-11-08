package pl.marcinm312.springbootspotify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.marcinm312.springbootspotify.model.SpotifyAlbum;
import pl.marcinm312.springbootspotify.model.dto.SpotifyAlbumDto;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SpotifyAlbumClient {

	private final RestTemplate restTemplate;
	private final OAuth2AuthorizedClientService authorizedClientService;


	public List<SpotifyAlbumDto> getAlbumsByAuthor(OAuth2AuthenticationToken authenticationToken, String authorName) {

		if (StringUtils.isEmpty(authorName)) {
			return new ArrayList<>();
		}

		String jwt = getJwtFromAuthorization(authenticationToken);

		ResponseEntity<SpotifyAlbum> exchange = getSpotifyAlbumResponseEntity(authorName, jwt);
		HttpStatus httpStatus = exchange.getStatusCode();
		log.info("status={}", httpStatus);

		if (!httpStatus.is2xxSuccessful()) {
			throw new HttpClientErrorException(httpStatus);
		}

		SpotifyAlbum spotifyAlbum = exchange.getBody();

		if (spotifyAlbum == null) {
			return new ArrayList<>();
		}

		return spotifyAlbum.getTracks().getItems()
				.stream()
				.map(item -> new SpotifyAlbumDto(item.getName(), item.getAlbum().getImages().get(0).getUrl()))
				.toList();
	}

	private String getJwtFromAuthorization(OAuth2AuthenticationToken authenticationToken) {

		if (authenticationToken == null) {
			return null;
		}
		String authenticationName = authenticationToken.getName();
		log.info("user={}", authenticationName);
		OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient
				(authenticationToken.getAuthorizedClientRegistrationId(), authenticationName);
		return client.getAccessToken().getTokenValue();
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
