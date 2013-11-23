<%@ page language="java" session="false" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<header class="header">
	<strong class="logo"></strong>
	<div class="header-box">
		<nav class="navigation">
			<ul class="nav">
				<li><a id="btnHome" class="menu-item" href="/"><span>Home</span></a></li>
				<c:if test="${not empty user}">
					<li><a id="btnAccount" class="menu-item" href="/account"><span>Account</span></a></li>
					<li><a id="btnSignOut" class="menu-item" href="<c:url value="/signout"/>">Sign out</a></li>
				</c:if>
				<c:if test="${empty user}">
					<li><a id="btnArtist" class="menu-item" href="/start"><span>Artist?</span></a></li>
				</c:if>
			</ul>
		</nav>
		<div class="facebook-holder">
			<div class="user-block">
				<span id="appId" style="display: none;"><c:out value="${appId}"/></span>				
				<c:if test="${empty user}">
					<form id="signinForm" action="/signin/facebook" method="POST">
						<input type="hidden" name="scope" value="publish_actions"/>
						<button id="signinBtn" type="submit" class="btn-facebook"></button>
					</form>
					<form id="signinToAccountForm" action="/signin/facebook" method="POST" style="display:none;">
						<input type="hidden" name="scope" value="publish_actions,manage_pages"/>
						<input type="hidden" name="redirect_uri" value="/account"/>
					</form>
				</c:if>
				<c:if test="${not empty user}">
					<img id="userImage" class="user-image" src="http://graph.facebook.com/<c:out value="${user.id}"/>/picture" />
					<span>&nbsp;Hello <c:out value="${user.firstName}"/>&nbsp;&nbsp;|&nbsp;</span>
				</c:if>
				<a href="http://facebook.iullui.com" target="_blank"><img class="social-img" src="images/icon_facebook_16.png"/></a>
				<a href="http://blog.iullui.com" target="_blank"><img class="social-img" src="images/icon_tumblr_16.png"/></a>
				<a href="http://twitter.iullui.com" target="_blank"><img class="social-img" src="images/icon_twitter_16.png"/></a>
			</div>
		</div>
	</div>
	<div class="ad-box"><a href="#"></a></div>
</header>
