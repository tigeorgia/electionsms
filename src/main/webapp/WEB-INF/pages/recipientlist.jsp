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
						<li>Show recipients</li>
					</ul>
				</div>

				<div class="row-fluid sortable">
					<div class="box span12">
						<div class="box-header well" data-original-title>
							<h2>
								<i class="icon-user"></i> List of recipients
							</h2>
						</div>
						<div class="box-content">
							<table
								class="table table-striped table-bordered bootstrap-datatable datatable">
								<thead>
									<tr>
										<th>#</th>
										<th>Name</th>
										<th>Phone(s)</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="person" items="${recipients}" varStatus="loopCount" begin="0">
										<tr>
											<td>${loopCount.count}</td>
											<td>${person.name}</td>
											<td class="center">
												<c:forEach var="number" items="${person.numbers}">
													${number}&nbsp;
												</c:forEach>
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