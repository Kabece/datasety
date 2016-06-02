package parsing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonStreamParser;
import parsing.util.MockPostalCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by pawel on 02.06.2016.
 */
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
