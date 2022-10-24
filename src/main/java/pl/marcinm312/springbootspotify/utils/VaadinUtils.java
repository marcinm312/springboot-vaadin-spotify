package pl.marcinm312.springbootspotify.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VaadinUtils {

	public static void showNotification(String notificationText) {
		Notification.show(notificationText, 5000, Notification.Position.MIDDLE);
	}

	public static void navigate(String location) {
		UI.getCurrent().navigate(location);
	}

	public static void reloadCurrentPage() {
		UI.getCurrent().getPage().reload();
	}
}
