package pl.marcinm312.springbootspotify.model.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SpotifyAlbumDtoTest {

	@Test
	void equalsHashCode_differentCases() {
		EqualsVerifier.forClass(SpotifyAlbumDto.class).suppress(Warning.NONFINAL_FIELDS).verify();

		SpotifyAlbumDto spotifyAlbumDto1 = new SpotifyAlbumDto("track1", "http://spotify.com/track1/image");
		SpotifyAlbumDto spotifyAlbumDto2 = new SpotifyAlbumDto("track1", "http://spotify.com/track1/image");

		Assertions.assertEquals(spotifyAlbumDto1, spotifyAlbumDto2);

		spotifyAlbumDto2.setTrackName("track2");
		Assertions.assertNotEquals(spotifyAlbumDto1, spotifyAlbumDto2);

		spotifyAlbumDto2.setTrackName("track1");
		Assertions.assertEquals(spotifyAlbumDto1, spotifyAlbumDto2);
	}
}