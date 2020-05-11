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
import java.sql.Statement;

@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String name = request.getParameter("name");
            String director = request.getParameter("director");
            String releaseYear = request.getParameter("releaseYear");
            String starName = request.getParameter("starName");

            String shown = request.getParameter("shown");
            String sorting = request.getParameter("sorting");

            String browse = request.getParameter("browse");

            String name1 = "AND m.title like ? ";
            String director1 = "AND m.director like ? ";
            String starName1 = "AND ms.allactors like ? ";
            String releaseYear1 = "AND m.year = ? ";
            String browse1 = "AND m.title like ? ";
            String browse2 = "AND m.title REGEXP '^[^a-zA-Z0-9]' ";

            String basic = "SELECT m.id, m.year, m.title, m.director, ms.actors, mg.genres, ms.starId\n" +
                    "FROM movies m\n" +
                    "INNER JOIN movie_stars ms ON ms.id = m.id, movie_genres mg\n" +
                    "WHERE mg.id = m.id ";

            if(!name.equals("")) {
                basic = basic + name1;
            }

            if(!director.equals("")) {
                basic = basic + director1;
            }

            if(!starName.equals("")) {
                basic = basic + starName1;
            }

            if(!releaseYear.equals("")) {
                basic = basic + releaseYear1;
            }

            if(!browse.equals("none")){
                if(!browse.equals("*")){
                    basic = basic + browse1;
                }
                else {
                    basic = basic + browse2;
                }
            }

            String ending;

            if(sorting.equals("Title")){
                ending = "ORDER BY m.title, (select r.rating from ratings r where r.movieId = m.id)";
            }
            else {
                ending = "ORDER BY (select r.rating from ratings r where r.movieId = m.id) DESC, m.title";
            }

            ending = ending + " LIMIT " + shown;

            basic = basic + ending;

            PreparedStatement searchMovie = dbcon.prepareStatement(basic);
            int i = 1;

            if(!name.equals("")) {
                searchMovie.setString(i,"%" + name + "%");
            }

            if(!director.equals("")) {
                searchMovie.setString(i,"%" + director + "%");
            }

            if(!starName.equals("")) {
                searchMovie.setString(i,"%" + starName + "%");
            }

            if(!releaseYear.equals("")) {
                searchMovie.setInt(i,Integer.parseInt(releaseYear));
            }

            if(!browse.equals("none")) {
                if (!browse.equals("*")) {
                    searchMovie.setString(i,browse + "%");
                }
            }

            System.out.println(searchMovie.toString());
            // Perform the query
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

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();

    }
}
