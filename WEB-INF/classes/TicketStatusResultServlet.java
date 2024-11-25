// TicketStatusResultServlet.java
// package yourpackage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Scanner;

@WebServlet("/TicketStatusResultServlet")
public class TicketStatusResultServlet extends HttpServlet {

    private final String TICKET_FILE_PATH = System.getProperty("catalina.home") + "\\webapps\\Tutorial_1\\TicketDetails.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String ticketNumber = request.getParameter("ticketNumber");
        boolean ticketFound = false;

        out.println("<html><head>");
        out.println("<title>Ticket Status</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background-color: #333; color: white; margin: 0; padding: 0; display: flex; align-items: center; justify-content: center; min-height: 100vh; }");
        out.println(".container { text-align: left; background-color: #222; width: 90%; max-width: 700px; padding: 40px; border-radius: 10px; box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.7); }");
        out.println("h1 { color: #e63946; font-size: 2.4em; margin-bottom: 25px; text-align: center; }");
        out.println(".info { font-size: 1.1em; margin: 15px 0; color: #f1faee; }");
        out.println(".label { font-weight: bold; color: #ff9e6b; }");
        out.println(".section-title { font-size: 1.3em; color: #ff9e6b; margin-top: 20px; font-weight: bold; }");
        out.println(".section-content { margin-top: 10px; line-height: 1.5; }");
        out.println(".recommended-action { background-color: #48cae4; color: white; padding: 12px; border-radius: 8px; font-weight: bold; text-align: center; margin-top: 20px; font-size: 1.3em; }");
        out.println(".ticket-image-container { text-align: center; margin-top: 20px; }");
        out.println(".ticket-image { max-width: 250px; max-height: 250px; width: auto; height: auto; object-fit: cover; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.5); border: 2px solid #555; }");
        out.println(".back-link { margin-top: 30px; font-size: 1.1em; color: #a8dadc; text-align: center; }");
        out.println(".back-link a { color: #a8dadc; text-decoration: none; font-weight: bold; }");
        out.println(".back-link a:hover { color: #f1faee; text-decoration: underline; }");
        out.println("</style>");
        out.println("</head><body>");

        out.println("<div class='container'>");

        try (Scanner scanner = new Scanner(new File(TICKET_FILE_PATH))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] ticketData = line.split(";");

                if (ticketData[0].equals(ticketNumber)) {
                    String description = ticketData[1];
                    String absoluteImagePath = ticketData[2];
                    String decision = ticketData[3].replace("**", "").replace("\\n", "<br>");  // Remove "**" and replace "\n" with <br>

                    // Convert absolute path to relative path for image display
                    String relativeImagePath = absoluteImagePath.replace("C:\\apache-tomcat-9.0.94\\webapps\\Tutorial_1\\", "");

                    out.println("<h1>Ticket Status</h1>");
                    out.println("<div class='info'><span class='label'>Ticket Number:</span> " + ticketNumber + "</div>");
                    out.println("<div class='info'><span class='label'>Description:</span> " + description + "</div>");

                    // Display specific sections based on content
                    if (decision.contains("Assessment of the Condition:") || decision.contains("Recommended Action:")) {
                        String[] decisionParts = decision.split("###");

                        for (String part : decisionParts) {
                            if (part.contains("Assessment of the Condition")) {
                                out.println("<div class='section-title'>Assessment of the Condition:</div>");
                                String conditionContent = part.replace("Assessment of the Condition:", "").trim();
                                out.println("<div class='section-content'>" + conditionContent + "</div>");
                            } 
                            if (part.contains("Recommended Action")) {
                                out.println("<div class='section-title'>Recommended Action:</div>");
                                String actionContent = part.replace("Recommended Action:", "").trim();
                                out.println("<div class='section-content'>" + actionContent + "</div>");

                                // Highlight specific action based on keywords
                                if (actionContent.toLowerCase().contains("replace")) {
                                    out.println("<div class='recommended-action'>Action: Replace the Product</div>");
                                } else if (actionContent.toLowerCase().contains("refund")) {
                                    out.println("<div class='recommended-action'>Action: Refund the Product</div>");
                                } else if (actionContent.toLowerCase().contains("escalate")) {
                                    out.println("<div class='recommended-action'>Action: Escalate to Human Agent</div>");
                                }
                            }
                        }
                    } else {
                        // Display unstructured decision as a single block if no specific sections are detected
                        out.println("<div class='section-content'>" + decision + "</div>");
                    }

                    // Display uploaded image if available
                    out.println("<div class='ticket-image-container'><p><strong>Uploaded Image:</strong></p>");
                    out.println("<img src='" + relativeImagePath + "' alt='Ticket Image' class='ticket-image'></div>");

                    ticketFound = true;
                    break;
                }
            }

            if (!ticketFound) {
                out.println("<h1>Ticket Not Found</h1>");
                out.println("<p>No ticket found with the provided ticket number.</p>");
            }

        } catch (FileNotFoundException e) {
            out.println("<h1>Error</h1>");
            out.println("<p>Unable to access the ticket file.</p>");
            e.printStackTrace();
        }

        out.println("<div class='back-link'><a href='CheckTicketStatusServlet'>Back to Check Another Ticket</a></div>");
        out.println("</div>");
        out.println("</body></html>");
    }
}
