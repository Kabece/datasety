package application.implementations.parser;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by pawel on 05.09.2016.
 */
public class XmlParser extends DefaultHandler{

    private File parsedFile;
    private Map<String,ArrayList<String>> dataset = new HashMap<>();


    public Map<String,ArrayList<String>> parseXml() {

        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(parsedFile);
            Element recordList = document.getRootElement();

            /* Niestety musimy dwa razy leciec po dokumencie - zeby nie zgubic atrybutow, ktore nie wystepuja w czesci rekordow */
            for (Element singleRow : recordList.getChildren()) {
                for(Element singleAttribute : singleRow.getChildren()) {
                    dataset.put(singleAttribute.getName(), new ArrayList<>());
                }
            }

            for(Element singleRow : recordList.getChildren()) {
                for (Element singleAttribute : singleRow.getChildren()) {
                    dataset.get(singleAttribute.getName()).add(singleAttribute.getValue());
                }

                int largestList = 0;
                Iterator iterator = dataset.entrySet().iterator();

                while(iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    if(((List<String>) pair.getValue()).size() < largestList) {
                        largestList = ((List<String>) pair.getValue()).size();
                    }
                }

                iterator = dataset.entrySet().iterator();

                while(iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    if(((List<String>) pair.getValue()).size() < largestList) {
                        ((List<String>) pair.getValue()).add("");
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    public XmlParser(File parsedFile) {
        this.parsedFile = parsedFile;
    }

    public File getParsedFile() {
        return parsedFile;
    }

    public void setParsedFile(File parsedFile) {
        this.parsedFile = parsedFile;
    }
}
