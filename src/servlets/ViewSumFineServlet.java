package servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import beans.SearchBean;
import beans.UserBean;
import beans.ProductBean;
import beans.ProductsBean;
import java.util.ArrayList;

/**
 * Servlet implementation class ViewPostsServlet
 */
@WebServlet("/ViewSumFineServlet")
public class ViewSumFineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()54
	 */
	public ViewSumFineServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPOST(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	// String user="";
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/json");
		// ServletRequest session = null;
		// @SuppressWarnings("null")
		// HttpSession session = request.getSession();
		// UserBean userBean=(UserBean)session.getAttribute("USER");
		// String user=userBean.getName();
		// String unpaid ="";String paid="";
		SearchBean search = new SearchBean();
		String fines = null;
		if (request.getParameter("typeOfFines") != null) {
			fines = request.getParameter("typeOfFines");
			if (fines.equalsIgnoreCase("paid"))
				search.setPaidFine(true);
			else if (fines.equalsIgnoreCase("unpaid"))
				search.setUnpaidFine(true);
		}
		// if( request.getParameter("paid")!= null)
		// String paid = request.getParameter("paid");
		// String name = request.getParameter("name");
		// String cardId = userBean.getCardId();
		// System.out.println("session in servlet: "+user+" "+cardId);

		// cardId = "%" + cardId + "%";
		// isbn = "%" + isbn + "%";
		// name = "%" + name + "%";

		ProductsBean products = new ProductsBean();

		// System.out.println("searchtext:" + searchTitle);
		// search.setSearch(cardId);
		// search.setSearch2(isbn);
		// search.setSearch3(name);

		Boolean status = false;
		try {

			Client client = Client.create();
			WebResource webResource = client
					.resource("http://localhost:9443/LibraryManagement/rest/viewsumfineservice/view");

			Gson userJson = new Gson();
			String data = userJson.toJson(search);

			ClientResponse restResponse = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, data);

			if (restResponse.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + restResponse.getStatus());
			}

			Gson gson = new Gson();
			ProductsBean searchResult = gson.fromJson(restResponse.getEntity(String.class), ProductsBean.class);

			System.out.println("servlet printing now: ");
			// searchResult.getBooks();

			products = searchResult;

			// String statusString = restResponse.getEntity(String.class);
			// status = Boolean.parseBoolean(statusString);
			status = products.isValidSearch();
			System.out.println("servlet status: " + status);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (status) {
			if (request.getParameter("typeOfFines") != null && fines.equalsIgnoreCase("paid")) {
				// UserBean ub=new UserBean();
				// ub.setCardId(cardId);
				System.out.println("status is good!");
				HttpSession session1 = request.getSession();
				session1.setAttribute("PRODUCTS1", products);
				// session1.setAttribute("USER", userBean);
				RequestDispatcher rd = request.getRequestDispatcher("ViewAllPaidFines.jsp");
				rd.forward(request, response);
			}
			else
				System.out.println("status is good!");
			HttpSession session1 = request.getSession();
			session1.setAttribute("PRODUCTS1", products);
			// session1.setAttribute("USER", userBean);
			RequestDispatcher rd = request.getRequestDispatcher("ViewAllSumFines.jsp");
			rd.forward(request, response);
		} else {
			RequestDispatcher rd = request.getRequestDispatcher("MainPage.jsp");
			rd.forward(request, response);
		}

		// now send request to service

	}

}
