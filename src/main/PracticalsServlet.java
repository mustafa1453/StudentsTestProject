package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.FileUploadManager;
import dao.FileDAO;
import dao.GroupsDAO;
import dao.PracticalsDAO;
import dao.SubjectsDAO;
import dao.UserDAO;
import beans.FileBean;
import beans.PracticalsBean;
import beans.UserBean;

/**
 * Servlet implementation class PracticalsServlet
 */
@WebServlet("/practicals")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
				 maxFileSize=1024*1024*10,      // 10MB
				 maxRequestSize=1024*1024*50)   // 50MB
public class PracticalsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String saveDir = "files" + File.separator + "practicalFiles";

	/**
     * Name of the directory where uploaded files will be saved, relative to
     * the web application directory.
     */

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UserBean user = (session != null) ? (UserBean) session.getAttribute("user") : null;
		
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login");
		} else {
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

			if (id == null) {
				// Show all practicals
				Map<String, ArrayList<PracticalsBean>> practicalsMap = PracticalsDAO.findAll(user);
				HashMap<String, String> groups = GroupsDAO.getGroupsByTeacher(user.getId());
				
				int num = 0;
				for (ArrayList<PracticalsBean> beans : practicalsMap.values()) {
					num += beans.size();
				}
				
				request.setAttribute("practicalsMap", practicalsMap);
				request.setAttribute("practicalsNum", num);
				request.setAttribute("currentUser", user);
				request.setAttribute("groupsMap", groups);
				request.getRequestDispatcher("practicals.jsp").forward(request, response);
			} else {
				// Show practical info, or editing form for practical/.
				String jsp = edit ? "practical-edit.jsp" : "practical-view.jsp";
				try {
					int practicalId = Integer.parseInt(id);
					// Show specific practical
					PracticalsBean practicalBean = PracticalsDAO.find(practicalId);
					
					if (practicalBean == null) {
						session.setAttribute("status", "warning");
						session.setAttribute("message", "Such practical does not exist");
						response.sendRedirect(request.getContextPath() + "/practicals");
					} else {
						request.setAttribute("practicalBean", practicalBean);
						request.setAttribute("saveDir", saveDir);
						request.getRequestDispatcher(jsp).forward(request, response);
					}
				} catch (NumberFormatException e) {
					response.sendError(404);
				}
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
		} else if (!user.getAccess("practicals")) {
			response.sendError(403);
		} else {
			// Update existed practical
			String updateId = request.getParameter("update-id");
			if (updateId != null) {
				// Get form values.
				String subject = request.getParameter("subject").trim();
				String title = request.getParameter("title").trim();
				String body = request.getParameter("body").trim();
				
				int subjectId = SubjectsDAO.find(subject);
				String errorMessage = practicalValidate(title, subject, 1);

				if (errorMessage == null) {
					PracticalsBean pBean = PracticalsDAO.find(Integer.valueOf(updateId));

					// Upload additional files.
					String filePath = request.getServletContext().getRealPath("") + File.separator + saveDir;
					ArrayList<String> fileNames = FileUploadManager.uploadFiles("upload", filePath, request.getParts());

					// Save uploaded files in DB.
					if (!fileNames.isEmpty()) {
						if (FileDAO.insert(pBean.getId(), "practicals", fileNames)) {
							session.setAttribute("status", "success");
							session.setAttribute("message", "Lecture has been added");
						} else {
							session.setAttribute("status", "danger");
							session.setAttribute("message", "Some troubles were occurred during writing file info to db");
						}
					}
					// Update fields in practical bean.
					pBean.setTitle(title);
					pBean.setBody(body);
					pBean.setSubjectId(subjectId);

					if (PracticalsDAO.update(pBean)) {
						session.setAttribute("status", "success");
						session.setAttribute("message", "Practical has been updated");
					} else {
						session.setAttribute("status", "danger");
						session.setAttribute("message", "Some troubles were occurred during updating a practical");
					}
				} else {
					session.setAttribute("status", "danger");
					session.setAttribute("message", errorMessage);
				}
				response.sendRedirect(request.getContextPath() + "/practicals");
			}

			// Create new practical
			// Save uploaded file, and retrieve his path.
			String filePath = request.getServletContext().getRealPath("") + File.separator + saveDir;
			ArrayList<String> fileNames = FileUploadManager.uploadFiles("upload", filePath, request.getParts());

			// Get form values.
			String subject = request.getParameter("subject").trim();
			String title = request.getParameter("title").trim();
			String body = request.getParameter("body").trim();
			int teacherId = 0;
			
			int subjectId = SubjectsDAO.find(subject);
			
			if (subjectId == 0) {
				session.setAttribute("status", "danger");
				session.setAttribute("message", "Please select subject from autocomplete list.");
				response.sendRedirect(request.getContextPath() + "/practicals");
				return;
			}
			
			if (user.getRole() == 2) {
				String name = request.getParameter("teacher");
				try {
					teacherId = Integer.parseInt(name.substring(name.indexOf("[") + 1, name.indexOf("]")));
				} catch (Exception e) {
					session.setAttribute("status", "warning");
					session.setAttribute("message", "Please select teacher from autocomplete list.");
					response.sendRedirect(request.getContextPath() + "/practicals");
					return;
				}
			} else {
				teacherId = user.getId();
			}
			UserBean teacher = UserDAO.findTeacher(teacherId);
			String errorMessage = practicalValidate(title, subject, 0);

			if (errorMessage == null && teacher != null) {
				// Create new practicals bean.
				PracticalsBean bean = new PracticalsBean(teacher.getId(), subjectId, title, body);
				
				if (PracticalsDAO.insert(bean)) {
					if (fileNames.isEmpty()) {
						session.setAttribute("status", "success");
						session.setAttribute("message", "Practical has been added");
					} else {
						bean = PracticalsDAO.find(subjectId, title);
						if (FileDAO.insert(bean.getId(), "practicals", fileNames)) {
							session.setAttribute("status", "success");
							session.setAttribute("message", "Practical has been added");
						} else {
							session.setAttribute("status", "danger");
							session.setAttribute("message", "Some troubles were occurred during writing file info to db");
						}
					}
				} else {
					session.setAttribute("status", "danger");
					session.setAttribute("message", "Some troubles were occurred during creating a practical");
				}
			} else {
				session.setAttribute("status", "danger");
				session.setAttribute("message", errorMessage);
			}
			response.sendRedirect(request.getContextPath() + "/practicals");
		}
	}

	private boolean practicalBelongSubject(String title, String subjectId, int numExisted) {
		return PracticalsDAO.findPracticalsCountInSubject(title, subjectId) > numExisted;
	}

	private String practicalValidate(String title, String subjectId, int numExisted) {
		if (practicalBelongSubject(title, subjectId, numExisted)) {
			return "Subject should not contain the same practicals";
		}
		return null;
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		UserBean user = (session != null) ? (UserBean) session.getAttribute("user") : null;
		
		if (user == null) {
			response.sendError(403);	
		} else {
			String deleteId = request.getHeader("id");
			if (deleteId != null) {
				int id = Integer.valueOf(deleteId);
				PracticalsBean pBean = PracticalsDAO.find(id);

				// Delete file from file system.
				ArrayList<FileBean> fileBeans = FileDAO.findAll(pBean.getId(), "practicals");
				String savePath = request.getServletContext().getRealPath("") + File.separator + saveDir;
				
				if (!fileBeans.isEmpty()) {
					FileUploadManager.deleteFiles(fileBeans, savePath);
					FileDAO.deleteAll(fileBeans);
				}

				if (PracticalsDAO.delete(id)) {
					response.getOutputStream().println("Lecture has been deleted successfully.");
				} else {
					response.getOutputStream().println("Some troubles during deleting a lecture.");
				}
			}
		}
	}
}
