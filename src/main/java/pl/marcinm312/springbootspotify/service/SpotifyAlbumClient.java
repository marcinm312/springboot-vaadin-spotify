package pl.marcinm312.springbootspotify.service;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.marcinm312.springbootspotify.model.SpotifyAlbum;
import pl.marcinm312.springbootspotify.model.dto.SpotifyAlbumDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpotifyAlbumClient {

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	public List<SpotifyAlbumDto> getAlbumsByAuthor(OAuth2Authentication details, String authorName) {
		String jwt = ((OAuth2AuthenticationDetails) details.getDetails()).getTokenValue();
		log.info("user=" + details.getName());

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + jwt);
		HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

		String url = "https://api.spotify.com/v1/search?q=" + authorName +  "&type=track&market=PL&limit=50&offset=0";
		log.info("url=" + url);

		ResponseEntity<SpotifyAlbum> exchange = restTemplate.exchange(
				url,
				HttpMethod.GET,
				httpEntity,
				SpotifyAlbum.class);
		log.info("status=" + exchange.getStatusCode());

		SpotifyAlbum spotifyAlbum = exchange.getBody();

		if (spotifyAlbum == null) {
			return new ArrayList<>();
		}

		return spotifyAlbum.getTracks().getItems()
				.stream()
				.map(item -> new SpotifyAlbumDto(item.getName(), item.getAlbum().getImages().get(0).getUrl()))
				.collect(Collectors.toList());
	}
}
