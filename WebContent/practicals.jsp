<%@page import="beans.UserBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Set"%>
<%@page import="beans.PracticalsBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<% String basePath = request.getContextPath(); %>
<% UserBean currentUser = (UserBean) request.getAttribute("currentUser"); %>
<% String status = (String) request.getAttribute("status"); %>
<% String message = (String) request.getAttribute("message"); %>
<% HashMap<String, ArrayList<PracticalsBean>> practicalsMap = (HashMap<String, ArrayList<PracticalsBean>>) request.getAttribute("practicalsMap"); %>
<% HashMap<String, String> groups = (HashMap<String, String>) request.getAttribute("groupsMap"); %>
<% int practicalsNum = (int) request.getAttribute("practicalsNum"); %>

<%@ include file="header.jsp" %>

<%@ include file="menu.jsp" %>

<div class="container">
	<%	if (status != null && message != null) { %>
		<div class="alert alert-${status}">
			<p>${message}</p>
		</div>
	<% } %>
	
	<% if (currentUser.getRole() > 0) { %>
	
		<h3 class="lead"><span class="translate" data-lang-key="Add practical"></span></h3>
		<form action="<%= basePath %>/practicals" class="form" method="post"
			class="form-horizontal" enctype="multipart/form-data">
			
			<div class="form-group">
				<label for="subject" class="col-xs-12 col-sm-2 col-md-2 control-label">
					<span class="translate" data-lang-key="Subject"></span>*
				</label>
				<div class="col-xs-12 col-sm-10 col-md-10">
					<input name="subject" type="text" class="form-control typeahead"
						class="subject" required autocomplete="off" data-autocomplete-url="autocomplete/subjects">
				</div>
			</div>
			
			<% if (currentUser.getRole() == 2) { %>
				<div class="form-group">
					<label for="teacher" class="col-xs-12 col-sm-2 col-md-2 control-label">
						<span class="translate" data-lang-key="Teacher"></span>*
					</label>
					<div class="col-xs-12 col-sm-10 col-md-10">
						<input name="teacher" type="text" class="form-control typeahead"
							required autocomplete="off" data-autocomplete-url="autocomplete/teachers">
					</div>
				</div>
			<% } %>
	
			<div class="form-group">
				<label for="title" class="col-xs-12 col-sm-2 col-md-2 control-label">
					<span class="translate" data-lang-key="Title"></span>*
				</label>
				<div class="col-sm-10">
					<input name="title" type="text" class="form-control" id="title" required>
				</div>
			</div>
	
			<div class="form-group">
				<label for="body" class="col-xs-12 col-sm-2 col-md-2 control-label required">
					<span class="translate" data-lang-key="Body"></span>
				</label>
				<div class="col-xs-12 col-sm-10 col-md-10">
					<textarea class="ckeditor" name="body" class="form-control" rows="3"></textarea>
				</div>
			</div>
	
			<div class="form-group">
				<label for="upload" class="col-xs-12 col-sm-2 col-md-2 control-label required">
					<span class="translate" data-lang-key="Upload files"></span>
				</label>
				<div class="col-xs-12 col-sm-10 col-md-10">
					<input id="upload" type="file" class="file" name="upload" data-preview-file-type="text" multiple>
					<p class="help-block">
						<span class="translate" data-lang-key="File size not more then"></span> 10 MB.
						<span class="translate" data-lang-key="Allowed formats"></span>: pdf, doc, docx.
					</p>
				</div>
			</div>
	
			<div class="form-group">
				<div class="col-xs-12 col-sm-offset-2 col-sm-10 col-md-offset-2 col-md-10">
					<button type="submit" class="btn btn-primary">
						<span class="translate" data-lang-key="Add"></span>
					</button>
				</div>
			</div>
		</form>
	<% } %>
</div>

<div class="container">
	<% if (practicalsNum == 0) { %>
		<% if (currentUser.getRole() == 0) { %>
			<h1><span class="translate" data-lang-key="We don't have practicals for you"></span>.</h1>
		<% } else if (currentUser.getRole() == 1) { %>
			<h1><span class="translate" data-lang-key="You don't have practicals"></span>.</h1>
			<h2><small><span class="translate" data-lang-key="You can add them on the form over this message"></span>.</small></h2>
		<% } else { %>
			<h1><span class="translate" data-lang-key="There are no any practicals"></span></h1>
		<% } %>
	<% } else { %>
		<h1>
			<span class="translate" data-lang-key="Practicals"></span>
			<span class="item-number">(<%= practicalsNum %>)</span>
		</h1>
	<% } %>
	<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
		<% int i = 0; %>
		<% for (String subject : practicalsMap.keySet()) { %>
		<% i++; %>
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="heading-<%= i %>">
				<h4 class="panel-title">
					<a class="subject-<%= i %>" data-toggle="collapse" data-parent="#accordion" href="#collapse-<%= i %>" 
						aria-controls="collapse-<%= i %>">
					  <%= subject %>
					</a>
				</h4>
			</div>
			<div id="collapse-<%= i %>" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-<%= i %>">
				<div class="panel-body">
				
					<% if (currentUser.getRole() == 1) { %>
						<p class="help-block"><span class="translate" data-lang-key="Share this subject to groups"></span>:
							<input type="text" value="<%= groups.get(subject) == null ? "" :  groups.get(subject) %>" 
								class="tokenfield" name="groupName" required autocomplete="off" />
							<input type="submit" value="Share" class="btn btn-info btn-share assign-subject-group" 
								data-num="<%= i %>" data-subject="<%= subject %>">
						</p>
					<% } %>
					<div class="table-responsive">	
						<table class="table" data-unique-id="id">
							<thead>
								<tr>
									<% if (currentUser.getRole() == 2) { %>
										<th data-field="id" data-align="center" data-sortable="true">ID</th>
									<% } %>
									<th data-field="title" data-align="center" data-sortable="true">
										<span class="translate" data-lang-key="Title"></span>
									</th>
						            <th data-field="view" data-align="center">
						            	<span class="translate" data-lang-key="View"></span>
						            </th>
						            <% if (currentUser.getRole() > 0) { %>
						            	<th data-field="edit" data-align="center">
						            		<span class="translate" data-lang-key="Edit"></span>
						            	</th>
						            	<th data-field="delete" data-align="center">
						            		<span class="translate" data-lang-key="Delete"></span>
						            	</th>
						            <%} %>
								</tr>
							</thead>
							<tbody>
								<% for (PracticalsBean practical : practicalsMap.get(subject)) { %>
									<tr>
										<% if (currentUser.getRole() == 2) { %>
											<td><%= practical.getId() %></td>
										<% } %>
										<td><%= practical.getTitle() %></td>
										<td>
											<a href="practicals?id=<%= practical.getId() %>">
												<span class="translate" data-lang-key="View"></span>
											</a>
										</td>
										<% if (currentUser.getRole() > 0) { %>
											<td>
												<a href="practicals?edit=true&id=<%= practical.getId() %>">
													<span class="translate" data-lang-key="Edit"></span>
												</a>
											</td>
											<td>
												<button class="btn btn-danger delete-item" data-id="<%= practical.getId() %>" 
														data-path="/practicals" data-item="practical">
													<span class="translate" data-lang-key="Delete"></span>
												</button>
											</td>
										<% } %>
									</tr>
								<% } %>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<% } %>
	</div>
</div>

<%@ include file="footer.jsp" %>
