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
import java.util.Date;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession();
            ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
            User user = (User)session.getAttribute("user");
            Connection dbcon = dataSource.getConnection();

            JsonArray jsonArray = new JsonArray();

            System.out.println("pre setup is good");
            System.out.println(user.getCid());

            String sales_id = null;

            for (int i = 0; i < previousItems.size(); i++) {
                String[] vals = previousItems.get(i).split("_", 2);
                Statement statement = dbcon.createStatement();
                String query = "SELECT T.AUTO_INCREMENT FROM information_schema.TABLES T WHERE T.TABLE_SCHEMA = 'moviedb' AND T.TABLE_NAME = 'sales'";
                System.out.println(query);
                ResultSet rs = statement.executeQuery(query);
                if (rs.next()) {
                    sales_id = rs.getString("AUTO_INCREMENT");
                }

                String query1 = "SELECT m.title FROM movies as m WHERE m.id = ?";
                PreparedStatement statement1 = dbcon.prepareStatement(query1);
                statement1.setString(1,vals[0]);
                ResultSet rs1 = statement1.executeQuery();

                if (rs1.next()) {
                    String movies_title = rs1.getString("title");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movies_title", movies_title);
                    jsonObject.addProperty("quantity", vals[1]);
                    jsonObject.addProperty("sales_id", sales_id);
                    jsonArray.add(jsonObject);
                }

                Statement statement2 = dbcon.createStatement();
                String query2 = String.format("INSERT INTO sales (customerId, movieId, saleDate, quantity) VALUES ('%s', '%s', '%s', %s)",user.getCid(), vals[0], java.time.LocalDate.now(), vals[1]);
                int rs2 = statement2.executeUpdate(query2);

                statement2.close();

                rs.close();
                statement.close();

                rs1.close();
                statement1.close();

            }
            synchronized (previousItems) {
                previousItems.clear();
            }

            out.write(jsonArray.toString());
            response.setStatus(200);
            dbcon.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("error");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());
            response.setStatus(500);
        }

        response.getWriter().close();

    }
}
