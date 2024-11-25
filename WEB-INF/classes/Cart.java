import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Cart")
public class Cart extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();

        Utilities utility = new Utilities(request, pw);
        String name = request.getParameter("name");
        String operation = request.getParameter("operation");

        // Perform add or remove operation
        if (operation == null) {
            utility.storeProduct(name, request.getParameter("type"), request.getParameter("maker"), request.getParameter("access"));
        } else if (operation.equals("remove")) {
            utility.removeProduct(name); // remove item from the cart
        }

        // Display the updated cart after the operation
        displayCart(request, response);
    }

    protected void displayCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        Utilities utility = new Utilities(request, pw);
        Carousel carousel = new Carousel();

        if (!utility.isLoggedin()) {
            HttpSession session = request.getSession(true);
            session.setAttribute("login_msg", "Please Login to add items to cart");
            response.sendRedirect("Login");
            return;
        }

        utility.printHtml("Header.html");
        utility.printHtml("LeftNavigationBar.html");
        pw.print("<div id='content'><div class='post'><h2 class='title meta'>");
        pw.print("<a style='font-size: 24px;'>Cart(" + utility.CartCount() + ")</a>");
        pw.print("</h2><div class='entry'>");

        if (utility.CartCount() > 0) {
            pw.print("<table class='gridtable'>");
            int i = 1;
            double total = 0;
            for (OrderItem oi : utility.getCustomerOrders()) {
                pw.print("<tr>");
                pw.print("<td>" + i + ".</td><td>" + oi.getName() + "</td><td>$ " + oi.getPrice() + "</td>");

                // Form for removing an item
                pw.print("<td>");
                pw.print("<form name='Cart' action='Cart' method='post'>");
                pw.print("<input type='hidden' name='name' value='" + oi.getName() + "'>");
                pw.print("<input type='hidden' name='operation' value='remove'>");
                pw.print("<input type='submit' name='Remove' value='Remove' class='btnbuy' />");
                pw.print("</form>");
                pw.print("</td>");
                pw.print("</tr>");

                total += oi.getPrice();
                i++;
            }

            pw.print("<tr><th colspan='2'>Total</th><th>$ " + total + "</th></tr>");
            pw.print("</table>");

            // Checkout form - separate from the Remove buttons
            pw.print("<form action='CheckOut' method='post'>");
            pw.print("<input type='hidden' name='orderTotal' value='" + total + "'>");
            pw.print("<input type='submit' name='CheckOut' value='Check Out' class='btnbuy' style='width: 50%; margin-top: 20px;' />");
            pw.print("</form>");

            // Carousel feature
            pw.print(carousel.carouselfeature(utility));
        } else {
            pw.print("<h4 style='color:red'>Your Cart is empty</h4>");
        }

        pw.print("</div></div></div>");
        utility.printHtml("Footer.html");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        Utilities utility = new Utilities(request, pw);
        displayCart(request, response);
    }
}
