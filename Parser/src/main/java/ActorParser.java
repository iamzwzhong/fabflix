import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private int starsId;
    private Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?useSSL=false", "mytestuser", "mypassword");
    private boolean valid = true;

    final int batchSize = 2500;
    int countStar = 0;

    String insertStar = "INSERT INTO stars(id, name, birthYear) VALUES (?, ?, ?)";
    PreparedStatement insertStarStmt = con.prepareStatement(insertStar);

    private Map<String, Integer> starMap = new HashMap<String, Integer>();

    public ActorParser() throws IOException, SQLException {

    }

    public void runParser() throws SQLException {
        con.setAutoCommit(false);
        setStarsId();
        buildStarsMap();
        parseDocument();
        pw.close();

        insertStarStmt.executeBatch();
        con.commit();
        insertStarStmt.close();

        con.setAutoCommit(true);
        con.close();
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
            valid = true;
            tempActor = new Actors();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equalsIgnoreCase("actor")) {
                if (valid == true) {
                    addStar();
                }
                //System.out.println(tempActor.toString());
            } else if (qName.equalsIgnoreCase("stagename")) {
                if (tempVal != null && !tempVal.equals("")) {
                    tempActor.setName(tempVal);
                }
                else {
                    throw new Exception("Empty Name");
                }
            } else if (qName.equalsIgnoreCase("dob")) {
                tempActor.setBirthYear(Integer.parseInt(tempVal.trim()));
            }
        }
        catch (Exception e) {
            if (e.getMessage().equals("Empty Name")) {
                valid = false;
                String s = String.format("%s, Error: %s", tempActor.toString(), e.getMessage());
                pw.println(s);
            }
            else {
                tempActor.setBirthYear(0);
            }
        }

    }

    private void addStar() {
        try {
            if (tempActor.getBirthYear() != 0) {
                String x = tempActor.getName().trim() + tempActor.getBirthYear();
                if (starMap.get(x) != null) {
                    String s = String.format("%s, Error: Star already exists in database", tempActor.toString());
                    pw.println(s);
                    return;
                }
            }

            insertStarStmt.setString(1,buildStarsId(starsId));
            insertStarStmt.setString(2, tempActor.getName());

            if (tempActor.getBirthYear() == 0) {
                insertStarStmt.setNull(3, Types.INTEGER);
            }
            else {
                insertStarStmt.setInt(3,tempActor.getBirthYear());
            }

            insertStarStmt.addBatch();
            if(++countStar % batchSize == 0) {
                insertStarStmt.executeBatch();
                con.commit();
            }

            starsId++;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void setStarsId() {
        try {
            PreparedStatement getTop = null;
            String getTopString = "SELECT max(id) AS max FROM stars";

            getTop = con.prepareStatement(getTopString);
            ResultSet rs = getTop.executeQuery();
            con.commit();


            while (rs.next())
                starsId = Integer.parseInt(rs.getString(1).substring(2)) + 1;

            rs.close();
            getTop.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String buildStarsId(int sid) {
        return "nm" + sid;
    }

    private void buildStarsMap() {
        try {
            String getStarsStr = "SELECT name, birthYear FROM stars";
            PreparedStatement getStars = con.prepareStatement(getStarsStr);
            ResultSet rs = getStars.executeQuery();
            con.commit();

            while (rs.next()) {
                String s = rs.getString(1) + rs.getInt(2);
                starMap.put(s, 1);
            }
            rs.close();
            getStars.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    /*
    public static void main(String[] args) {
        ActorParser p = new ActorParser();
        p.runParser();
    }
     */


}
