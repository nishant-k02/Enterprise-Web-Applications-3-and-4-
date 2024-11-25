import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;

@WebServlet("/CheckOut")

//once the user clicks buy now button page is redirected to checkout page where user has to give checkout information

public class CheckOut extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
	        Utilities Utility = new Utilities(request, pw);
		storeOrders(request, response);
	}
	
	protected void storeOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try
        {
        response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
        Utilities utility = new Utilities(request,pw);
		if(!utility.isLoggedin())
		{
			HttpSession session = request.getSession(true);				
			session.setAttribute("login_msg", "Please Login to add items to cart");
			response.sendRedirect("Login");
			return;
		}
        HttpSession session=request.getSession(); 

		//get the order product details	on clicking submit the form will be passed to submitorder page	
		
	    String userName = session.getAttribute("username").toString();
        String orderTotal = request.getParameter("orderTotal");
		utility.printHtml("Header.html");
		utility.printHtml("LeftNavigationBar.html");
		pw.print("<form name ='CheckOut' action='Payment' method='post'>");
        pw.print("<div id='content'><div class='post'><h2 class='title meta'>");
		pw.print("<a style='font-size: 24px;'>Order</a>");
		pw.print("</h2><div class='entry'>");
		pw.print("<table  class='gridtable'><tr><td>Customer Name:</td><td>");
		pw.print(userName);
		pw.print("</td></tr>");
		// for each order iterate and display the order name price
		for (OrderItem oi : utility.getCustomerOrders()) 
		{
			pw.print("<tr><td> Product Purchased:</td><td>");
			pw.print(oi.getName()+"</td></tr><tr><td>");
			pw.print("<input type='hidden' name='orderPrice' value='"+oi.getPrice()+"'>");
			pw.print("<input type='hidden' name='orderName' value='"+oi.getName()+"'>");
			pw.print("Product Price:</td><td>"+ oi.getPrice());
			pw.print("</td></tr>");
		}
		pw.print("<tr><td>");
        pw.print("Total Order Cost</td><td>"+orderTotal);
		pw.print("<input type='hidden' name='orderTotal' value='"+orderTotal+"'>");
		pw.print("</td></tr></table><table><tr></tr><tr></tr>");	
		pw.print("<form name='Cart' action='OrderServlet' method='post'>");

pw.print("<table>");
pw.print("<tr><th>Total Amount</th><td>$ " + orderTotal + "</td></tr>");
pw.print("<input type='hidden' name='orderTotal' value='" + orderTotal + "'>");
pw.print("</table><br>");

pw.print("<h3>Shipping Details</h3><br>");
pw.print("<table>");

pw.print("<tr>");
pw.print("<th>Shipping Mode</th>");
pw.print("<td><input type='radio' id='delivery' name='mode' value='delivery' required onclick='toggleShippingDetails(true)' style='margin-left: 1rem;'> <label for='delivery'>Delivery</label><br></td>");
pw.print("<td><input type='radio' id='pickup' name='mode' value='pickup' onclick='toggleShippingDetails(false)'> <label for='pickup'>Pickup</label><br></td>");
pw.print("</tr>");

pw.print("<tr>");
pw.print("<th>Street Address</th>");


pw.print("<td><input type='text' id='street' name='street' style='margin-left: 1rem; margin-bottom: 0.5rem;'></td></tr>");

pw.print("<tr>");
pw.print("<th>Apt/Suit</th>");
pw.print("<td><input type='text' id='apt' name='apt' style='margin-left: 1rem; margin-bottom: 0.5rem;'></td></tr>");

pw.print("<tr>");
pw.print("<th>City</th>");
pw.print("<td><input type='text' id='city' name='city' style='margin-left: 1rem; margin-bottom: 0.5rem;'></td></tr>");

pw.print("<tr>");
pw.print("<th>State</th>");
pw.print("<td><input type='text' id='state' name='state' style='margin-left: 1rem; margin-bottom: 0.5rem;'></td></tr>");

pw.print("<tr>");
pw.print("<th>Zip</th>");
pw.print("<td><input type='text' id='zip' name='zip' style='margin-left: 1rem; margin-bottom: 0.5rem;'></td></tr>");



pw.print("<tr>");
pw.print("<td colspan='2'>");
pw.print("<label>Select Pickup location (if pickup is selected): </label>");
pw.print("<input name='location' id='location' list='locations'>");
pw.print("<datalist id='locations'>");
pw.print("<option value='1555N State Street'/>");
pw.print("<option value='256N Michigan Avenue'/>");
pw.print("<option value='400E 25th Street'/>");
pw.print("<option value='Devon Ave'/>");
pw.print("<option value='Belmont Harbour'/>");
pw.print("<option value='525 S Adams'/>");
pw.print("<option value='256 W Chinatown'/>");
pw.print("<option value='501 E 32nd St'/>");
pw.print("<option value='18th St King Drive'/>");
pw.print("<option value='47th St Wabash'/>");
pw.print("</datalist>");
pw.print("</td>");
pw.print("</tr>");

pw.print("</table>");

pw.print("<h3>Payment Details</h3><br>");
pw.print("<table>");
pw.print("<tr>");
pw.print("<th>Credit/Debit Card</th>");
pw.print("<td><input type='text' name='creditCardNo' required style='margin-left: 1rem;'> </td></tr>");

pw.print("<tr><td colspan='2'>");
pw.print("<input type='submit' name='submit' class='btnbuy' value='Place Order' style='width: 100%; margin-top: 1rem;'>");
pw.print("</td></tr></table>");

pw.print("</form>");

// Include the JavaScript at the bottom of the page
pw.print("<script>");
pw.print("function toggleShippingDetails(enable) {");
pw.print("document.getElementById('street').required = enable;");
pw.print("document.getElementById('apt').required = enable;");
pw.print("document.getElementById('city').required = enable;");
pw.print("document.getElementById('state').required = enable;");
pw.print("document.getElementById('zip').required = enable;");

// For pickup, ensure the location field is required
pw.print("document.getElementById('location').required = !enable;");
pw.print("}");
pw.print("</script>");
	
		utility.printHtml("Footer.html");
	    }
        catch(Exception e)
		{
         System.out.println(e.getMessage());
		}  			
		}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	    {
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
	    }
}
