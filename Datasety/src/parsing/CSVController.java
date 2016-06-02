package parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.opencsv.CSVReader;

public class CSVController {

	public static void read(File file) {
		try {
		CSVReader reader = new CSVReader(new FileReader(file));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			for(int i = 0; i < nextLine.length; i++) {
				System.out.print(nextLine[i] + " | ");
			}
			System.out.println();
		}

		reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: Plik nie znaleziony!");
			e.getLocalizedMessage();
		} catch (IOException e) {
			System.out.println("Error: IOException");
			e.getLocalizedMessage();
		}
	}
}
