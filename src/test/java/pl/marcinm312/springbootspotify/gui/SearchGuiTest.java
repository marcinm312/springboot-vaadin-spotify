package pl.marcinm312.springbootspotify.gui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
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
import pl.marcinm312.springbootspotify.utils.VaadinUtils;

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
@MockBeans({@MockBean(OAuth2AuthorizedClientService.class)})
class SearchGuiTest {

	@MockBean
	private SessionUtils sessionUtils;

	@SpyBean
	private SpotifyAlbumClient spotifyAlbumClient;

	private MockRestServiceServer mockServer;

	@Autowired
	private RestTemplate restTemplate;

	private static MockedStatic<VaadinUtils> mockedVaadinUtils;

	@BeforeEach
	void setup() {

		mockedVaadinUtils = Mockito.mockStatic(VaadinUtils.class);
		MockitoAnnotations.openMocks(this);
		this.mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	@AfterEach
	void tearDown() {
		mockedVaadinUtils.close();
	}

	@Test
	void searchGuiTest_simpleSearchCase_success() throws IOException {

		String spotifyUrl = "https://api.spotify.com/v1/search?q=krzysztof%20krawczyk&type=track&market=PL&limit=50&offset=0";
		String filePath = "test_response" + FileSystems.getDefault().getSeparator() + "response.json";
		this.mockServer.expect(requestTo(spotifyUrl)).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(ResponseReaderFromFile.readResponseFromFile(filePath), MediaType.APPLICATION_JSON));

		SearchGui searchGui = new SearchGui(spotifyAlbumClient, sessionUtils);
		searchGui.searchTextField.setValue("Krzysztof Krawczyk");
		searchGui.searchButton.click();

		mockServer.verify();
		verify(spotifyAlbumClient, times(1)).getAlbumsByAuthor(any(), eq("krzysztof krawczyk"));

		Grid<SpotifyAlbumDto> grid = searchGui.albumDtoGrid;
		int expectedGridSize = 50;
		int receivedGridSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(expectedGridSize, receivedGridSize);
	}

	@Test
	void searchGuiTest_searchEmptyValue_emptyGrid() {

		SearchGui searchGui = new SearchGui(spotifyAlbumClient, sessionUtils);
		searchGui.searchTextField.setValue("");
		searchGui.searchButton.click();

		verify(spotifyAlbumClient, times(1)).getAlbumsByAuthor(any(), eq(""));

		Grid<SpotifyAlbumDto> grid = searchGui.albumDtoGrid;
		int receivedSize = grid.getDataProvider().size(new Query<>());
		Assertions.assertEquals(0, receivedSize);
	}

	@Test
	void searchGuiTest_searchBlankValue_emptyGrid() {

		SearchGui searchGui = new SearchGui(spotifyAlbumClient, sessionUtils);
		searchGui.searchTextField.setValue("        ");
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

		SearchGui searchGui = new SearchGui(spotifyAlbumClient, sessionUtils);
		searchGui.searchTextField.setValue("Krzysztof Krawczyk");
		searchGui.searchButton.click();

		verify(spotifyAlbumClient, times(1)).getAlbumsByAuthor(any(), eq("krzysztof krawczyk"));
		verify(sessionUtils, times(1)).expireCurrentSession();
	}

	@Test
	void searchGuiTest_HTTPErrorWhileSearching_notification() {

		doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"))
				.when(spotifyAlbumClient).getAlbumsByAuthor(null, "krzysztof krawczyk");

		String expectedNotification = "Error while searching. HTTP status: Internal Server Error. Message: 500 Internal Server Error";
		SearchGui searchGui = new SearchGui(spotifyAlbumClient, sessionUtils);
		searchGui.searchTextField.setValue("Krzysztof Krawczyk");
		searchGui.searchButton.click();

		verify(spotifyAlbumClient, times(1)).getAlbumsByAuthor(any(), eq("krzysztof krawczyk"));
		verify(sessionUtils, never()).expireCurrentSession();
		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq(expectedNotification)), times(1));
	}

	@Test
	void searchGuiTest_otherErrorWhileSearching_notification() {

		doThrow(new RuntimeException("Unexpected Error"))
				.when(spotifyAlbumClient).getAlbumsByAuthor(null, "krzysztof krawczyk");

		String expectedNotification = "Error while searching: Unexpected Error";
		SearchGui searchGui = new SearchGui(spotifyAlbumClient, sessionUtils);
		searchGui.searchTextField.setValue("Krzysztof Krawczyk");
		searchGui.searchButton.click();

		verify(spotifyAlbumClient, times(1)).getAlbumsByAuthor(any(), eq("krzysztof krawczyk"));
		verify(sessionUtils, never()).expireCurrentSession();
		mockedVaadinUtils.verify(() -> VaadinUtils.showNotification(eq(expectedNotification)), times(1));
	}

	@Test
	void searchGuiTest_logout_success() {

		SearchGui searchGui = new SearchGui(spotifyAlbumClient, sessionUtils);
		searchGui.logoutButton.click();

		verify(sessionUtils, times(1)).expireCurrentSession();
	}
}
