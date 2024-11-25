import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.util.ArrayList;

@WebServlet("/SearchReviewsServlet")
public class SearchReviewsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve search query from form submission
        String searchQuery = request.getParameter("searchQuery");

        // Fetch reviews from the FastAPI backend
        ArrayList<String> reviews = getReviewsFromFastAPI(searchQuery);

        // Prepare the response HTML content
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Review Search</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background-color: #fff; color: #333; margin: 0; padding: 0; }");
        out.println("header { background-color: #e60000; color: white; padding: 20px; display: flex; align-items: center; font-size: 24px; }");
        out.println(".back-arrow { color: white; text-decoration: none; font-weight: bold; font-size: 24px; margin-right: 15px; }");
        out.println(".back-arrow:hover { color: #808080; }");
        out.println("h3 { color: #e60000; }");
        out.println("ul { list-style-type: none; padding: 0; }");
        out.println("li { background-color: #f0f0f0; margin: 10px 0; padding: 10px; border-radius: 5px; }");
        out.println(".container { width: 80%; margin: 20px auto; }");
        out.println(".no-results { font-size: 18px; color: #999; }");
        out.println("footer { text-align: center; padding: 10px; background-color: #222; color: white; position: fixed; width: 100%; bottom: 0; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        // Header with back arrow
        out.println("<header>");
        out.println("<a href='http://localhost:8080/Tutorial_1/Home' class='back-arrow'>&larr;</a>");
        out.println("Reviews Based on Search");
        out.println("</header>");

        // Content container
        out.println("<div class='container'>");
        out.println("<h3>Search Results for: <span style='color: black;'>" + searchQuery + "</span></h3>");

        if (reviews.isEmpty()) {
            out.println("<p class='no-results'>No reviews found for your query.</p>");
        } else {
            out.println("<ul>");
            int count = 1;
            for (String review : reviews) {
                out.println("<li><strong>Review " + count + ":</strong> " + review + "</li>");
                count++;
            }
            out.println("</ul>");
        }

        out.println("</div>");
        out.println("<footer>Prices and offers are subject to change. &copy; 2024 Smart Homes. All rights reserved. Developed by Nishant Khandhar</footer>");
        out.println("</body>");
        out.println("</html>");
    }

    private ArrayList<String> getReviewsFromFastAPI(String query) {
        ArrayList<String> reviews = new ArrayList<>();
        try {
            // Define the FastAPI endpoint
            URL url = new URL("http://localhost:8000/search-reviews/");

            // Log the query for debugging
            System.out.println("getReviewsFromFastAPI, query: " + query);

            // Create an HTTP connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setDoOutput(true);

            // Write the plain string request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = query.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Log the raw response
            System.out.println("Raw response: " + response);

            // Manually parse the response string to extract reviews
            String responseString = response.toString();
            String matchesBlock = responseString.substring(responseString.indexOf("\"matches\":") + 10, responseString.lastIndexOf("]") + 1);
            String[] reviewBlocks = matchesBlock.split("\\},\\{");

            for (String reviewBlock : reviewBlocks) {
                int reviewIndex = reviewBlock.indexOf("\"review\":\"") + 10;
                if (reviewIndex > 0) {
                    String review = reviewBlock.substring(reviewIndex, reviewBlock.indexOf("\"", reviewIndex));
                    reviews.add(review);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
