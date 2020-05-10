import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

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
    private Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?useSSL=false", "mytestuser", "mypassword");

    final int batchSize = 5000;
    int countSIM = 0;

    String insertSIMString = "INSERT INTO stars_in_movies(starId, movieId) VALUES (?, ?)";
    PreparedStatement insertSIMStmt = con.prepareStatement(insertSIMString);

    private Map<String, String> movieMap = new HashMap<String, String>();
    private Map<String, String> starsMap = new HashMap<String, String>();
    private Map<String, Integer> SIMMap = new HashMap<String, Integer>();

    public CastParser() throws IOException, SQLException {
    }

    public void runParser() throws SQLException {
        con.setAutoCommit(false);
        buildMoviesMap();
        buildStarsMap();
        buildSIMMap();
        parseDocument();

        insertSIMStmt.executeBatch();
        con.commit();
        insertSIMStmt.close();

        con.setAutoCommit(true);
        con.close();
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
                if (tempSIM.getStarName().equals("s a")) {
                    throw new Exception("No Actor Name");
                }
                else if (tempSIM.getDirector().contains("Unknown") || tempSIM.getDirector().contains("UnYear")) {
                    throw new Exception("No Director Name");
                }
                else {
                    addStarsInMovies();
                }
                //System.out.println(tempSIM.toString());
            } else if (qName.equalsIgnoreCase("t")) {
                tempSIM.setTitle(tempVal);
            } else if (qName.equalsIgnoreCase("a")) {
                tempSIM.setStarName(tempVal);
            }
        }
        catch (Exception e) {
            if (e.getMessage().equals("No Actor Name")) {
                String s = String.format("%s, Error: %s", tempSIM.toString(), e.getMessage());
                pw.println(s);
            }
            else if (e.getMessage().equals("No Director Name")) {
                String s = String.format("%s, Error: %s", tempSIM.toString(), e.getMessage());
                pw.println(s);
            }
        }

    }

    private void addStarsInMovies() {
        try {
            String SIMstr = tempSIM.getStarName().trim() + tempSIM.getTitle().trim() + tempSIM.getDirector().trim();
            if(SIMMap.get(SIMstr) != null) {
                String s = String.format("%s, Error: Star-Movie relationship already exists in database", tempSIM.toString());
                pw.println(s);
                return;
            }

            String starId;
            String movieId;

            String name = tempSIM.getStarName().trim();
            if(starsMap.get(name) == null) {
                String s = String.format("%s, Error: Star does not exist in database", tempSIM.toString());
                pw.println(s);
                return;
            }
            starId = starsMap.get(name);

            String x = tempSIM.getTitle().trim() + tempSIM.getDirector().trim();
            if(movieMap.get(x) == null) {
                String s = String.format("%s, Error: Movie does not exist in database", tempSIM.toString());
                pw.println(s);
                return;
            }
            movieId = movieMap.get(x);

            insertSIMStmt.setString(1, starId);
            insertSIMStmt.setString(2, movieId);
            insertSIMStmt.addBatch();

            if(++countSIM % batchSize == 0) {
                insertSIMStmt.executeBatch();
                con.commit();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void buildMoviesMap() {
        try {
            String getMoviesStr = "SELECT title,director,id FROM movies";
            PreparedStatement getMovies = con.prepareStatement(getMoviesStr);
            ResultSet rs = getMovies.executeQuery();
            con.commit();

            while (rs.next()) {
                String s = rs.getString(1) + rs.getString(2);
                movieMap.put(s, rs.getString(3));
            }
            rs.close();
            getMovies.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void buildStarsMap() {
        try {
            String getStarsStr = "SELECT name, id FROM stars";
            PreparedStatement getStars = con.prepareStatement(getStarsStr);
            ResultSet rs = getStars.executeQuery();
            con.commit();

            while (rs.next()) {
                starsMap.put(rs.getString(1), rs.getString(2));
            }
            rs.close();
            getStars.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void buildSIMMap() {
        try {
            String getSIMStr = "SELECT s.name, m.title, m.director FROM stars_in_movies sm, stars s, movies m WHERE s.id = sm.starId AND m.id = sm.movieId";
            PreparedStatement getSIM = con.prepareStatement(getSIMStr);
            ResultSet rs = getSIM.executeQuery();
            con.commit();

            while (rs.next()) {
                String s = rs.getString(1) + rs.getString(2) + rs.getString(3);
                SIMMap.put(s, 1);
            }
            rs.close();
            getSIM.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//    public static void main(String[] args) {
//        CastParser p = new CastParser();
//        p.runParser();
//    }

}
