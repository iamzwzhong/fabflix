import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@WebServlet(name = "MainSearchServlet", urlPatterns = "/api/mainsearch")
public class MainSearchServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private Map<String,Integer> stopWords = new HashMap<String,Integer>();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        buildStopWords();

        String[] tokens = request.getParameter("main").split(" ", 0);

        ArrayList<String> noSWTokens = new ArrayList<String>();
        for (int i = 0; i < tokens.length; i++) {
            if (stopWords.get(tokens[i]) == null) {
                noSWTokens.add(tokens[i]);
            }
        }

        try {
            Connection dbcon = dataSource.getConnection();

            String basic = "SELECT m.id, m.year, m.title, m.director, ms.actors, mg.genres, ms.starId\n" +
                    "FROM movies m\n" +
                    "INNER JOIN movie_stars ms ON ms.id = m.id, movie_genres mg\n" +
                    "WHERE mg.id = m.id " +
                    "AND MATCH(m.title) AGAINST(";

            for (int i = 0; i < noSWTokens.size(); i++) {
                basic += " ? ";
            }

            basic += " IN BOOLEAN MODE)";
            System.out.println(basic);

            PreparedStatement searchMovie = dbcon.prepareStatement(basic);

            for (int i =0; i < noSWTokens.size(); i++) {
                searchMovie.setString(i+1,"+" + noSWTokens.get(i) + "*");
            }
            ResultSet rs = searchMovie.executeQuery();


            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movies_id = rs.getString("id");
                String movies_title = rs.getString("title");
                int movies_year = rs.getInt("year");
                String movies_director = rs.getString("director");
                String movies_actors = rs.getString("actors");
                String movies_genres = rs.getString("genres");
                String movies_starIds = rs.getString("starId");

                String getRating = "SELECT * from ratings where movieId = ?";
                PreparedStatement getRatingStmt = dbcon.prepareStatement(getRating);
                getRatingStmt.setString(1,movies_id);
                ResultSet rs1 = getRatingStmt.executeQuery();

                String movies_ratings = "N/A";

                if (rs1.next()) {
                    movies_ratings = rs1.getString("rating");
                }

                rs1.close();


                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movies_id", movies_id);
                jsonObject.addProperty("movies_title", movies_title);
                jsonObject.addProperty("movies_year", movies_year);
                jsonObject.addProperty("movies_director",movies_director);
                jsonObject.addProperty("movies_ratings",movies_ratings);
                jsonObject.addProperty("movies_actors",movies_actors);
                jsonObject.addProperty("movies_genres",movies_genres);
                jsonObject.addProperty("movies_starIds",movies_starIds);

                jsonArray.add(jsonObject);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            searchMovie.close();
            dbcon.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();

    }

    private void buildStopWords() {
        try {
            Connection dbcon = dataSource.getConnection();
            String getStopWordsString = "SELECT * FROM INFORMATION_SCHEMA.INNODB_FT_DEFAULT_STOPWORD";
            PreparedStatement getStopWords = dbcon.prepareStatement(getStopWordsString);
            ResultSet rs = getStopWords.executeQuery();

            while (rs.next()) {
                stopWords.put(rs.getString(1),1);
            }

            rs.close();
            getStopWords.close();
            dbcon.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
