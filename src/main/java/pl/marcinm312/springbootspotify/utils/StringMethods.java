package pl.marcinm312.springbootspotify.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringMethods {

	public static boolean isValidUrl(String url) {

		if (url.contains("#")) {
			return false;
		}
		try {
			new URI(url);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
