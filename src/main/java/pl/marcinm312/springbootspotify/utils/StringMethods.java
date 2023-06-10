package pl.marcinm312.springbootspotify.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringMethods {

	public static boolean isValidUrl(String url) {

		if (url.contains("#")) {
			return false;
		}
		try {
			new URL(url).toURI();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
