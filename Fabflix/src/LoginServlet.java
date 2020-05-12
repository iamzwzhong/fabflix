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

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status","fail");
            responseJsonObject.addProperty("message","Failed Recaptcha");
            out.write(responseJsonObject.toString());
            out.close();
            return;
        }

        try {

            JsonObject responseJsonObject = new JsonObject();

            Connection dbCon = dataSource.getConnection();

            String email = request.getParameter("email");
            String query = "SELECT * from customers where email like ?";
            PreparedStatement statement = dbCon.prepareStatement(query);
            statement.setString(1,email);
            ResultSet rs = statement.executeQuery();

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
                String id = rs.getString("id");
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

    private static boolean verifyCredentials(String email, String password) throws Exception {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        String query = "SELECT * from customers where email=?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1,email);

        ResultSet rs = statement.executeQuery();

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