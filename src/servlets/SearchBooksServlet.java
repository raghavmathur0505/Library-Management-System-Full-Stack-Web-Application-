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
import beans.ProductBean;
import beans.ProductsBean;

import java.util.ArrayList;

/**
 * Servlet implementation class ViewPostsServlet
 */
@WebServlet("/SearchBooksServlet")
public class SearchBooksServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchBooksServlet() {
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
		// ServletRequest session2 = null;
		// @SuppressWarnings("null")
		// UserBean userBean=(UserBean)session2.getAttribute("USER");
		// user=userBean.getUserName();
		
		//String searchTitle = request.getParameter("title");
		//String searchIsbn = request.getParameter("isbn");
		//String searchAuthor = request.getParameter("name");
		String inputSearch =request.getParameter("input");
		
		
		String[] searchArray = inputSearch.trim().split("\\s+");
		for(int i=0;i< searchArray.length;i++)
		{
			searchArray[i]= "%" + searchArray[i] + "%";
		}
		
		//searchTitle = "%" + searchTitle + "%";
		//searchIsbn = "%" + searchIsbn + "%";
		//searchAuthor = "%" + searchAuthor + "%";

		ProductsBean products = new ProductsBean();
		SearchBean search = new SearchBean();
		//System.out.println("searchtext:" + searchTitle);
		//search.setSearch(searchTitle);
		
		//search.setSearch(inputSearch);

		//search.setSearch2(searchIsbn);
		//search.setSearch3(searchAuthor);
		
		search.setSearchArray(searchArray);
		
		Boolean status = false;
		try {

			Client client = Client.create();
			WebResource webResource = client
					.resource("http://localhost:9443/LibraryManagement/rest/searchbooks/search");

			Gson userJson = new Gson();
			String data = userJson.toJson(search);

			// ClientResponse restResponse = webResource
			// .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
			// .post(ClientResponse.class, formData);
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
			System.out.println("status is good!");
			HttpSession session = request.getSession();
			session.setAttribute("PRODUCTS", products);
			RequestDispatcher rd = request.getRequestDispatcher("SearchBooks.jsp");
			rd.forward(request, response);
		} else {
			RequestDispatcher rd = request.getRequestDispatcher("MainPage.jsp");
			rd.forward(request, response);
		}

		// now send request to service

	}

}
