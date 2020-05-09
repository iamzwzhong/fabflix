import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class ActorParser extends DefaultHandler{

    private String tempVal;
    private Actors tempActor;
    private FileWriter fw = new FileWriter("report.txt", true);
    private PrintWriter pw = new PrintWriter(fw);

    public ActorParser() throws IOException {

    }

    public void runParser() {
        parseDocument();
        pw.close();
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse("actors.xml", this);
        }
        catch (SAXException se) { se.printStackTrace(); }
        catch (ParserConfigurationException pce) { pce.printStackTrace(); }
        catch (IOException ie) { ie.printStackTrace(); }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            tempActor = new Actors();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equalsIgnoreCase("actor")) {
                //System.out.println(tempActor.toString());
            } else if (qName.equalsIgnoreCase("stagename")) {
                if (tempVal != null && !tempVal.equals("")) {
                    tempActor.setName(tempVal);
                }
                else {
                    throw new Exception("Empty Name");
                }
            } else if (qName.equalsIgnoreCase("dob")) {
                tempActor.setBirthYear(Integer.parseInt(tempVal));
            }
        }
        catch (Exception e) {
            if (e.getMessage().equals("Empty Name")) {
                String s = String.format("%s, Error: %s", tempActor.toString(), e.getMessage());
                pw.println(s);
            }
        }

    }
    /*
    public static void main(String[] args) {
        ActorParser p = new ActorParser();
        p.runParser();
    }
     */


}
