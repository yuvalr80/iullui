<%@ page language="java" session="false" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<meta charset="utf-8">
	<title>iullui - Contact us</title>
	<c:set var="appDesc" value="iullui is a social app for content matching"/>	
	<c:set var="engageText" value="Tell iullui what you like, see what other people recommend you and make your own recommendations."/>
	<meta name="description"    content="<c:out value="${appDesc}"/>. <c:out value="${engageText}"/>"/> 	
	<meta property="fb:app_id"      content="<c:out value="${appId}"/>" /> 
 	<meta property="og:url"         content="<c:out value="${appBaseUrl}"/>/contact" /> 
	<meta property="og:title"       content="iullui - Contact us" />
	<meta property="og:image"       content="<c:out value="${appBaseUrl}/images/iullui_logo_icon.png"/>" />
	<meta property="og:description" content="iullui is a social app for content matching. Tell iullui what you like, see what other people recommend you and make your own recommendations." />
	<meta property="og:site_name"	content="iullui" />
	<link media="all" rel="icon" type="image/png" href="images/favicon.png" />
	<script type="text/javascript" src="js/jquery-1.8.2.min.js" ></script>
	<link media="all" rel="stylesheet" type="text/css" href="css/all.css" />
	<link media="all" rel="stylesheet" type="text/css" href="css/info-page.css" />	
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<link rel="stylesheet" type="text/css" href="css/ie.css" media="screen"/>
	<![endif]-->
</head>
<body id="contact">

<script type="text/javascript" src="scripts/ga.js"></script>
<script type="text/javascript">
$(function() {
	$("#allFieldsRequired").hide();
	$("#btnContact").click(function(e) {
		e.preventDefault();
		var name = $("#name").val();
		var email = $("#email").val();
		var subject = $("#subject").val();
		var message = $("#message").val();
		
		if (isEmpty(name) || isEmpty(email) || isEmpty(subject) || isEmpty(message)) {
			$("#allFieldsRequired").show();
		} 
		else {
			$("#contactForm").submit();
		}	
		
		function isEmpty(str) {
			return (str === undefined || str == null || str.replace(/^\s+|\s+$/g,"").length === 0);
		}
	});
});
</script>

<div class="wrapper">
	<c:import url="header.jsp"/>
	<div id="mainBody" class="main">
		<div class="row-block">
			<div class="left-block">
				<h2>Contact us</h2>
				<p>We would love to hear from you.</p>
				<form id="contactForm" action="/contact" method="POST">
					<c:if test="${not empty confirmContact}">
						<p class="contact-result"><span>Thanks.</span> We will get back to you soon.</p>
					</c:if>
					<p><span>Name</span> <br/><input id="name" name="name" class="contact-field" type="text"></input></p>
					<p><span>Email</span> (please double-check)<br/><input id="email" name="email" class="contact-field" type="text"></input></p>
					<p><span>Subject</span> <br/><input id="subject" name="subject" class="contact-field" type="text"></input></p>
					<p><span>Message</span> <br/><textarea id="message" name="message" class="contact-field contact-message"></textarea></p>
					<p id="allFieldsRequired" class="center-content contact-result">Please fill in all fields</p>						
					<p class="center-content">
						<input id="btnContact" type="submit" class="button contact-button" value="Submit"></input>
					</p>					
				</form>
			</div>
			<div class="right-block">
				<h2>Ninja JS developers / <br/>Supersocial networkers</h2>
				<p><span>{ <span class="piece-of-cake"> need.some : more.challenging.stuff</span> } ?</span></p>
				<p><span>Send us your résumé</span> to jobs [at] iullui.com</p>
				<img src="images/favicon_128.png" class="logo-image" />
			</div>
		</div>
	</div>
	<c:import url="footer.jsp"/>
</div>

</body>
 
</html>