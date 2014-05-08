<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<!--
		Charisma v1.0.0
		
		Copyright 2012 Muhammad Usman
		Licensed under the Apache License v2.0
		http://www.apache.org/licenses/LICENSE-2.0

		http://usman.it
		http://twitter.com/halalit_usman
	-->
	<meta charset="utf-8">
	<title>Election SMS</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="Charisma, a fully featured, responsive, HTML5, Bootstrap admin template.">
	<meta name="author" content="Muhammad Usman">
	
	<script>var ctx = "${pageContext.request.contextPath}"</script>

	<!-- The styles -->
	<link id="bs-css" href="<spring:url value='/resources/css/bootstrap-cerulean.css'/>" rel="stylesheet" type="text/css"/>
	<style type="text/css">
	  body {
		padding-bottom: 40px;
	  }
	  .sidebar-nav {
		padding: 9px 0;
	  }
	</style>
	<link href="<spring:url value='/resources/css/bootstrap-responsive.css'/>" rel="stylesheet" type="text/css"/>
	<link href="<spring:url value='/resources/css/charisma-app.css'/>" rel="stylesheet" type="text/css"/>
	<!-- <link href="<spring:url value='/resources/css/jquery-ui-1.8.21.custom.css'/>" rel="stylesheet"type="text/css"/>
	<link href="<spring:url value='/resources/css/chosen.css'/>" rel='stylesheet' type="text/css"/> -->
	<link href="<spring:url value='/resources/css/uniform.default.css'/>" rel='stylesheet' type="text/css"/>
	<!-- <link href="<spring:url value='/resources/css/colorbox.css'/>" rel='stylesheet' type="text/css"/> 
	<link href="<spring:url value='/resources/css/jquery.cleditor.css'/>" rel='stylesheet' type="text/css"/>
	<link href="<spring:url value='/resources/css/jquery.noty.css'/>" rel='stylesheet' type="text/css"/>
	<link href="<spring:url value='/resources/css/noty_theme_default.css'/>" rel='stylesheet' type="text/css"/> 	
	<link href="<spring:url value='/resources/css/elfinder.min.css'/>" rel='stylesheet' type="text/css"/>
	<link href="<spring:url value='/resources/css/elfinder.theme.css'/>" rel='stylesheet' type="text/css"/>
	<link href="<spring:url value='/resources/css/jquery.iphone.toggle.css'/>" rel='stylesheet' type="text/css"/>
	<link href="<spring:url value='/resources/css/opa-icons.css'/>" rel='stylesheet' type="text/css"/> -->
	<link href="<spring:url value='/resources/css/uploadify.css'/>" rel='stylesheet' type="text/css"/>

	
	<!-- The HTML5 shim, for IE6-8 support of HTML5 elements -->
	<!--[if lt IE 9]>
	  <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->