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

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String ccfn = request.getParameter("ccfn");
        String ccln = request.getParameter("ccln");
        String ccard = request.getParameter("ccard");
        String expdate = request.getParameter("expdate");

        response.setContentType("application/json");

        try {
            HttpSession session = request.getSession();
            ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
            JsonObject responseJsonObject = new JsonObject();
            if (previousItems == null || previousItems.size() == 0) {
                responseJsonObject.addProperty("status","failed");
                responseJsonObject.addProperty("message","Shopping cart is empty");
                response.getWriter().write(responseJsonObject.toString());
                response.getWriter().close();
                return;
            }

            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
            String query = String.format("SELECT * FROM creditcards AS cc WHERE cc.id = '%s' and cc.firstName = '%s' and cc.lastName = '%s' and cc.expiration = '%s'",ccard,ccfn,ccln,expdate);
            ResultSet rs = statement.executeQuery(query);
            System.out.println("query success");

            if (rs.next()) {
                System.out.println("payment success");
                responseJsonObject.addProperty("status","success");
            }
            else {
                System.out.println("payment failed");
                responseJsonObject.addProperty("status","failed");
                responseJsonObject.addProperty("message","Invalid credentials");
            }

            System.out.println("before send");
            System.out.println(responseJsonObject.toString());
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            statement.close();
            dbcon.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());
            response.setStatus(500);
        }

        response.getWriter().close();

    }
}
