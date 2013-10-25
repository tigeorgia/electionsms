<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

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
				<!-- content starts -->
				<div>
					<ul class="breadcrumb">
						<li><a href="${pageContext.request.contextPath}/">Home</a> <span
							class="divider">/</span></li>
						<li>Send message</li>
					</ul>
				</div>

				<c:if test="${errorMessage != null}">
					<div class="alert alert-error">
						<button type="button" class="close" data-dismiss="alert">×</button>
						<strong>Error</strong> ${errorMessage}
					</div>
				</c:if>
				
				<c:if test="${didntReceiveMessage != null}">
					<div class="alert alert-info">
						<button type="button" class="close" data-dismiss="alert">×</button>
						<strong>Warning</strong> ${didntReceiveMessage}
					</div>
				</c:if>

				<c:if test="${validMessage != null}">
					<div class="alert alert-success">
							<button type="button" class="close" data-dismiss="alert">×</button>
							<strong>Success!</strong> ${validMessage}
						</div>
				</c:if>

				<div class="row-fluid sortable">
					<div class="box span12">
						<div class="box-header well" data-original-title>
							<h2>
								<i class="icon-edit"></i> Send messages to recipients
							</h2>
						</div>
						<div class="box-content">
							<form:form modelAttribute="messageModel"
								action="${pageContext.request.contextPath}/sendmessage/result"
								method="POST" class="form-horizontal">
								<fieldset>
									<div class="control-group">
										<label class="control-label" for="textarea2">Type your
											message here (160 characters max.)</label>
										<div class="controls">
											<form:textarea class="message" id="textarea2" rows="8"
												cols="40" maxlength="160" path="body"></form:textarea>
											<span class="countdown"></span>
										</div>
										<div class="controls">
											<label class="checkbox inline"> <input
												type="checkbox" id="allGroupCheckbox" value="All" /> All
											</label>
											<c:forEach var="group" items="${groups}">
												<label class="checkbox inline"> <form:checkbox
														path="chosenGroups" class="groupCheckbox" value="${group}" />
													${group}
												</label>
											</c:forEach>
										</div>
									</div>
									<div class="form-actions">
										<button type="submit" id="sendMessageSubmitId"
											class="btn btn-primary">Send message</button>
									</div>
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
