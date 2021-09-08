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

	@Override
	public String toString() {
		return "SpotifyAlbumDto{" +
				"trackName='" + trackName + '\'' +
				", imageUrl='" + imageUrl + '\'' +
				'}';
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SpotifyAlbumDto)) return false;

		SpotifyAlbumDto that = (SpotifyAlbumDto) o;

		if (getTrackName() != null ? !getTrackName().equals(that.getTrackName()) : that.getTrackName() != null)
			return false;
		return getImageUrl() != null ? getImageUrl().equals(that.getImageUrl()) : that.getImageUrl() == null;
	}

	@Override
	public final int hashCode() {
		int result = getTrackName() != null ? getTrackName().hashCode() : 0;
		result = 31 * result + (getImageUrl() != null ? getImageUrl().hashCode() : 0);
		return result;
	}
}
