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

import java.sql.DriverManager;
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/api/employeelogin")
public class EmployeeLoginServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        try {

            JsonObject responseJsonObject = new JsonObject();

            Connection dbCon = dataSource.getConnection();
            Statement statement = dbCon.createStatement();

            String email = request.getParameter("email");
            String query = String.format("SELECT * from employees where email like '%s'", email);
            ResultSet rs = statement.executeQuery(query);

            if (rs.next() == false) {
                responseJsonObject.addProperty("status","fail");
                responseJsonObject.addProperty("message", "Invalid Email");
                out.write(responseJsonObject.toString());
                return;
            }

            String password = request.getParameter("pswd");
            boolean verify = verifyCredentials(email, password);

            if (verify == false) {
                responseJsonObject.addProperty("status","fail");
                responseJsonObject.addProperty("message", "Incorrect Password");
            }
            else {
                request.getSession().setAttribute("user", new User(email, "employee"));
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

    private static boolean verifyCredentials(String email, String password) throws Exception {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();

        String query = String.format("SELECT * from employees where email='%s'", email);

        ResultSet rs = statement.executeQuery(query);

        boolean success = false;
        if (rs.next()) {
            // get the encrypted password from the database
            String encryptedPassword = rs.getString("password");

            // use the same encryptor to compare the user input password with encrypted password stored in DB
            success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
        }

        rs.close();
        statement.close();
        connection.close();

        System.out.println("verify " + email + " - " + password);

        return success;
    }
}