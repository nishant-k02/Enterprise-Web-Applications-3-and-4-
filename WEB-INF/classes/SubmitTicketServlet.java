// package yourpackage;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@WebServlet("/submitTicket")
@MultipartConfig
public class SubmitTicketServlet extends HttpServlet {

    private static int ticketCounter = 0;
    String TOMCAT_HOME = System.getProperty("catalina.home");
    private final String TICKET_FILE_PATH = TOMCAT_HOME + "\\webapps\\Tutorial_1\\TicketDetails.txt";
    private final String OPENAI_API_KEY = "your_api_key"; // Replace with your actual API key
    private final String OPENAI_MODEL = "gpt-4o-mini";

    @Override
    public void init() throws ServletException {
        super.init();
        File ticketFile = new File(TICKET_FILE_PATH);
        if (ticketFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(ticketFile))) {
                String lastLine;
                while ((lastLine = br.readLine()) != null) {
                    String[] ticketData = lastLine.split(";");
                    ticketCounter = Integer.parseInt(ticketData[0]);
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String description = request.getParameter("description");
        Part filePart = request.getPart("image");
        String ticketNumber = String.valueOf(++ticketCounter);
        String uploadDir = getServletContext().getRealPath("/") + "uploads";
        File uploadDirFile = new File(uploadDir);

        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String filePath = uploadDir + File.separator + ticketNumber + ".jpg";

        // Save the uploaded image
        try (FileOutputStream fos = new FileOutputStream(filePath);
             InputStream inputStream = filePart.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        // Encode image to Base64
        String base64Image = encodeImageToBase64(new File(filePath));

        // Get OpenAI response
        String decision = getOpenAIDecision(description, base64Image);

        // Save the ticket details
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TICKET_FILE_PATH, true))) {
            bw.write(ticketNumber + ";" + description + ";" + filePath + ";" + decision);
            bw.newLine();
        }

        // Respond to the user
       response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head>");
        out.println("<title>Ticket Submitted</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background-color: #333; color: white; margin: 0; padding: 0; display: flex; align-items: center; justify-content: center; height: 100vh; }");
        out.println(".container { text-align: center; background-color: #222; width: 90%; max-width: 600px; padding: 40px; border-radius: 8px; box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.5); }");
        out.println("h1 { color: #e63946; font-size: 2em; margin-bottom: 20px; }");
        out.println("p { font-size: 1.2em; margin: 10px 0; color: #f1faee; }");
        out.println(".back-link { margin-top: 20px; font-size: 1em; color: #a8dadc; }");
        out.println(".back-link a { color: #a8dadc; text-decoration: none; }");
        out.println(".back-link a:hover { color: #f1faee; text-decoration: underline; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h1>Ticket Submitted Successfully!</h1>");
        out.println("<p>Your ticket number is: <strong>" + ticketNumber + "</strong></p>");

    //    String formattedDecision = decision.replace("\n", "<br>");
    //     out.println("<p>Decision: <strong>" + formattedDecision + "</strong></p>"); // Display the decision

        out.println("<div class='back-link'><a href='CustomerServiceServlet'>Back to Customer Service</a></div>");
        out.println("</div>");
    }

    private String encodeImageToBase64(File imageFile) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            byte[] bytes = new byte[(int) imageFile.length()];
            fileInputStream.read(bytes);
            return Base64.getEncoder().encodeToString(bytes);
        }
    }

    private String getOpenAIDecision(String description, String base64Image) {
        String decision = "";
        try {
           String jsonInputString = "{"
                + "\"model\": \"" + OPENAI_MODEL + "\","
                + "\"messages\": ["
                + "{\"role\": \"system\", \"content\": \"You are a customer service assistant tasked with analyzing parcel conditions based on description and image.\"},"
                + "{\"role\": \"user\", \"content\": ["
                + "{\"type\": \"text\", \"text\": \"The user provided the following description: " + description + ". Please analyze the image and description to determine the condition of the parcel. Based on this, give a short assessment of the condition and decide if it should be refunded, replaced, or escalated to a human agent.\"},"
                + "{\"type\": \"image_url\", \"image_url\": {\"url\": \"data:image/jpeg;base64," + base64Image + "\"}}"
                + "]}"
                + "],"
                + "\"max_tokens\": 100"
                + "}";

            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
            conn.setDoOutput(true);

            // Send JSON request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    decision = parseOpenAIResponse(response.toString());
                }
            } else {
                throw new IOException("Unexpected HTTP response: " + responseCode);
            }
        } catch (Exception e) {
            decision = "Error: " + e.getMessage();
            e.printStackTrace();
        }
        return decision;
    }

    private String parseOpenAIResponse(String response) {
        try {
            int startIndex = response.indexOf("content") + 11;
            int endIndex = response.indexOf("\"", startIndex);
            return response.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unable to parse OpenAI response.";
    }
}
