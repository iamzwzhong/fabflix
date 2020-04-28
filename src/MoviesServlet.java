import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String name = request.getParameter("name");
            String director = request.getParameter("director");
            String releaseYear = request.getParameter("releaseYear");
            String starName = request.getParameter("starName");

            String shown = request.getParameter("shown");
            String sorting = request.getParameter("sorting");



            String name1 = "AND m.title like '%" + name + "%' ";
            String director1 = "AND m.director like '%" + director + "%' ";
            String starName1 = "AND ms.allactors like '%" + starName + "%' ";
            String releaseYear1 = "AND m.year = " + releaseYear + " ";

            String basic = "SELECT m.id, m.year, m.title, m.director, ms.actors, mg.genres, ms.starId, r.rating\n" +
                    "FROM movies m\n" +
                    "INNER JOIN movie_stars ms ON ms.id = m.id, ratings r, movie_genres mg\n" +
                    "WHERE mg.id = m.id AND r.movieId = m.id ";


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

            String ending;

            if(sorting.equals("Title")){
                ending = "ORDER BY m.title, r.rating";
            }
            else {
                ending = "ORDER BY r.rating, m.title";
            }

            ending = ending + " LIMIT " + shown;

            basic = basic + ending;


            // Perform the query
            ResultSet rs = statement.executeQuery(basic);


            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movies_id = rs.getString("id");
                String movies_title = rs.getString("title");
                int movies_year = rs.getInt("year");
                String movies_director = rs.getString("director");
                float movies_ratings = rs.getFloat("rating");
                String movies_actors = rs.getString("actors");
                String movies_genres = rs.getString("genres");
                String movies_starIds = rs.getString("starId");

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
            statement.close();
            dbcon.close();
        } catch (Exception e) {

            System.out.println("HI");

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
