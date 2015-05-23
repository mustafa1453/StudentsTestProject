<%@page import="beans.UserBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<% String basePath = request.getContextPath(); %>
<% ArrayList<UserBean> users = (ArrayList<UserBean>) request.getAttribute("usersList"); %>
<% String usersJson = (String) request.getAttribute("usersJson"); %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>Users</title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="shortcut icon" href="<%= basePath %>/favicon.ico" type="image/x-icon">
<link rel="icon" href="<%= basePath %>/favicon.ico" type="image/x-icon">

<link rel="stylesheet" href="<%= basePath %>/css/style.css">
<link rel="stylesheet" href="<%= basePath %>/css/bootstrap.min.css">
<link rel="stylesheet" href="<%= basePath %>/css/bootstrap-theme.css">
<link rel="stylesheet" href="<%= basePath %>/css/bootstrap-table.min.css">

<script src="<%= basePath %>/js/jquery.min.js"></script>
<script src="<%= basePath %>/js/bootstrap.min.js"></script>
<script src="<%= basePath %>/js/bootstrap-table.min.js"></script>
<script src="<%= basePath %>/js/script.js"></script>

</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="<%= basePath %>">Students Test Project</a>
		</div>
		<div id="navbar" class="collapse navbar-collapse">
			<ul class="nav navbar-nav">
				<li><a href="<%= basePath %>">Home</a></li>
				<li class="active"><a href="<%= basePath %>/users">Users</a></li>
				<li><a href="<%= basePath %>/register">Register</a></li>
				<li><a href="<%= basePath %>/logout">Logout</a></li>
			</ul>
		</div>
	</div>
	</nav>


	<div class="container">

		<div class="starter-template">
			<h1>Users</h1>
<!-- 			class="table table-striped table-bordered" -->
			<table class="table" data-search="true" data-show-columns="true">
				<% out.print(users == null ? "Users are not exists" : ""); %>
			    <thead>
			        <tr>
			            <th data-field="userName" data-align="center" data-sortable="true">User Name</th>
			            <th data-field="email" data-align="center" data-sortable="true">Email</th>
			            <th data-field="firstName" data-align="center" data-sortable="true">First Name</th>
			            <th data-field="lastName" data-align="center" data-sortable="true">Last Name</th>
			            <th data-field="role" data-align="center" data-sortable="true">Role</th>
			            <th data-field="edit" data-align="center">Edit</th>
			        </tr>
			    </thead>
			    <tbody>
			    <% for (UserBean user: users) { %>
					<tr>
				        <td><% out.print(user.getUsername()); %></td>
				        <td><% out.print(user.getEmail()); %></td>
				        <td><% out.print(user.getFirstName()); %></td>
				        <td><% out.print(user.getLastName()); %></td>
				        <td><% out.print(user.getHumanRole()); %></td>
				        <td><a href="<% out.print(basePath + "/user/" + user.getId()); %>" target="_blank">Edit</a></td>
				    </tr>
				<% } %>
				</tbody>
			</table>

		</div>
	</div>

</body>
</html>
