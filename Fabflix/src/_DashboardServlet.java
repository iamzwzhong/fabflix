import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "_DashboardServlet", urlPatterns = "/api/_dashboard")
public class _DashboardServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String meta = "SELECT table_name, column_name, column_type FROM information_schema.columns WHERE (table_schema='moviedb')";

            // Perform the query
            ResultSet rs1 = statement.executeQuery(meta);


            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs1.next()) {
                String table_name = rs1.getString("table_name");
                String column_name = rs1.getString("column_name");
                String column_type = rs1.getString("column_type");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("table_name", table_name);
                jsonObject.addProperty("column_name", column_name);
                jsonObject.addProperty("column_type", column_type);

                jsonArray.add(jsonObject);
            }

            rs1.close();


            String name = "";
            String birthYear = "";

            if(request.getParameter("name")!=null){
                name = request.getParameter("name");
            }

            if(request.getParameter("birthYear")!=null){
                birthYear = request.getParameter("birthYear");
            }

            if (!name.equals("")) {
                ResultSet rs = statement.executeQuery("select max(id) as max from stars");
                String m_id = "nm";
                if (rs.next()) {
                    m_id = rs.getString(1);
                }
                m_id = m_id.substring(2);
                int id = Integer.parseInt(m_id);
                id++;
                String id2 = Integer.toString(id);
                id2 = "nm" + id2;
                String insertStar = "INSERT INTO stars(id, name, birthYear) VALUES (?, ?, ?)";
                PreparedStatement insertStarStmt = dbcon.prepareStatement(insertStar);
                insertStarStmt.setString(1, id2);
                insertStarStmt.setString(2, name);
                if (!birthYear.equals("")) {
                    insertStarStmt.setString(3, birthYear);
                } else {
                    insertStarStmt.setNull(3, Types.INTEGER);
                }
                insertStarStmt.executeUpdate();
                JsonObject jsonObject = new JsonObject();
                String end = String.format("Star added %s", id2);
                jsonObject.addProperty("status", "success");
                jsonObject.addProperty("message", end);
                jsonArray.add(jsonObject);
                rs.close();
            }

            String m_name = "";
            String director = "";
            String s_name = "";
            String releaseYear = "";
            String genre = "";

            if(request.getParameter("m_name")!=null){
                m_name = request.getParameter("m_name");
            }

            if(request.getParameter("director")!=null){
                director = request.getParameter("director");
            }

            if(request.getParameter("s_name")!=null){
                s_name = request.getParameter("s_name");
            }

            if(request.getParameter("releaseYear")!=null){
                releaseYear = request.getParameter("releaseYear");
            }

            if(request.getParameter("genre")!=null){
                genre = request.getParameter("genre");
            }


            if (!m_name.equals("") && !director.equals("") && !s_name.equals("") && !releaseYear.equals("") && !genre.equals("")) {
                System.out.println("HI");
                String exists = "SELECT EXISTS(select * from movies where title = ? and director = ? and year = ?)";
                PreparedStatement existMovie = dbcon.prepareStatement(exists);
                existMovie.setString(1, m_name);
                existMovie.setString(2, director);
                existMovie.setString(3, releaseYear);
                ResultSet r = existMovie.executeQuery();
                if(r.next()) {
                    JsonObject jsonObject = new JsonObject();
                    if (r.getInt(1) == 1) {
                        jsonObject.addProperty("status", "failed");
                        jsonObject.addProperty("message", "Movie already exists.");
                        jsonArray.add(jsonObject);
                    } else {
                        ResultSet rs = statement.executeQuery("select max(id) as max from movies");
                        String m_id = "tt";
                        if (rs.next()) {
                            m_id = rs.getString(1);
                        }
                        m_id = m_id.substring(2);
                        int id = Integer.parseInt(m_id);
                        id++;
                        String id2 = Integer.toString(id);
                        id2 = "tt0" + id2;
                        String insertMovie = "CALL add_movie(?, ?, ?, ?, ?, ?)";
                        CallableStatement insertStarStmt = dbcon.prepareCall(insertMovie);
                        insertStarStmt.setString(1, id2);
                        insertStarStmt.setString(2, m_name);
                        insertStarStmt.setString(3, releaseYear);
                        insertStarStmt.setString(4, director);
                        insertStarStmt.setString(5, s_name);
                        insertStarStmt.setString(6, genre);
                        insertStarStmt.executeQuery();
                        String genre_id  = "select id as max from genres where name = ?";
                        PreparedStatement gen_id = dbcon.prepareStatement(genre_id);
                        gen_id.setString(1, genre);
                        ResultSet rs4 = gen_id.executeQuery();
                        String g_id = "";
                        if (rs4.next()) {
                            g_id = rs4.getString(1);
                        }
                        String star_id  = "select id as max from stars where name = ?";
                        PreparedStatement st_id = dbcon.prepareStatement(star_id);
                        st_id.setString(1, s_name);
                        ResultSet rs5 = st_id.executeQuery();
                        String s_id = "";
                        if (rs5.next()) {
                            s_id = rs5.getString(1);
                        }
                        String end1 = String.format("Movie added. Movie ID: %s Genre ID: %s Star ID: %s", id2, g_id, s_id);
                        jsonObject.addProperty("status", "success");
                        jsonObject.addProperty("message", end1);
                        jsonArray.add(jsonObject);
                        rs.close();
                        rs4.close();
                        rs5.close();
                    }
                }
                r.close();
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            statement.close();
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