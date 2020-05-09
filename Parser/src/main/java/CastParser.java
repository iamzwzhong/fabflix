import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class CastParser extends DefaultHandler{

    private String tempVal;
    private StarsInMovies tempSIM;
    private String tempDirector;
    private FileWriter fw = new FileWriter("report.txt", true);
    private PrintWriter pw = new PrintWriter(fw);

    public CastParser() throws IOException {
    }

    public void runParser() {
        parseDocument();
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse("casts.xml", this);
        }
        catch (SAXException se) { se.printStackTrace(); }
        catch (ParserConfigurationException pce) { pce.printStackTrace(); }
        catch (IOException ie) { ie.printStackTrace(); }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            tempSIM = new StarsInMovies();
            tempSIM.setDirector(tempDirector);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equalsIgnoreCase("is")) {
                tempDirector = tempVal;
            } else if (qName.equalsIgnoreCase("m")) {
                if (!tempSIM.getStarName().equals("s a") && !tempSIM.getDirector().contains("Unknown") && !tempSIM.getDirector().contains("UnYear")) {
                    //System.out.println(tempSIM.toString());
                }
            } else if (qName.equalsIgnoreCase("t")) {
                tempSIM .setTitle(tempVal);
            } else if (qName.equalsIgnoreCase("a")) {
                tempSIM.setStarName(tempVal);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

//    public static void main(String[] args) {
//        CastParser p = new CastParser();
//        p.runParser();
//    }

}
