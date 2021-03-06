package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DepartmentsDAO;
import dao.GroupsDAO;
import dao.UserDAO;
import beans.UserBean;

/**
 * Servlet implementation class UsersServlet
 */
@WebServlet("/users")
public class UsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UserBean user = (session != null) ? (UserBean) session.getAttribute("user") : null;
		
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login");
		} else if (!user.getAccess("register")) {
			request.setAttribute("status", "warning");
			request.setAttribute("message", "You don't have access to this page.");
			request.getRequestDispatcher("error-access.jsp").forward(request, response);
		} else {
			// Select all users here.
			ArrayList<UserBean> users = UserDAO.findAll();
			HashMap<Integer, String> departmentsMap = DepartmentsDAO.getDepartmentsMap();
			HashMap<Integer, String> groupsMap = GroupsDAO.getGroupsMap();
			
			request.setAttribute("usersList", users);
			request.setAttribute("departmentsMap", departmentsMap);
			request.setAttribute("groupsMap", groupsMap);
			request.getRequestDispatcher("users.jsp").forward(request, response);
		}
	}
}
