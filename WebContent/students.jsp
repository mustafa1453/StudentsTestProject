<%@page import="beans.UserBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<% String basePath = request.getContextPath(); %>
<% Map<String, ArrayList<UserBean>> studentMap = (HashMap<String, ArrayList<UserBean>>) request.getAttribute("studentMap"); %>
<% String status = (String) request.getAttribute("status"); %>
<% String message = (String) request.getAttribute("message"); %>

<%@ include file="header.jsp"%>
<%@ include file="menu.jsp"%>

<div class="container">
	<%	if (status != null && message != null) { %>
	<div class="alert alert-${status}">
		<p>${message}</p>
	</div>
	<% } %>
	
</div>

<div class="container">

	<h1>Students</h1>
		<% out.print(studentMap.isEmpty() ? "You don't have any students" : ""); %>
		<% int i = 0; %>
		<% for (String groupName : studentMap.keySet()) { %>
		<% i++; %>
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="heading-<%= i %>">
				<h4 class="panel-title">
					<a data-toggle="collapse" data-parent="#accordion" href="#collapse-<%= i %>" aria-controls="collapse-<%= i %>">
					  <%= groupName %>
					</a>
				</h4>
			</div>
			<div id="collapse-<%= i %>" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-<%= i %>">
				<div class="panel-body">
					<table class="table">
						<thead>
							<tr>
								<th data-field="avatar" data-align="center" data-sortable="false">Avatar</th>
								<th data-field="firstName" data-align="center" data-sortable="true">First Name</th>
								<th data-field="lastName" data-align="center" data-sortable="true">Last Name</th>
								<th data-field="delete" data-align="center">Delete from list</th>
							</tr>
						</thead>
						<tbody>
						<% for (UserBean student : studentMap.get(groupName)) { %>
							<tr>
							<td>
								<%  if (student.getAvatar().isEmpty()) { %>
									<img src="<%= defaultAvatar %>" class="img-circle avatar-table">
								<% } else { %>
									<img src="<%= uploadAvatarPath + File.separator + student.getAvatar() %>" class="img-circle avatar-table">
								<% } %>
							</td>
							<td><% out.print(student.getFirstName()); %></td>
							<td><% out.print(student.getLastName()); %></td>
							<td>
								<form action="<%= basePath %>/students" method="post">
									<button type="submit" class="btn btn-danger">Delete</button>
									<input type="hidden" name="delete-id" value="<%= student.getId() %>">
								</form>
							</td>
						</tr>
						<% } %>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<% } %>
</div>

<%@ include file="footer.jsp" %>
