package main;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.SubjectsDAO;
import beans.SubjectsBean;
import beans.UserBean;

/**
 * Servlet implementation class SubjectServlet
 */
@WebServlet("/subjects")
public class SubjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubjectServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UserBean user = (session != null) ? (UserBean) session.getAttribute("user") : null;
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login");
		}
		else {
			String status = (String) session.getAttribute("status");
			String message = (String) session.getAttribute("message");
			if (status != null && message != null) {
				request.setAttribute("status", status);
				request.setAttribute("message", message);
				session.setAttribute("status", null);
				session.setAttribute("message", null);
			}
			
			String id = request.getParameter("id");
			boolean edit = request.getParameter("edit") == null ? false : Boolean.valueOf(request.getParameter("edit"));
			
			if (edit) {
				SubjectsBean subjectsBean = SubjectsDAO.find(Integer.valueOf(id));
				if (subjectsBean == null) {
					session.setAttribute("status", "Warning");
					session.setAttribute("message", "Such subject does not exist");
					response.sendRedirect(request.getContextPath() + "/subjects");
				}
				else {
					request.setAttribute("subjectsBean", subjectsBean);
					request.getRequestDispatcher("subject-edit.jsp").forward(request, response);
				}
			}
			//Show all subjects
			else {
				ArrayList<SubjectsBean> subjectsList = SubjectsDAO.findAll(user);
				request.setAttribute("subjectsList", subjectsList);
				request.getRequestDispatcher("subjects.jsp").forward(request, response);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UserBean user = (session != null) ? (UserBean) session.getAttribute("user") : null;
		
		if (user == null) {
			response.sendError(403);
			
		} 
		else {
			// Delete subject
			String deleteId = request.getParameter("delete-id");
			if (deleteId != null) {
				int id = Integer.parseInt(deleteId);
				
				boolean deletedFlag = SubjectsDAO.delete(id);
				if (deletedFlag) {
					session.setAttribute("status", "success");
					session.setAttribute("message", "Subject has been deleted successfully");
				}
				else {
					session.setAttribute("status", "danger");
					session.setAttribute("message", "Some troubles were occurred during deleting a subject");
				}
				response.sendRedirect(request.getContextPath() + "/subjects");
				return;
			}
			
			// Update existed subject
			String updateId = request.getParameter("update-id");
			if (updateId != null) {
				// Get form values.
				String subjectName = request.getParameter("subjectName").trim();
				String department = request.getParameter("department").trim();

				boolean errorMessage = true;

				if (errorMessage) {
					SubjectsBean sBean = SubjectsDAO.find(Integer.valueOf(updateId));

					// Update fields in subject bean.
					sBean.setSubjectName(subjectName);
					sBean.setDepartment(department);
					
					if (SubjectsDAO.update(sBean)) {
						session.setAttribute("status", "success");
						session.setAttribute("message", "Subject has been updated");
					}
					else {
						session.setAttribute("status", "danger");
						session.setAttribute("message", "Some troubles were occurred during updating a subject");
					}
				}
				else {
					session.setAttribute("status", "danger");
					session.setAttribute("message", errorMessage);
				}
				response.sendRedirect(request.getContextPath() + "/subjects");
				return;
			}
			
			// Add new subject
			// Get form values.
			String subjectName = request.getParameter("subjectName").trim();
			String department = request.getParameter("department").trim();
			
			//TODO: Check if subject is unique
			boolean errorMessage = true;
			
			if (errorMessage) {
				SubjectsBean bean = new SubjectsBean(subjectName, department);
				
				if (SubjectsDAO.insert(bean)) {
					session.setAttribute("status", "success");
					session.setAttribute("message", "Subject has been added");
				}
				else {
					session.setAttribute("status", "danger");
					session.setAttribute("message", "Some troubles were occurred during adding a subject");
				}
			} 
			else {
				session.setAttribute("status", "danger");
				session.setAttribute("message", errorMessage);
			}
			
			response.sendRedirect(request.getContextPath() + "/subjects");
		}
	}
}
