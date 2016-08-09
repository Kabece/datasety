package parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;

import parsing.util.MockPostalCode;
public class JsonFileParser {

        public static void read(File file) {

                Gson gson = new GsonBuilder().create();

                JsonStreamParser parser = null;
                try {
                        parser = new JsonStreamParser(new FileReader(file));

                        while(parser.hasNext())
                        {
                                System.out.println(gson.fromJson(parser.next(), MockPostalCode.class));
                        }

                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }

        }

}
