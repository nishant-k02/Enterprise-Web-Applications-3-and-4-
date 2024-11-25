import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/OpenTicketServlet")
public class OpenTicketServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head>");
        out.println("<title>Open a Ticket</title>");
        out.println("<style>");
        // Basic layout and theme styling
        out.println("body { font-family: Arial, sans-serif; background-color: #333; color: white; margin: 0; padding: 0; display: flex; align-items: center; justify-content: center; height: 100vh; }");
        out.println(".container { text-align: center; background-color: #222; width: 90%; max-width: 600px; padding: 40px; border-radius: 8px; box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.5); }");
        out.println("h1 { color: #e63946; font-size: 2em; margin-bottom: 20px; }");
        out.println("form { display: flex; flex-direction: column; align-items: center; }");
        out.println("label { font-size: 1.1em; margin: 10px 0; color: #f1faee; }");
        out.println("textarea { width: 100%; max-width: 500px; padding: 10px; margin-bottom: 20px; border-radius: 5px; border: none; font-size: 1em; height: 100px; resize: none; }");

        // Custom file input styling
        out.println(".file-upload-container { position: relative; width: 100%; max-width: 500px; margin-bottom: 20px; }");
        out.println(".custom-file-label { display: inline-block; padding: 12px 20px; background-color: #e63946; color: white; font-size: 1em; border-radius: 5px; cursor: pointer; text-align: center; transition: background-color 0.3s; width: 100%; }");
        out.println(".custom-file-label:hover { background-color: #d62828; }");
        out.println("input[type='file'] { position: absolute; left: 0; top: 0; width: 100%; height: 100%; opacity: 0; cursor: pointer; }");
        
        // Image preview styling for square shape
        out.println("img { width: 200px; height: 200px; margin: 20px 0; border-radius: 5px; display: none; object-fit: cover; }"); // Square shape

        // Submit button styling
        out.println("input[type='submit'] { padding: 10px 20px; font-size: 1em; color: white; background-color: #e63946; border: none; border-radius: 5px; cursor: pointer; transition: background-color 0.3s; margin-top: 20px; }");
        out.println("input[type='submit']:hover { background-color: #d62828; }");

        // Back link styling
        out.println(".back-link { display: block; margin-top: 20px; font-size: 1em; color: #a8dadc; }");
        out.println(".back-link a { color: #a8dadc; text-decoration: none; }");
        out.println(".back-link a:hover { color: #f1faee; text-decoration: underline; }");

        out.println("</style>");
        out.println("<script>");
        out.println("function previewImage(event) {");
        out.println("  const file = event.target.files[0];");
        out.println("  if (file) {");
        out.println("    const reader = new FileReader();");
        out.println("    reader.onload = function(e) {");
        out.println("      const imgPreview = document.getElementById('imagePreview');");
        out.println("      imgPreview.src = e.target.result;");
        out.println("      imgPreview.style.display = 'block';"); // Show image
        out.println("    };");
        out.println("    reader.readAsDataURL(file);");
        out.println("  }");
        out.println("}");
        out.println("</script>");
        out.println("</head><body>");

        // Container for form
        out.println("<div class='container'>");
        out.println("<h1>Open a Ticket</h1>");

        // Ticket form
        out.println("<form action='submitTicket' method='POST' enctype='multipart/form-data'>");
        out.println("<label for='description'>Description:</label>");
        out.println("<textarea name='description' placeholder='Enter shipment description' required></textarea>");

        // Custom styled file upload
        out.println("<div class='file-upload-container'>");
        out.println("<label class='custom-file-label'>Choose Image to Upload");
        out.println("<input type='file' name='image' accept='image/*' required onchange='previewImage(event)'>");
        out.println("</label>");
        out.println("</div>");

        // Image preview element
        out.println("<img id='imagePreview' src='' alt='Image Preview'>"); // Image preview element
        
        // Submit button
        out.println("<input type='submit' value='Submit Ticket'>");
        out.println("</form>");

        // Back to Customer Service link
        out.println("<div class='back-link'><a href='CustomerServiceServlet'>Back to Customer Service</a></div>");
        out.println("</div>");  // Close container div

        out.println("</body></html>");
    }
}
