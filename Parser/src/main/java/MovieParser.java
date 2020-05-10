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
       put("ScFi", "Sci-Fi"); put("Cart", "Cartoon");put("Faml", "Family");put("Surl", "Surreal");
       put("AvGa", "AvantGarde"); put("Hist", "History"); put("Disa", "Disaster"); put("Epic", "Epic"); put("Fant", "Fantasy");
       put("Noir", "Noir"); put("Surr", "Surreal"); put("SciF", "Sci-Fi"); put("Porn", "Porn"); put("CnRb", "Crime");
    }};
    private boolean valid = true;
    private int movieId;
    private Map<String, Integer> genreMap = new HashMap<String, Integer>();
    private Map<String, Integer> movieMap = new HashMap<String, Integer>();
    private Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?useSSL=false", "mytestuser", "mypassword");

    final int batchSize = 2500;
    int countMovie = 0;
    int countGenre = 0;

    String insertMovie = "INSERT INTO movies(id, title, year, director) VALUES (?, ?, ?, ?)";
    PreparedStatement insertMovieStmt = con.prepareStatement(insertMovie);

    String insertGinMString = "INSERT INTO genres_in_movies(genreId, movieId) VALUES (?, ?)";
    PreparedStatement insertGinM = con.prepareStatement(insertGinMString);


    public MovieParser() throws IOException, SQLException {
    }

    public void runParser() throws SQLException {
        con.setAutoCommit(false);
        setMovieId();
        buildGenresMap();
        buildMoviesMap();
        parseDocument();
        pw.close();

        insertMovieStmt.executeBatch();
        con.commit();
        insertMovieStmt.close();

        insertGinM.executeBatch();
        con.commit();
        insertGinM.close();

        con.setAutoCommit(true);
        con.close();
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
            } else if (valid == false) {
                return;
            } else if (qName.equalsIgnoreCase("film")) {
                if (tempMovie.getDirector().contains("UnYear")) {
                        throw new Exception("Not a Director Name");
                }
                else if (valid == true) {
                    addMovie();
                    checkGenres(tempMovie);
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
                String g = catMap.get(tempVal.trim());
                if (g != null) {
                    tempGenres.add(g);
                }
                else {
                    tempGenres.add(tempVal);
                    String s = String.format("Genre does not match documented codes: %s", tempVal);
                    throw new Exception(s);
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
            String x = tempMovie.getTitle().trim() + tempMovie.getYear() + tempMovie.getDirector().trim();
            if(movieMap.get(x) != null) {
                String s = String.format("%s, Error: Movie already exists in database", tempMovie.toString());
                pw.println(s);
            }
            else {
                insertMovieStmt.setString(1,buildMovieId(movieId));
                insertMovieStmt.setString(2, tempMovie.getTitle());
                insertMovieStmt.setInt(3,tempMovie.getYear());
                insertMovieStmt.setString(4,tempMovie.getDirector());

                insertMovieStmt.addBatch();
                if(++countMovie % batchSize == 0) {
                    insertMovieStmt.executeBatch();
                    con.commit();
                }

                tempMovie.setMovieId(buildMovieId(movieId));
                movieId++;
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void setMovieId() {
        try {
            PreparedStatement getTop = null;
            String getTopString = "SELECT max(id) AS max FROM movies";

            getTop = con.prepareStatement(getTopString);
            ResultSet rs = getTop.executeQuery();
            con.commit();


            while (rs.next())
                movieId = Integer.parseInt(rs.getString(1).substring(2)) + 1;

            rs.close();
            getTop.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String buildMovieId(int mid) {
            int length = String.valueOf(mid).length();
            if (length == 6) {
                return "tt0" + mid;
            }
            else {
                return "tt" + mid;
            }
    }

    private void buildGenresMap() {
        try {
            PreparedStatement getGenres = null;
            String getGenresString = "SELECT * FROM genres";

            getGenres = con.prepareStatement(getGenresString);
            ResultSet rs = getGenres.executeQuery();
            con.commit();

            while (rs.next())
                genreMap.put(rs.getString(2),rs.getInt(1));
            rs.close();
            getGenres.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void buildMoviesMap() {
        try {
            String getMoviesStr = "SELECT title,year,director FROM movies";
            PreparedStatement getMovies = con.prepareStatement(getMoviesStr);
            ResultSet rs = getMovies.executeQuery();
            con.commit();

            while (rs.next()) {
                String s = rs.getString(1) + rs.getInt(2) + rs.getString(3);
                movieMap.put(s, 1);
            }
            rs.close();
            getMovies.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkGenres(Movies m) {
        try {
            for (int i = 0; i < m.getGenres().size(); i++) {
                String g = m.getGenres().get(i);
                if (genreMap.get(g) == null) {
                    PreparedStatement insertGenreStmt = null;
                    String insertGenreString = "INSERT INTO genres (id, name) VALUES (? ,?)";
                    int gid = genreMap.size() + 1;
                    insertGenreStmt = con.prepareStatement(insertGenreString);
                    insertGenreStmt.setInt(1, gid);
                    insertGenreStmt.setString(2,g);
                    insertGenreStmt.executeUpdate();
                    con.commit();
                    insertGenreStmt.close();
                    genreMap.put(g, gid);
                }
                int currGenreId = genreMap.get(g);
                String mid = m.getMovieId();

                insertGinM.setInt(1,currGenreId);
                insertGinM.setString(2, mid);
                insertGinM.addBatch();

                if (++countGenre % batchSize == 0) {
                    insertGinM.executeBatch();
                    con.commit();
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    /*
    public static void main(String[] args) throws IOException {
        MovieParser p = new MovieParser();
        p.runParser();
    }
     */


}
