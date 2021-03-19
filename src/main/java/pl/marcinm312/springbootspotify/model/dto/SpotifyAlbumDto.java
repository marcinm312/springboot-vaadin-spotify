package pl.marcinm312.springbootspotify.model.dto;

public class SpotifyAlbumDto {

	private String trackName;
	private String imageUrl;

	public SpotifyAlbumDto(String trackName, String imageUrl) {
		this.trackName = trackName;
		this.imageUrl = imageUrl;
	}

	public SpotifyAlbumDto() {
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
