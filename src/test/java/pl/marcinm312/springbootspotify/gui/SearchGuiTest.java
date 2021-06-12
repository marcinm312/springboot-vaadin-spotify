package pl.marcinm312.springbootspotify.gui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.marcinm312.springbootspotify.config.BeansConfig;

import pl.marcinm312.springbootspotify.model.dto.SpotifyAlbumDto;
import pl.marcinm312.springbootspotify.service.SpotifyAlbumClient;
import pl.marcinm312.springbootspotify.testdataprovider.ResponseReaderFromFile;
import pl.marcinm312.springbootspotify.utils.SessionUtils;

import java.io.IOException;
import java.nio.file.FileSystems;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BeansConfig.class)
class SearchGuiTest {

	@MockBean
	SessionUtils sessionUtils;

	@SpyBean
	SpotifyAlbumClient spotifyAlbumClient;

	private MockRestServiceServer mockServer;

	@Autowired
	private RestTemplate restTemplate;

	@BeforeEach
	void setup() {

		MockitoAnnotations.openMocks(this);
		this.mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	void searchGuiTest_simpleSearchCase_success() throws IOException {

		String spotifyUrl = "https://api.spotify.com/v1/search?q=krzysztof%20krawczyk&type=track&market=PL&limit=50&offset=0";
		String filePath = "test_response" + FileSystems.getDefault().getSeparator() + "response.json";
		this.mockServer.expect(requestTo(spotifyUrl)).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(ResponseReaderFromFile.readResponseFromFile(filePath), MediaType.APPLICATION_JSON));

		SearchGui searchGui = getSearchGuiWithModifiedMethods();
		searchGui.searchTextField.setValue("Krzysztof Krawczyk");
		searchGui.searchButton.click();

		mockServer.verify();
		verify(spotifyAlbumClient, times(1)).getAlbumsByAuthor(any(), eq("krzysztof krawczyk"));

		Grid<SpotifyAlbumDto> grid = searchGui.albumDtoGrid;
		int receivedSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(50, receivedSize);
	}

	@Test
	void searchGuiTest_searchEmptyValue_emptyGrid() {

		SearchGui searchGui = getSearchGuiWithModifiedMethods();
		searchGui.searchTextField.setValue("");
		searchGui.searchButton.click();

		verify(spotifyAlbumClient, times(1)).getAlbumsByAuthor(any(), eq(""));

		Grid<SpotifyAlbumDto> grid = searchGui.albumDtoGrid;
		int receivedSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(0, receivedSize);
	}

	@Test
	void searchGuiTest_expiredSession_logoutExecuted() {

		doThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"))
				.when(spotifyAlbumClient).getAlbumsByAuthor(null, "krzysztof krawczyk");

		SearchGui searchGui = getSearchGuiWithModifiedMethods();
		searchGui.searchTextField.setValue("Krzysztof Krawczyk");
		searchGui.searchButton.click();

		verify(spotifyAlbumClient, times(1)).getAlbumsByAuthor(any(), eq("krzysztof krawczyk"));

		verify(sessionUtils, times(1)).expireCurrentSession();
	}

	@Test
	void searchGuiTest_HTTPErrorWhileSearching_notification() {

		doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"))
				.when(spotifyAlbumClient).getAlbumsByAuthor(null, "krzysztof krawczyk");

		String expectedNotification = "Error while searching: 500 Internal Server Error";
		SearchGui searchGui = getSearchGuiWithAssertionInNotification(expectedNotification);
		searchGui.searchTextField.setValue("Krzysztof Krawczyk");
		searchGui.searchButton.click();

		verify(spotifyAlbumClient, times(1)).getAlbumsByAuthor(any(), eq("krzysztof krawczyk"));

		verify(sessionUtils, never()).expireCurrentSession();
	}

	@Test
	void searchGuiTest_otherErrorWhileSearching_notification() {

		doThrow(new RuntimeException("Unexpected Error"))
				.when(spotifyAlbumClient).getAlbumsByAuthor(null, "krzysztof krawczyk");

		String expectedNotification = "Error while searching: Unexpected Error";
		SearchGui searchGui = getSearchGuiWithAssertionInNotification(expectedNotification);
		searchGui.searchTextField.setValue("Krzysztof Krawczyk");
		searchGui.searchButton.click();

		verify(spotifyAlbumClient, times(1)).getAlbumsByAuthor(any(), eq("krzysztof krawczyk"));

		verify(sessionUtils, never()).expireCurrentSession();
	}

	@Test
	void searchGuiTest_logout_success() {

		SearchGui searchGui = getSearchGuiWithModifiedMethods();
		searchGui.logoutButton.click();

		verify(sessionUtils, times(1)).expireCurrentSession();
	}

	private SearchGui getSearchGuiWithModifiedMethods() {
		return new SearchGui(spotifyAlbumClient, sessionUtils) {
			@Override
			void showNotification(String notificationText) {
			}
			@Override
			void navigateOrReload(boolean withRedirect) {
			}
		};
	}

	private SearchGui getSearchGuiWithAssertionInNotification(String expectedNotificationText) {
		return new SearchGui(spotifyAlbumClient, sessionUtils) {
			@Override
			void showNotification(String notificationText) {
				Assertions.assertEquals(expectedNotificationText, notificationText);
			}
			@Override
			void navigateOrReload(boolean withRedirect) {
			}
		};
	}
}