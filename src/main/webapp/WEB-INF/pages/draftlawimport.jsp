<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="include/headerContent.jsp"></jsp:include>
</head>

<body>
	<jsp:include page="include/topBar.jsp"></jsp:include>
	<div class="container-fluid">
		<div class="row-fluid">

			<jsp:include page="include/leftMenu.jsp"></jsp:include>

			<div id="content" class="span10">

				<c:if test="${isUpdateSuccess}">
					<div class="alert alert-success">
						<button type="button" class="close" data-dismiss="alert">×</button>
						${successMessage}
					</div>
				</c:if>

				<!-- content starts -->
				<div>
					<ul class="breadcrumb">
						<li><a href="${pageContext.request.contextPath}/">Home</a> <span
							class="divider">/</span></li>
						<li>Draft law import</li>
					</ul>
				</div>


				<div class="row-fluid sortable">
					<div class="box span12">
						<div class="box-header well" data-original-title>
							<h2>
								<i class="icon-edit"></i> Upload draft law information
							</h2>
						</div>
						<div class="box-content">
							<form:form modelAttribute="draftLawPageModel"
								action="${pageContext.request.contextPath}/draftlaw-import/upload"
								method="POST" class="form-horizontal">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											Click on the button underneath to:
											<ol>
												<li>Test the validity of the information entered on the
													<a
													href="https://docs.google.com/a/transparency.ge/spreadsheet/ccc?key=0AtLaNvIaw4_rdEZkRWlDX2RhTlF6NEx5SU10Vll2R3c&usp=sharing"
													target="_blank"> Draft Law Google spreadsheet.</a>
												</li>
												<li>Upload the information onto the MyParliament
													database, if validation passes.</li>
											</ol>
											If the information in the spreadsheet is not valid, relevant
											error messages will be displayed here on this page, and
											nothing will be updated in the MyParliament database.
											
											Select the Google Spreadsheet to read: 
										</div>
										
										<div class="controls">
											<c:forEach items="${titleList}" var="title">
												<label class="radio"> <form:radiobutton path="page" value="${title}" />${title}</label>
												<div style="clear: both"></div>
											</c:forEach>
										</div>
										
									</div>

									<div class="control-group">
										<div class="controls">
											<!--  <a
												href="${pageContext.request.contextPath}/draftlaw-import/upload"
												class="btn btn-info">Validate & upload draft laws to
												database</a> -->
											<button type="submit" id="submitId"
												class="btn btn-info">Validate & upload draft laws to
												database</button>
										</div>
									</div>

									<c:if test="${errorMessages != null}">
										<div class="control-group">
											<div class="controls">
												<span style="color: red; font-weight: bold;">The
													MyParliament database has not been updated: there are
													${fn:length(errorMessages)} validation error(s) in the
													spreadsheet</span><br /> <span style="color: red">Please
													correct them then try to upload again:</span>
												<ol>
													<c:forEach items="${errorMessages}" var="errorMessage"
														varStatus="status">
														<c:choose>
															<c:when test="${status.count % 2 == 0}">
																<li style="background-color: #F0F8FF;">${errorMessage.message}</li>
															</c:when>
															<c:otherwise>
																<li>${errorMessage.message}</li>
															</c:otherwise>
														</c:choose>
													</c:forEach>
												</ol>
											</div>
										</div>
									</c:if>
									
									
									<c:if test="${errors != null}">
										<div class="control-group">
											<div class="controls">
												<span style="color: red; font-weight: bold;">While the database update was taking place, some errors were found:</span>
												<ol>
													<c:forEach items="${discussionErrors}" var="errorMessage"
														varStatus="status">
														<c:choose>
															<c:when test="${status.count % 2 == 0}">
																<li style="background-color: #F0F8FF;">${errorMessage.message}</li>
															</c:when>
															<c:otherwise>
																<li>${errorMessage.message}</li>
															</c:otherwise>
														</c:choose>
													</c:forEach>
												</ol>
											</div>
										</div>
									</c:if>
									
								</fieldset>
							</form:form>
						</div>
					</div>
					<!--/span-->

				</div>
				<!--/row-->

				<!-- content ends -->
			</div>
			<!--/#content.span10-->
		</div>
		<!--/fluid-row-->

		<hr>

		<div class="modal hide fade" id="myModal">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">Ã</button>
				<h3>Settings</h3>
			</div>
			<div class="modal-body">
				<p>Here settings can be configured...</p>
			</div>
			<div class="modal-footer">
				<a href="#" class="btn" data-dismiss="modal">Close</a> <a href="#"
					class="btn btn-primary">Save changes</a>
			</div>
		</div>

		<footer>
			<p class="pull-left">
				&copy; <a href="http://usman.it" target="_blank">Muhammad Usman</a>
				2012
			</p>
			<p class="pull-right">
				Powered by: <a href="http://usman.it/free-responsive-admin-template">Charisma</a>
			</p>
		</footer>

	</div>
	<!--/.fluid-container-->

	<!-- external javascript
	================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<jsp:include page="include/includedJs.jsp"></jsp:include>

</body>
</html>
