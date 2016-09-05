package application.implementations.parser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by pawel on 05.09.2016.
 */
public class XmlParser extends DefaultHandler{

    private File parsedFile;
    private Hashtable tags;
    private Map<String,ArrayList<String>> dataset;


    public Map<String,ArrayList<String>> parseXml() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(parsedFile, this);
        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfigurationException: ");
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            System.out.println("SAXException: ");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }
    /**
     * Event: Parser starts reading an element
     */
    @Override
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes atts)
            throws SAXException {

        String key = localName;
        Object value = dataset.get(key);

        if (value == null) {
            dataset.put(key, null);
        }
        else {
            int count = ((Integer)value).intValue();
            count++;
            dataset.put(key, null);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        boolean isFirstName=true, isLastName=false, isLocation=false;

        String a = "";
        if (isFirstName) {
            a = new String(ch, start, length);
        }
        if (isLastName) {
            a = new String(ch, start, length);
        }
        if (isLocation) {
            a = new String(ch, start, length);
        }

        System.out.println(a);
    }

    @Override
    public void startDocument() throws SAXException {
        dataset = new HashMap<String,ArrayList<String>>();
    }

    @Override
    public void endDocument() throws SAXException {
        Set e = dataset.keySet();

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
