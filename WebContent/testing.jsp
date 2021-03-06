<%@page import="beans.QuestionBean"%>
<%@page import="beans.AnswerBean"%>
<%@page import="beans.TestBean"%>
<%@page import="beans.FileBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<% String basePath = request.getContextPath(); %>
<% String status = (String) request.getAttribute("status"); %>
<% String message = (String) request.getAttribute("message"); %>
<% String saveDir = (String) request.getAttribute("saveDir"); %>
<% saveDir = saveDir.replaceAll("\\\\", "/"); %>
<% TestBean test = (TestBean) request.getAttribute("test"); %>
<% ArrayList<QuestionBean> questions = (ArrayList<QuestionBean>) request.getAttribute("questions"); %>
<% int [] questionsId = new int[questions.size()]; %>

<%@ include file="header.jsp" %>

<%@ include file="menu.jsp" %>

	<div class="container">

		<%	if (status != null && message != null) { %>
			<div class="alert alert-${status}">
				<p>${message}</p>
			</div>
		<% } %>
		<div class="col-xs-12 col-sm-12 col-md-12">
			<div class="progress">
	  			<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="1" 
	  				aria-valuemin="0" aria-valuemax="15">
	    			<p><span id="current-number-question"></span> / <span id="all-questions"></span></p> 
	  			</div>
			</div>
		</div>
		<div class="col-md-8 col-md-offset-1 col-sm-8 col-sm-offset-1 col-xs-12">
			<form action="<%= basePath %>/testing/" id="testing-form" method="post" id="testing-form" class="form-horizontal" >
				<% int i = 0; %>
				<% for(QuestionBean question : questions) { %>
					<% questionsId[i] = question.getId(); %>
					<div class="question-id-<%= question.getId() %> row question-block uncompleted">
	               		<div class="form-group">
		                    <h2 class="question-text"><%= question.getQuestionText() %></h2>
		                   	<input type="hidden" class="question-id" name="question-id<%= i %>" 
		                   		value="<%= question.getId() %>">
	                   	</div>
	                   	<% FileBean image = question.getImage(); %>
		  				<% if (image != null) { %>
			  				<img class="question-img" src="<%= basePath + "/" + saveDir + "/" + image.getName() %>" 
			  					alt="<%= image.getName() %>" />
		  				<% } %>
	                   	<hr>
	                   	<ul class="answers-list">
		                   	<% for(AnswerBean answer : question.getAnswers()) { %>
		                   		<li><div class="form-group">
		                   			<% if (question.getTrueAnswers() == 1) { %>
					                   	<div class="radio">
											<label class="answer-text">
										    	<input type="radio" class="radio" name="answer-to-question<%= i %>"
										    		value="<%= answer.getAnswerId() %>">
										    	<%= answer.getAnswerText() %>
										  	</label>
										</div>
									<% } else { %>
										<div class="checkboxes">
											<label class="answer-text">
										    	<input type="checkbox" name="answer-to-question<%= i %>" value="<%= answer.getAnswerId() %>">
										    	<span class="answer-text"><%= answer.getAnswerText() %></span>
										  	</label>
										</div>
									<% } %>
								</div></li>
							<% } %>
						</ul>
		            </div>
		            <% i++; %>
	            <% } %>
	            <div class="row test-complete-info hidden">
	               	<div class="form-group">
						<h2 class="question-text">
							<span class="translate" data-lang-key="You have given the answers to all questions"></span>!
						</h2>
						<h3>
							<small>
								<span class="translate" data-lang-key="For checking click the Complete"></span>.
							</small>
						</h3>
					</div>
					<hr>
				</div>
	            <div class="form-group">
	           		<button type="button" class="btn btn-lg btn-info" id="miss-question">Miss</button>
					<button type="button" class="btn btn-lg btn-success" id="next-question">Next</button>
				</div>
				<div class="form-group">
					<input type="hidden" name="test-id" value="<%= test.getId() %>"/>
					<input type="hidden" name="questions-number" value="<%= questions.size() %>"/>
					<input type="submit" class="btn btn-primary btn-lg hidden" value="Complete"/>
				</div>
			</form>
		</div>
		<div class="col-xs-12 col-sm-3 col-md-3 timer-container">
			<div class="timer" data-timer="<%= test.getTime() %>"></div>
			<span id="test-time" class="hidden"><%= test.getTime() %></span>
		</div>
		<div>
			<button type="button" class="btn btn-lg btn-danger pull-right" id="turbo-mode" 
				data-toggle="popover" data-content="Система автоматично підставить відповіді на всі запитання та відправить на перевірку!"
					title="Режим швидкого тестування" data-placement="top">TURBO MODE</button>
		</div>
		<input type="hidden" name="questions-id-list" value="<%= Arrays.toString(questionsId) %>" />
	</div>
	
<%@ include file="footer.jsp" %>
