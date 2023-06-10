package pl.marcinm312.springbootspotify.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.HttpClientErrorException;
import pl.marcinm312.springbootspotify.service.SpotifyAlbumClient;
import pl.marcinm312.springbootspotify.model.dto.SpotifyAlbumDto;
import pl.marcinm312.springbootspotify.utils.SessionUtils;
import pl.marcinm312.springbootspotify.utils.VaadinUtils;

import javax.validation.ValidationException;
import java.util.List;

@Slf4j
@Route("")
@PageTitle("Music search")
public class SearchGui extends VerticalLayout {

	Anchor logoutAnchor;
	TextField searchTextField;
	Button searchButton;
	Grid<SpotifyAlbumDto> albumDtoGrid;

	private static final String SEARCH_ERROR_FORMAT = "Error while searching: %s";

	private final transient SpotifyAlbumClient spotifyAlbumClient;
	private final transient SessionUtils sessionUtils;

	@Autowired
	public SearchGui(SpotifyAlbumClient spotifyAlbumClient, SessionUtils sessionUtils) {

		this.spotifyAlbumClient = spotifyAlbumClient;
		this.sessionUtils = sessionUtils;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;

		logoutAnchor = new Anchor("/logout", "Log out");

		searchTextField = new TextField();
		searchTextField.setLabel("Artist or track name:");

		prepareSearchResultsGrid();

		searchButton = new Button("Search!");
		searchButton.addClickListener(event -> searchButtonClickEvent(authenticationToken));

		add(logoutAnchor, searchTextField, searchButton, albumDtoGrid);
	}

	private void prepareSearchResultsGrid() {

		albumDtoGrid = new Grid<>(SpotifyAlbumDto.class);
		albumDtoGrid.removeColumnByKey("imageUrl");
		albumDtoGrid.addColumn(new ComponentRenderer<>(albumDto -> {
			Image image = new Image(albumDto.getImageUrl(), albumDto.getImageUrl());
			image.setHeight("150px");
			return image;
		})).setHeader("Image");
		albumDtoGrid.setAllRowsVisible(true);
	}

	private void searchButtonClickEvent(OAuth2AuthenticationToken authenticationToken) {

		log.info("----------------------------------------");
		String searchValue = searchTextField.getValue().trim();
		log.info("searchValue={}", searchValue);
		try {
			List<SpotifyAlbumDto> albumList = spotifyAlbumClient.getAlbumsByAuthor(authenticationToken, searchValue);
			log.info("albumList.size()={}", albumList.size());
			albumDtoGrid.setItems(albumList);
		} catch (HttpClientErrorException exc) {
			String errorMessage = String.format("Error while searching. HTTP status: %s. Message: %s",
					exc.getStatusText(), exc.getMessage());
			log.error(errorMessage, exc);
			if ("Unauthorized".equals(exc.getStatusText())) {
				logoutActionWithReload();
			} else {
				VaadinUtils.showNotification(errorMessage.replace("<EOL>", ""));
			}
		} catch (ValidationException exc) {
			String errorMessage = String.format(SEARCH_ERROR_FORMAT, exc.getMessage());
			log.error(errorMessage);
			VaadinUtils.showNotification(errorMessage);
		} catch (NullPointerException exc) {
			String errorMessage = String.format(SEARCH_ERROR_FORMAT, exc.getMessage());
			log.error(errorMessage, exc);
			logoutActionWithFullReload();
			VaadinUtils.showNotification(errorMessage);
		} catch (Exception exc) {
			String errorMessage = String.format(SEARCH_ERROR_FORMAT, exc.getMessage());
			log.error(errorMessage, exc);
			VaadinUtils.showNotification(errorMessage);
		}
	}

	private void logoutActionWithReload() {
		sessionUtils.expireCurrentSession();
		VaadinUtils.reloadCurrentPage();
	}

	private void logoutActionWithFullReload() {
		VaadinServletRequest request = (VaadinServletRequest) VaadinService.getCurrentRequest();
		try {
			request.logout();
		} catch (Exception e) {
			String errorMessage = String.format("Error while reloading: %s", e.getMessage());
			log.error(errorMessage, e);
		}
		sessionUtils.expireCurrentSession();
		VaadinUtils.reloadCurrentPage();
	}
}
