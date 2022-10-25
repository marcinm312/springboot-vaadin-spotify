package pl.marcinm312.springbootspotify.testdataprovider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseReaderFromFile {

	public static String readResponseFromFile(String filePath) throws IOException {

		StringBuilder response = new StringBuilder();
		try (FileReader fileReader = new FileReader(filePath);
			 BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			String linia;
			while ((linia = bufferedReader.readLine()) != null) {
				response.append(linia);
			}
		}
		return response.toString();
	}
}
