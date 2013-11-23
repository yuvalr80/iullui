<%@ page language="java" session="false" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<meta charset="utf-8">
	<title>iullui - About</title>
	<c:set var="appDesc" value="iullui is a social app for content matching"/>	
	<c:set var="engageText" value="Tell iullui what you like, see what other people recommend you and make your own recommendations."/>
	<meta name="description"    content="<c:out value="${appDesc}"/>. <c:out value="${engageText}"/>"/> 	
 	<meta name="keywords" 		content="iullui, content, matching, recommend, recommended, recommendation, recommendations, interest, if you like, you might also like, you might be interested in"/>
	<meta property="fb:app_id"      content="<c:out value="${appId}"/>" /> 
 	<meta property="og:url"         content="<c:out value="${appBaseUrl}"/>/about" /> 
	<meta property="og:title"       content="iullui - About" />
	<meta property="og:image"       content="<c:out value="${appBaseUrl}/images/iullui_logo_icon.png"/>" />
	<meta property="og:description" content="iullui is a social app for content matching. Tell iullui what you like, see what other people recommend you and make your own recommendations." />
	<meta property="og:site_name"	content="iullui" />
	<script type="text/javascript" src="js/jquery-1.8.2.min.js" ></script>
	<script type="text/javascript" src="js/jquery.cookie.js"></script>
	<link media="all" rel="icon" type="image/png" href="images/favicon.png" />
	<link media="all" rel="stylesheet" type="text/css" href="css/all.css" />
	<link media="all" rel="stylesheet" type="text/css" href="css/info-page.css" />
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<link rel="stylesheet" type="text/css" href="css/ie.css" media="screen"/>
	<![endif]-->
</head>
<body id="about">

<script type="text/javascript" src="scripts/ga.js"></script>

<div class="wrapper">
	<c:import url="header.jsp"/>
	<div id="mainBody" class="main">
		<div class="row-block">
			<div class="left-block">
				<h2>Welcome to iullui!</h2>
				<p><span>iullui</span> is a content matching social app that enables people to find and recommend new content based on their current likes.</p>
				<p><span>iullui users</span> can enter their favorite music, movies, books or any other interests, see what other people recommend them and add their own matches.</p>
				<p><span>artists and content makers</span> can gain well-targeted exposure by adding their pages as iullui items and matching them with their audience preferences.</p>
			</div>
			<div class="right-block">
				<h2 class="block-title">Artists, Content makers and Facebook page owners</h2>
				<p><span>Are you an artist or a content maker?</span> Do you have a Facebook page that you wish to promote? This is your chance to expose it to your best targeted audience in just a few simple steps!</p>
				<p><span>Since iullui is a content matching app,</span> users come to search and make recommendations that match their current preferences. </p>
				<p><span>You can promote yourself</span> by matching your page with other content such as music, films, books or different themes.  By matching with the right content, you are most likely to gain well-targeted exposure and reach genuine new fans who value your work.</p>
				<p style="text-align:center;"><a href="/start" id="btnLearnMore" type="submit" class="button">Learn more...</a></p>
			</div>
			<div class="left-block">
				<h2>How do we help, say, musicians?</h2>
				<div class="video-block">
					<iframe width="400" height="225" src="http://www.youtube.com/embed/bOcJJEaiSTI" frameborder="0" allowfullscreen></iframe>				
				</div>
			</div>
			<div class="right-block">
				<h2 class="block-title">Got any more questions?</h2>
				<p>Check out our <a href="/support">Support</a> page or <a href="/contact">Contact us</a>.</p>
			</div>
		</div>
	</div>
	<br/><br/>
	<c:import url="footer.jsp"/>
</div>

</body>
 
</html>