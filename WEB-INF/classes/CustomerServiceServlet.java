// CustomerServiceServlet.java
// package yourpackage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/CustomerServiceServlet")
public class CustomerServiceServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head>");
        out.println("<title>Customer Service</title>");
        out.println("<style>");
        // Set basic styling
        out.println("body { font-family: Arial, sans-serif; background-color: #333; color: white; margin: 0; padding: 0; display: flex; align-items: center; justify-content: center; height: 100vh; }");
        // Centered container with red and black theme
        out.println(".container { text-align: center; background-color: #222; width: 90%; max-width: 600px; padding: 40px; border-radius: 8px; box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.5); }");
        // Header styling
        out.println("h1 { color: #e63946; font-size: 2em; margin-bottom: 20px; }");
        // Menu styling with red buttons
        out.println(".menu { display: flex; flex-direction: column; gap: 20px; }");
        out.println(".menu a { text-decoration: none; color: white; padding: 15px; background-color: #e63946; border-radius: 5px; font-weight: bold; font-size: 1.1em; transition: background-color 0.3s; }");
        out.println(".menu a:hover { background-color: #d62828; }");
        // Back link
        out.println(".back-link { display: block; margin-top: 30px; font-size: 1em; color: #a8dadc; }");
        out.println(".back-link a { color: #a8dadc; text-decoration: none; }");
        out.println(".back-link a:hover { color: #f1faee; text-decoration: underline; }");
        out.println("</style>");
        out.println("</head><body>");

        // Main container
        out.println("<div class='container'>");
        out.println("<h1>Customer Service</h1>");
        out.println("<p>Please select one of the following options:</p>");

        // Options menu
        out.println("<div class='menu'>");
        out.println("<a href='OpenTicketServlet'>Open a Ticket</a>");
        out.println("<a href='CheckTicketStatusServlet'>Check Ticket Status</a>");
        out.println("</div>");

        // Back to main menu link
        out.println("<div class='back-link'><a href='http://localhost:8080/Tutorial_1/'>Back to Home</a></div>");
        out.println("</div>");  // End of container div

        out.println("</body></html>");
    }
}
