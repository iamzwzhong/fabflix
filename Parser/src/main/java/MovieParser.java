import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

import javax.annotation.Resource;
import javax.sql.DataSource;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class MovieParser extends DefaultHandler{

    private String tempVal;
    private Movies tempMovie;
    private String tempDirector;
    private ArrayList<String> tempGenres;
    private FileWriter fw = new FileWriter("report.txt", true);
    private PrintWriter pw = new PrintWriter(fw);
    private Map<String, String> catMap = new HashMap<String, String>() {{
       put("Susp", "Thriller"); put("CnR", "Crime"); put("Dram", "Drama"); put("West", "Western");
       put("Myst", "Mystery"); put("S.F.", "Sci-Fi"); put("Advt","Adventure"); put("Horr", "Horror");
       put("Romt", "Romance"); put("Comd", "Comedy"); put("Musc", "Musical"); put("Docu", "Documentary");
       put("BioP", "Biography"); put("Ctxx", "Uncategorized"); put("Actn", "Violence"); put("Camp", "Camp");
       put("ScFi", "Sci-Fi"); put("Cart", "Cartoon");put("Faml", "Family");put("Surl", "Sureal");
        put("AvGa", "AvantGarde"); put("Hist", "History");
    }};
    private boolean valid = true;

    public MovieParser() throws IOException {
    }

    public void runParser() {
        parseDocument();
        pw.close();
        addMovie();
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse("movies.xml", this);
        }
        catch (SAXException se) { se.printStackTrace(); }
        catch (ParserConfigurationException pce) { pce.printStackTrace(); }
        catch (IOException ie) { ie.printStackTrace(); }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            tempMovie = new Movies();
            valid = true;
            tempMovie.setDirector(tempDirector);
        }
        else if (qName.equalsIgnoreCase("cats")) {
            tempGenres = new ArrayList<String>();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equalsIgnoreCase("dirname")) {
                if (tempVal != null && !tempVal.equals("")) {
                    tempDirector = tempVal;
                }
                else {
                    throw new Exception("Empty Director");
                }
            } else if (qName.equalsIgnoreCase("film")) {
                if (valid == true) {
                    //addMovie(tempMovie);
                }
                //System.out.println(tempMovie.toString());
            } else if (qName.equalsIgnoreCase("t")) {
                if (tempVal != null && !tempVal.equals("")) {
                    tempMovie.setTitle(tempVal);
                }
                else {
                    throw new Exception ("Empty Title");
                }
            } else if (qName.equalsIgnoreCase("year")) {
                tempMovie.setYear(Integer.parseInt(tempVal));
            } else if (qName.equalsIgnoreCase("cat")) {
                String g = catMap.get(tempVal);
                if (g != null) {
                    tempGenres.add(g);
                }
                else {
                    tempGenres.add(tempVal);
                }
            } else if (qName.equalsIgnoreCase("cats")) {
                if (tempGenres.size() == 0) {
                    tempGenres.add("Uncategorized");
                }
                tempMovie.setGenres(tempGenres);
            }
        }
        catch (Exception e) {
            valid = false;
            String s = String.format("%s, Error: %s", tempMovie.toString(), e.getMessage());
            pw.println(s);
        }

    }

    private void addMovie() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "mypassword");
            PreparedStatement addMovie = null;
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from genres");
            while(rs.next())
                System.out.println(rs.getInt(1)+"  "+rs.getString(2));
            con.close();
        }
        catch (Exception e) {

        }
    }
    /*
    public static void main(String[] args) throws IOException {
        MovieParser p = new MovieParser();
        p.runParser();
    }
     */


}
