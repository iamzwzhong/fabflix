import com.google.gson.JsonObject;
import javax.annotation.Resource;
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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        try {

            JsonObject responseJsonObject = new JsonObject();

            Connection dbCon = dataSource.getConnection();
            Statement statement = dbCon.createStatement();

            String email = request.getParameter("email");
            String query = String.format("SELECT * from customers where email like '%s'", email);
            ResultSet rs = statement.executeQuery(query);

            if (rs.next() == false) {
                responseJsonObject.addProperty("status","fail");
                responseJsonObject.addProperty("message", "Invalid Email");
                out.write(responseJsonObject.toString());
                return;
            }

            String password = request.getParameter("pswd");
            String query1 = String.format("SELECT * from customers where email like '%s' and password like '%s'", email, password);
            ResultSet rs1 = statement.executeQuery(query1);

            if (rs1.next() == false) {
                responseJsonObject.addProperty("status","fail");
                responseJsonObject.addProperty("message", "Incorrect Password");
            }
            else {
                String id = rs1.getString("id");
                request.getSession().setAttribute("user", new User(email,id));
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            }

            out.write(responseJsonObject.toString());
        }
        catch (Exception e) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);

        }
    }
}