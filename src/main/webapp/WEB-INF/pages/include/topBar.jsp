<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- topbar starts -->
	<div class="navbar">
		<div class="navbar-inner">
			<div class="container-fluid">
				<a class="btn btn-navbar" data-toggle="collapse"
					data-target=".top-nav.nav-collapse,.sidebar-nav.nav-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
				</a> 
				<a class="brand" href="${pageContext.request.contextPath}/"> <img alt="Charisma Logo"
					src="${pageContext.request.contextPath}/resources/img/logo20.png" />
					<span>TIGeorgia</span>
				</a>
				<div class="pull-right nav-collapse">
					<ul class="nav">
						<li><a href="<c:url value="/j_spring_security_logout" />" >Logout</a></li>
					</ul>
				</div>

				<!--/.nav-collapse -->
			</div>
		</div>
	</div>
<!-- topbar ends -->