// CheckTicketStatusServlet.java
// package yourpackage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/CheckTicketStatusServlet")
public class CheckTicketStatusServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head>");
        out.println("<title>Check Ticket Status</title>");
        out.println("<style>");
        // Basic layout and theme styling
        out.println("body { font-family: Arial, sans-serif; background-color: #333; color: white; margin: 0; padding: 0; display: flex; align-items: center; justify-content: center; height: 100vh; }");
        // Container to center the form and apply a dark background
        out.println(".container { text-align: center; background-color: #222; width: 90%; max-width: 600px; padding: 40px; border-radius: 8px; box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.5); }");
        // Header styling
        out.println("h1 { color: #e63946; font-size: 2em; margin-bottom: 20px; }");
        // Form styling
        out.println("form { display: flex; flex-direction: column; align-items: center; }");
        out.println("label { font-size: 1.1em; margin: 10px 0; color: #f1faee; }");
        out.println("input[type='text'] { width: 100%; max-width: 500px; padding: 10px; margin-bottom: 20px; border-radius: 5px; border: none; font-size: 1em; }");
        
        // Submit button styling
        out.println("input[type='submit'] { padding: 10px 20px; font-size: 1em; color: white; background-color: #e63946; border: none; border-radius: 5px; cursor: pointer; transition: background-color 0.3s; }");
        out.println("input[type='submit']:hover { background-color: #d62828; }");

        // Back link styling
        out.println(".back-link { display: block; margin-top: 20px; font-size: 1em; color: #a8dadc; }");
        out.println(".back-link a { color: #a8dadc; text-decoration: none; }");
        out.println(".back-link a:hover { color: #f1faee; text-decoration: underline; }");
        out.println("</style>");
        out.println("</head><body>");

        // Container for form
        out.println("<div class='container'>");
        out.println("<h1>Check Ticket Status</h1>");
        out.println("<form action='TicketStatusResultServlet' method='POST'>");
        out.println("<label for='ticketNumber'>Ticket Number:</label>");
        out.println("<input type='text' name='ticketNumber' required>");
        out.println("<input type='submit' value='Check Status'>");
        out.println("</form>");
        out.println("<div class='back-link'><a href='CustomerServiceServlet'>Back to Customer Service</a></div>");
        out.println("</div>");  // Close container div

        out.println("</body></html>");
    }
}
