package pl.marcinm312.springbootspotify.model.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class SpotifyAlbumDtoTest {

	@Test
	void equalsHashCode_differentCases() {
		EqualsVerifier.forClass(SpotifyAlbumDto.class).verify();
	}
}