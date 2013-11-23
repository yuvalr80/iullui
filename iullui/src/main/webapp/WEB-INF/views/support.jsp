<%@ page language="java" session="false" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<meta charset="utf-8">
	<title>iullui - Support</title>
	<c:set var="appDesc" value="iullui is a social app for content matching"/>	
	<c:set var="engageText" value="Tell iullui what you like, see what other people recommend you and make your own recommendations."/>
	<meta name="description"    content="<c:out value="${appDesc}"/>. <c:out value="${engageText}"/>"/> 	
	<meta property="fb:app_id"      content="<c:out value="${appId}"/>" /> 
 	<meta property="og:url"         content="<c:out value="${appBaseUrl}"/>/support" /> 
	<meta property="og:title"       content="iullui - Support" />
	<meta property="og:image"       content="<c:out value="${appBaseUrl}/images/iullui_logo_icon.png"/>" />
	<meta property="og:description" content="iullui is a social app for content matching. Tell iullui what you like, see what other people recommend you and make your own recommendations." />
	<meta property="og:site_name"	content="iullui" />
	<link media="all" rel="icon" type="image/png" href="images/favicon.png" />
	<link media="all" rel="stylesheet" type="text/css" href="css/all.css" />
	<link media="all" rel="stylesheet" type="text/css" href="css/info-page.css" />
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<link rel="stylesheet" type="text/css" href="css/ie.css" media="screen"/>
	<![endif]-->
</head>
<body id="support">

<script type="text/javascript" src="scripts/ga.js"></script>

<div class="wrapper">
	<c:import url="header.jsp"/>
	<div id="mainBody" class="main">
		<p style="text-align:center; color:#444444; font-size:large; margin-top:100px;">Coming soon...</p>
	</div>
	<c:import url="footer.jsp"/>
</div>

</body>
 
</html>