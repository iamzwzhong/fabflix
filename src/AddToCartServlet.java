import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.JsonObject;

@WebServlet(name = "AddToCartServlet", urlPatterns = "/api/addtocart")
public class AddToCartServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String mid = request.getParameter("id");
        String qtn = request.getParameter("quantity");

        String val = mid + '_' + qtn;

        try {
            HttpSession session = request.getSession();

            // get the previous items in a ArrayList
            ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
            if (previousItems == null) {
                previousItems = new ArrayList<>();

                previousItems.add(val);
                session.setAttribute("previousItems", previousItems);
            } else {
                // prevent corrupted states through sharing under multi-threads
                // will only be executed by one thread at a time
                synchronized (previousItems) {
                    previousItems.add(val);
                }
            }
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status","success");
            response.getWriter().write(responseJsonObject.toString());
        }
        catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("errorMessage",e.getMessage());
            response.getWriter().write(responseJsonObject.toString());
            response.setStatus(500);
            }
    }
}