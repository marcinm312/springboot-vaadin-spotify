package pl.marcinm312.springbootspotify.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;

public class VaadinUtils {

	private VaadinUtils() {

	}

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
