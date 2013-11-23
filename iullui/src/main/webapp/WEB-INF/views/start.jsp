<%@ page language="java" session="false" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<meta charset="utf-8">
	<title>Artists and Content makers - Get exposed now with iullui!</title>
	<c:set var="appDesc" value="iullui is a social app for content matching"/>	
	<c:set var="engageText" value="Tell iullui what you like, see what other people recommend you and make your own recommendations."/>
	<meta name="description"    content="<c:out value="${appDesc}"/>. <c:out value="${engageText}"/>"/> 	
 	<meta name="keywords" 		content="iullui, content, matching, recommend, recommended, recommendation, recommendations, interest, if you like, you might also like, you might be interested in"/>
	<meta property="fb:app_id"      content="<c:out value="${appId}"/>" /> 
 	<meta property="og:url"         content="<c:out value="${appBaseUrl}"/>/start" /> 
	<meta property="og:title"       content="Artists and Content makers - Get exposed now with iullui!" />
	<meta property="og:image"       content="<c:out value="${appBaseUrl}/images/iullui_logo_icon.png"/>" />
	<meta property="og:image"       content="<c:out value="${appBaseUrl}/images/artist_flow.png"/>" />
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
<body id="Start">

<script type="text/javascript" src="scripts/ga.js"></script>
<script type="text/javascript">
	$(function() {
		$("#btnGetStarted").click(function(e) {
			e.preventDefault();
			$.cookie("content-maker-guide", true);
			$.cookie("redirectUri", "/account");
			$("#getStartedForm").submit();
		});
	});
</script>

<div class="wrapper">
	<c:import url="header.jsp"/>
	<div id="mainBody" class="main">
		<div class="row-block">
			<div class="left-block">
				<h2>Artists and Content makers, Welcome to iullui!</h2>
				<p><span>iullui</span> helps you to get exposed to <span>your</span> target audience!</p>
				<p style="text-align:center;"><iframe width="284" height="160" src="http://www.youtube.com/embed/bOcJJEaiSTI?hd=1" frameborder="0" allowfullscreen></iframe></p>				
			</div>
			<div class="right-block">
				<h2>What's the idea?</h2>
				<p>The basic idea is that rather than tagging yourself in genres or categories, you <span>match your content</span> with other artists or content makers that your potential audience likes. That way, you can get much better <span>targeted exposure</span>!</p>
				<p>So when you match yourself with other contents, content makers or themes, you are <span>exposing yourself</span> to anyone who likes them. If you targeted yourself well, there are good chances that they will also to like you.</p>
				<p><span>There is no better way to get exposed!</span></p>
			</div>
		</div>
		<div class="row-block">
			<p style="text-align:center;">Whether you are a musician, video artist, illustrator, blogger or any other content maker, in just a few quick steps, you can <span>expose yourself</span> to an audience that is very likely to enjoy your stuff. All you need is a public Facebook page.</p>
		</div>			
		<div class="row-block" style="text-align:center;">
			<h2>How does it work?</h2>
			<p>In <span>iullui</span>, the audience looks for new recommendations based on items they <span>already like</span>. A typical example would be:</p>
			<p><img src="images/artist_flow.png"/></p>
			<h3>So matching yourself is super easy:</h3>
			<div style="text-align:left; margin-left:250px">
				<ul>
					<li><img src="images/favicon_75.png" class="icon-bullet" /><span>&nbsp;Select anything</span> that your target audience likes.</li>
					<li><img src="images/favicon_75.png" class="icon-bullet" /><span>&nbsp;Select your item.</span></li>
					<li><img src="images/favicon_75.png" class="icon-bullet" /><span>&nbsp;Match</span> between them.</li>
					<li><img src="images/favicon_75.png" class="icon-bullet" /><span>&nbsp;Share</span> your match and ask your friends to recommend it.</li>
				</ul>
			</div>
		</div>
		<div class="row-block" style="text-align:center">
			<form id="getStartedForm" action="/signin/facebook" method="POST">
				<input type="hidden" name="scope" value="publish_actions,manage_pages"/>
				<input type="hidden" name="redirect_uri" value="/account"/>
				<input id="btnGetStarted" type="submit" class="button get-started-button" title="Click here to get started"  value="Get Started!"/>
			</form>
		</div>
	</div>
	<br/><br/>
	<c:import url="footer.jsp"/>
</div>

</body>
 
</html>