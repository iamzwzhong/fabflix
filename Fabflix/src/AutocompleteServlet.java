
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "AutocompleteServlet", urlPatterns = "/api/autocomplete")
public class AutocompleteServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private Map<String,Integer> stopWords = new HashMap<String,Integer>();

    public AutocompleteServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        buildStopWords();
        try {
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");
            System.out.println("Autocomplete with query: " + query);

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            String[] tokens = query.split(" ", 0);

            ArrayList<String> noSWTokens = new ArrayList<String>();
            for (int i = 0; i < tokens.length; i++) {
                if (stopWords.get(tokens[i]) == null) {
                    noSWTokens.add(tokens[i]);
                }
            }

            Connection dbcon = dataSource.getConnection();

            String basic = "SELECT m.id, m.title\n" +
                    "FROM movies m\n" +
                    "WHERE MATCH(m.title) AGAINST(";

            for (int i = 0; i < noSWTokens.size(); i++) {
                basic += " ? ";
            }

            basic += " IN BOOLEAN MODE) LIMIT 10";
            System.out.println(basic);

            PreparedStatement searchMovie = dbcon.prepareStatement(basic);

            for (int i =0; i < noSWTokens.size(); i++) {
                searchMovie.setString(i+1,"+" + noSWTokens.get(i) + "*");
            }
            ResultSet rs = searchMovie.executeQuery();

            while (rs.next()) {
                jsonArray.add(generateJsonObject(rs.getString(1),rs.getString(2)));
            }

            System.out.println(jsonArray.toString());

            response.getWriter().write(jsonArray.toString());
            rs.close();
            searchMovie.close();
            dbcon.close();
            return;

        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
            return;
        }
    }

    private static JsonObject generateJsonObject(String movieId, String heroName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", heroName);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
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
            return;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }


}