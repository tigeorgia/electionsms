<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
						<li>Show recipients</li>
					</ul>
				</div>
				
				<div class="row-fluid sortable">
					<div class="box span12">
						<div class="box-header well" data-original-title>
							<h2>
								<i class="icon-user"></i> Upload Election contacts
							</h2>
						</div>
						<div class="box-content">
							<div class="control-group">
								If you want to work with a new list of contacts, for the Election list, upload a new CSV file underneath.<br />
								The CSV file must have one of the 2 following formats: 'Name,Numbers,Groups' or 'Name,Language,Numbers,Groups'.<br />
								(If several 'Numbers' or 'Groups', values have to be separated by '|', in each column)
							</div>
							<c:if test="${isUploadedSuccessfully == true }">
								<div class="alert alert-success">
									<button type="button" class="close" data-dismiss="alert">×</button>
									<strong>Success!</strong> The CSV file has been uploaded successfully, and been loaded in the Election table below.
								</div>
							</c:if>
							<div class="control-group">
								<form:form modelAttribute="uploadedFile" enctype="multipart/form-data" 
								id="uploadbanner" action="${pageContext.request.contextPath}/uploadlist"
								method="POST" class="form-horizontal">
									<c:if test="${uploadErrorMessage != null }">
										<div class="alert alert-error">
											<button type="button" class="close" data-dismiss="alert">×</button>
											<strong>ERROR!</strong> ${uploadErrorMessage}
										</div>
									</c:if>
									<form:input id="fileupload" type="file" path="file" name="file" /> 
									<input type="submit" value="Upload" id="submit"  />
								</form:form>
							</div>
							<div class="control-group">
								<a href="${pageContext.request.contextPath}/download/ElectionPhoneNumberList.csv">Download current Election contact list (CSV file)</a>
							</div>
						</div>
					</div>
				</div>
				
				
				<div class="row-fluid sortable">
					<div class="box span12">
						<div class="box-header well" data-original-title>
							<h2>
								<i class="icon-user"></i> Parliamentary contacts (retrieved from MyParliament.ge)
							</h2>
							<div class="box-icon">
								<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
							</div>
						</div>
						<div class="box-content">
							<c:if test="${errorMessageparliament != null}">
								<div class="alert alert-error">
									<button type="button" class="close" data-dismiss="alert">×</button>
										${errorMessageparliament}
								</div>
							</c:if>
							<table
								class="table table-striped table-bordered bootstrap-datatable datatable">
								<thead>
									<tr>
										<th>#</th>
										<th>Name</th>
										<th>Language</th>
										<th>Phone(s)</th>
										<th>Group</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="person" items="${parliamentRecipients}"
										varStatus="loopCount" begin="0">
										<tr>
											<td>${loopCount.count}</td>
											<td>${person.name}</td>
											<td>${person.language}</td>
											<td class="center"><c:forEach var="number"
													items="${person.numbers}">
													${number}&nbsp;
												</c:forEach></td>
											<td>
												<c:choose>
													<c:when test="${person.groups != null && fn:length(person.groups) gt 1}">
														<select>
															<c:forEach var="group"	items="${person.groups}">
																<option>${group}</option>
															</c:forEach>
														</select>
													</c:when>
													<c:when test="${person.groups != null && fn:length(person.groups) eq 1}">
														${person.groups[0]}
													</c:when>
													<c:otherwise>
														No group attached
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
					<!--/span-->
				</div>
				<!--/row-->
				
				<div class="row-fluid sortable">
					<div class="box span12">
						<div class="box-header well" data-original-title>
							<h2>
								<i class="icon-user"></i> Election contacts (retrieved from uploaded CSV file)
							</h2>
							<div class="box-icon">
								<a href="#" class="btn btn-minimize btn-round"><i class="icon-chevron-up"></i></a>
							</div>
						</div>
						<div class="box-content">
							<c:if test="${errorMessageelection != null}">
								<div class="alert alert-error">
									<button type="button" class="close" data-dismiss="alert">×</button>
										${errorMessageelection}
								</div>
							</c:if>
							<table
								class="table table-striped table-bordered bootstrap-datatable datatable">
								<thead>
									<tr>
										<th>#</th>
										<th>Name</th>
										<th>Language</th>
										<th>Phone(s)</th>
										<th>Group</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="person" items="${electionRecipients}"
										varStatus="loopCount" begin="0">
										<tr>
											<td>${loopCount.count}</td>
											<td>${person.name}</td>
											<td>${person.language}</td>
											<td class="center"><c:forEach var="number"
													items="${person.numbers}">
													${number}&nbsp;
												</c:forEach></td>
											<td>
												<c:choose>
													<c:when test="${person.groups != null && fn:length(person.groups) gt 1}">
														<select>
															<c:forEach var="group"	items="${person.groups}">
																<option>${group}</option>
															</c:forEach>
														</select>
													</c:when>
													<c:when test="${person.groups != null && fn:length(person.groups) eq 1}">
														${person.groups[0]}
													</c:when>
													<c:otherwise>
														No group attached
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
					<!--/span-->
				</div>

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
