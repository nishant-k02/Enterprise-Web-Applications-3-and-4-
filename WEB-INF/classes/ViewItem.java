import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ViewItem")
public class ViewItem extends HttpServlet
{
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter pw = response.getWriter();

    /* From the HttpServletRequest variable name,type,maker and acessories information are obtained.*/
    Utilities utility = new Utilities(request, pw);
    String name = request.getParameter("name");
    String type = request.getParameter("type");
    String maker = request.getParameter("maker");
    String access = request.getParameter("access");

    displayItem(request, response);
  }

  /* displayItem Function shows the products that users has bought, these products will be displayed with Total Amount.*/
  protected void displayItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {    //pw.print(carousel.carouselfeature(utility));

    response.setContentType("text/html");
    PrintWriter pw = response.getWriter();
    Utilities utility = new Utilities(request, pw);
    Carousel carousel = new Carousel();
    Console console = new Console();

    String name = request.getParameter("name");
    String type = request.getParameter("type");
    String maker = request.getParameter("maker");
    // String image = "";
    String description = "The Xiaomi Smart Doorbell offers 1080p HD video with night vision, two-way audio, and real-time alerts.";
    String retailer = "";
    String key = "";

    double price = 200.00, discount = 5;


    switch (type)
    {
      case "consoles":
        for (Map.Entry<String, Console> entry : SaxParserDataStore.consoles.entrySet()) {
          if (entry.getValue().getRetailer().equals(maker) && entry.getValue().getName().equals(name))
          {
            console = entry.getValue();
            price = console.getPrice();
            discount = console.getDiscount();
            // image = console.getImage();
            description = console.getDescription();
            retailer = console.getRetailer();
            key = entry.getKey();
            break;
          }
        }
        break;
    }

    utility.printHtml("Header.html");
    utility.printHtml("LeftNavigationBar.html");

    pw.print("<div id='content'><div class='post'>");
    pw.print("<h2 class='title meta'><a style='font-size: 24px; text-align: center'>" + name + "</a></h2>");
    pw.print("<div class='entry'><table id='bestseller'>");

    pw.print("<td><div id='shop_item'>");

    pw.print("<h3>$ " + getNewPrice(price, discount) + "</h3>");
    pw.print("<strong>Discount: " + discount + " %</strong>");
    pw.print("<h3>Old Price: $ " + price + "</h3><ul>");
    pw.print("<li id='item'><img src='images/consoles/xiaomi2.jpeg' alt='' /></li>");

    pw.print("<li><p style = 'text-align: center;'>" + description + "</p></li>");

    pw.print(
      "<li><form method='post' action='Cart'>" +
      "<input type='hidden' name='name' value='" + key +"'>" +
      "<input type='hidden' name='type' value='" + type +"'>" +
      "<input type='hidden' name='maker' value='" + retailer +"'>" +
      "<input type='hidden' name='access' value=''>" +
      "<input type='submit' class='btnbuy' value='Add to Cart'></form></li>");

    pw.print(
      "<li><form method='post' action='WriteReview'>" +
      "<input type='hidden' name='name' value='" + name +"'>" +
      "<input type='hidden' name='type' value='laptops'>" +
      "<input type='hidden' name='maker' value='" + retailer +"'>" +
      "<input type='hidden' name='access' value=''>" +
	  "<input type='submit' value='WriteReview' class='btnreview'></form></li>");

    pw.print(
      "<li><form method='post' action='ViewReview'>" +
      "<input type='hidden' name='name' value='" + name +"'>" +
      "<input type='hidden' name='type' value='laptops'>" +
      "<input type='hidden' name='maker' value='" + retailer +"'>" +
      "<input type='hidden' name='access' value=''>" +
	  "<input type='submit' value='ViewReview' class='btnreview'></form></li>");

    pw.print("</ul></div></td>");
    pw.print(carousel.carouselfeature(utility));
    pw.print("</table></div></div></div>");
        pw.print(carousel.carouselfeature( utility));

    utility.printHtml("Footer.html");
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter pw = response.getWriter();
    Utilities utility = new Utilities(request, pw);
    displayItem(request, response);
  }

  protected double getNewPrice(double original, double discount) {
    return  (original * (100 - discount)) / 100;
  }
}
