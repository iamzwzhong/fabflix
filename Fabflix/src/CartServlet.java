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
import java.util.ArrayList;

@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String n = request.getParameter("new");
        String old = request.getParameter("old");

        System.out.println("accessing cart");

        if (n != null) {
            System.out.println("changing cart");
            HttpSession session = request.getSession();
            ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
            synchronized (previousItems) {
                previousItems.remove(old);
                previousItems.add(n);
            }
        }

        else if (old != null) {
            HttpSession session = request.getSession();
            ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
            synchronized (previousItems) {
                previousItems.remove(old);
            }
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession();
            ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");


            Connection dbcon = dataSource.getConnection();

            JsonArray jsonArray = new JsonArray();

            for (int i = 0; i < previousItems.size(); i++) {
                String[] vals = previousItems.get(i).split("_", 2);
                String query = "SELECT m.title FROM movies as m WHERE m.id = ?";
                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1,vals[0]);
                ResultSet rs = statement.executeQuery();


                if (rs.next()) {
                    String movies_title = rs.getString("title");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movies_title", movies_title);
                    jsonObject.addProperty("quantity", vals[1]);
                    jsonObject.addProperty("movie_id",vals[0]);
                    jsonArray.add(jsonObject);
                }

                rs.close();
                statement.close();

            }
            out.write(jsonArray.toString());
            response.setStatus(200);
            dbcon.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }

        out.close();

    }
}
