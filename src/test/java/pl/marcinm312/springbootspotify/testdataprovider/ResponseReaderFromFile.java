package pl.marcinm312.springbootspotify.testdataprovider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ResponseReaderFromFile {

	private ResponseReaderFromFile() {

	}

	public static String readResponseFromFile(String filePath) throws IOException {

		StringBuilder response = new StringBuilder();
		FileReader fileReader = new FileReader(filePath);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String linia;
		while ((linia = bufferedReader.readLine()) != null) {
			response.append(linia);
		}
		bufferedReader.close();
		fileReader.close();
		return response.toString();
	}
}
