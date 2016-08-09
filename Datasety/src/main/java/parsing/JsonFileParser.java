package parsing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parsing.util.MockPostalCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonFileParser {

   private static final Logger logger = LogManager.getLogger(JsonFileParser.class.getName());

   // TODO Zająć się wyjątkami
   public static void read(File file) {
	  logger.info("Start read");

	  Gson gson = new GsonBuilder().create();
	  JsonStreamParser parser;
	  try {
		 parser = new JsonStreamParser(new FileReader(file));
		 while (parser.hasNext()) {
			System.out.println(gson.fromJson(parser.next(), MockPostalCode.class));
		 }
	  } catch (FileNotFoundException e) {
		 e.printStackTrace();
	  }

	  logger.info("Finish read");
   }
}
