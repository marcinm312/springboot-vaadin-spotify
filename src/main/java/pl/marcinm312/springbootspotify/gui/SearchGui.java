package pl.marcinm312.springbootspotify.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.client.HttpClientErrorException;
import pl.marcinm312.springbootspotify.service.SpotifyAlbumClient;
import pl.marcinm312.springbootspotify.model.dto.SpotifyAlbumDto;

import java.util.List;

@Route("")
public class SearchGui extends VerticalLayout {

	TextField searchTextField;
	Button searchButton;
	Grid<SpotifyAlbumDto> albumDtoGrid;

	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public SearchGui(SpotifyAlbumClient spotifyAlbumClient) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		OAuth2Authentication details = (OAuth2Authentication) authentication;

		searchTextField = new TextField();
		searchTextField.setLabel("Artist or track name:");

		albumDtoGrid = new Grid<>(SpotifyAlbumDto.class);
		albumDtoGrid.removeColumnByKey("imageUrl");
		albumDtoGrid.addColumn(new ComponentRenderer<>(albumDto -> {
			Image image = new Image(albumDto.getImageUrl(), albumDto.getImageUrl());
			image.setHeight("150px");
			return image;
		})).setHeader("Image");
		albumDtoGrid.setHeightByRows(true);

		searchButton = new Button("Search!");
		searchButton.addClickListener(event -> searchButtonClickEvent(spotifyAlbumClient, details));

		add(searchTextField, searchButton, albumDtoGrid);
	}

	private void searchButtonClickEvent(SpotifyAlbumClient spotifyAlbumClient, OAuth2Authentication details) {
		log.info("----------------------------------------");
		String searchValue = searchTextField.getValue().toLowerCase();
		log.info("searchValue=" + searchValue);
		try {
			List<SpotifyAlbumDto> albumList = spotifyAlbumClient.getAlbumsByAuthor(details, searchValue);
			log.info("albumList.size()=" + albumList.size());
			albumDtoGrid.setItems(albumList);
		} catch (HttpClientErrorException exc) {
			log.error("Error while searching: " + exc.getMessage());
			if ("Unauthorized".equals(exc.getStatusText())) {
				Notification.show("Session expired. Reload webpage", 5000, Notification.Position.MIDDLE);
			} else {
				Notification.show("Error while searching: " + exc.getMessage(), 5000, Notification.Position.MIDDLE);
			}
		} catch (Exception exc) {
			log.error("Error while searching: " + exc.getMessage());
			Notification.show("Error while searching: " + exc.getMessage(), 5000, Notification.Position.MIDDLE);
		}
	}
}
