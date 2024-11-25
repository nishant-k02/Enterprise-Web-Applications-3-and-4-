import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.util.ArrayList;

@WebServlet("/RecommendProductServlet")
public class RecommendProductServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve search query from form submission
        String searchQuery = request.getParameter("productQuery");

        // Fetch products from the FastAPI backend
        ArrayList<String> products = getProductsFromFastAPI(searchQuery);

        // Prepare the response HTML content
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Product Recommendations</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background-color: #f9f9f9; color: #333; margin: 0; padding: 0; }");
        out.println("header { background-color: #e60000; color: white; padding: 20px; display: flex; align-items: center; font-size: 24px; }");
        out.println(".back-arrow { color: white; text-decoration: none; font-weight: bold; font-size: 24px; margin-right: 15px; }");
        out.println(".back-arrow:hover { color: #808080; }");
        out.println("h3 { color: #e60000; }");
        out.println(".container { width: 80%; margin: 20px auto; }");
        out.println(".product-card { background-color: #fff; border: 1px solid #ddd; border-radius: 8px; padding: 20px; margin: 10px 0; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); display: flex; flex-direction: column; justify-content: space-between; }");
        out.println(".product-card h4 { color: #333; font-size: 22px; margin-bottom: 10px; }");
        out.println(".product-card p { color: #666; font-size: 16px; margin-bottom: 10px; }");
        out.println(".product-card .price { font-size: 18px; color: #e60000; font-weight: bold; margin-bottom: 15px; }");
        out.println(".product-card .buttons { display: flex; justify-content: space-between; }");
        out.println(".product-card button { padding: 10px 20px; font-size: 16px; border: none; border-radius: 5px; cursor: pointer; }");
        out.println(".product-card .buy-btn { background-color: #e60000; color: white; }");
        out.println(".product-card .cart-btn { background-color: #008CBA; color: white; }");
        out.println(".no-results { font-size: 18px; color: #999; }");
        out.println("footer { text-align: center; padding: 10px; background-color: #222; color: white; position: fixed; width: 100%; bottom: 0; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        // Header with back arrow
        out.println("<header>");
        out.println("<a href='http://localhost:8080/Tutorial_1/Home' class='back-arrow'>&larr;</a>");
        out.println("Recommended Products based on Search");
        out.println("</header>");

        // Content container
        out.println("<div class='container'>");
        out.println("<h3>Product Recommendations for: <span style='color: black;'>" + searchQuery + "</span></h3>");

        if (products.isEmpty()) {
            out.println("<p class='no-results'>No products found for your query.</p>");
        } else {
            for (String product : products) {
                out.println("<div class='product-card'>");
                out.println("<h4>" + product.split("\\|")[0] + "</h4>");
                out.println("<p class='price'>$" + product.split("\\|")[1] + "</p>");
                out.println("<p>" + product.split("\\|")[2] + "</p>");
                out.println("<div class='buttons'>");
                out.println("<button class='buy-btn'>Buy Now</button>");
                out.println("<button class='cart-btn'>Add to Cart</button>");
                out.println("</div>");
                out.println("</div>");
            }
        }

        out.println("</div>");
        out.println("<footer>Prices and offers are subject to change. &copy; 2024 Smart Homes. All rights reserved. Developed by Nishant Khandhar</footer>");
        out.println("</body>");
        out.println("</html>");
    }

    private ArrayList<String> getProductsFromFastAPI(String query) {
        ArrayList<String> products = new ArrayList<>();
        try {
            // Define the FastAPI endpoint
            URL url = new URL("http://localhost:8000/recommend-product/");

            System.out.println("getProductsFromFastAPI, query: " + query);

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

            // Manually parse the response string to extract product details
            String responseString = response.toString();
            String recommendationsBlock = responseString.substring(responseString.indexOf("\"recommendations\":") + 18, responseString.lastIndexOf("]") + 1);
            String[] productBlocks = recommendationsBlock.split("\\},\\{");

            for (String productBlock : productBlocks) {
                int productNameIndex = productBlock.indexOf("\"product_name\":\"") + 16;
                if (productNameIndex > 0) {
                    String productName = productBlock.substring(productNameIndex, productBlock.indexOf("\"", productNameIndex));
                    String price = productBlock.substring(productBlock.indexOf("\"price\":") + 8, productBlock.indexOf(",", productBlock.indexOf("\"price\":")));
                    String description = productBlock.substring(productBlock.indexOf("\"description\":\"") + 15, productBlock.indexOf("\"", productBlock.indexOf("\"description\":\"") + 15));

                    products.add(productName + " | " + price + " | " + description);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}
