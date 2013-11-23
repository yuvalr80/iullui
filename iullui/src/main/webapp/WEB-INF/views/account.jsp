<%@ page language="java" session="false" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" xmlns:fb="http://ogp.me/ns/fb#">
<head>
	<meta charset="utf-8">
	<title>iullui - Account</title>
	<c:set var="appDesc" value="iullui is a social app for content matching"/>	
	<c:set var="engageText" value="Tell iullui what you like, see what other people recommend you and make your own recommendations."/>
	<meta name="description"    content="<c:out value="${appDesc}"/>. <c:out value="${engageText}"/>"/> 	
	<link media="all" rel="icon" type="image/png" href="images/favicon.png" />
	<link media="all" rel="stylesheet" type="text/css" href="css/messi.min.css" />
	<link media="all" rel="stylesheet" type="text/css" href="css/all.css" />
	<link media="all" rel="stylesheet" type="text/css" href="css/account.css" />
	<script type="text/javascript" src="js/jquery-1.8.2.min.js" ></script>
	<script type="text/javascript" src="js/knockout-2.1.0.js"></script>
	<script type="text/javascript" src="js/jquery.cookie.js"></script>
	<script type="text/javascript" src="js/jquery.scrollTo-1.4.2-min.js" ></script>
	<script type="text/javascript" src="js/messi.min.js" ></script>
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<link rel="stylesheet" type="text/css" href="css/ie.css" media="screen"/>
	<![endif]-->
</head>
<body id="account">

<script type="text/javascript" src="scripts/ga.js"></script>
<script type="text/javascript" src="scripts/common.js"></script>
<script type="text/javascript" src="scripts/account.js"></script> 

<div class="wrapper">
	<c:import url="header.jsp"/>
	<div id="mainBody" class="main">
		<section class="account-block">
			<div class="heading">
				<div class="holder">
					<h2><span>Account</span></h2>
				</div>
			</div>
			<div id="accountTabs" class="account-tabs">
				<ul id="menu" class="menu">
					<li><a href="#items"><span>My Items</span></a></li>
				</ul>
				<div id="items" class="content">
					<div class="loader-wrapper" data-bind="visible: processing">
						<div class="loader">
		           			<p>Loading your items...</p>
		           			<img src="images/ico-load.gif"/>
		           		</div>
		           	</div>
		           	<div class="permission-wrapper" data-bind="visible: requestPermission">
		           		<div class="permission-block">
				           	<img src="images/facebook_logo_small.png"/> 			           		
				           	<p>
				           		<span>To add and promote your own Facebook pages as iullui items, an access permission to your pages is required.</span>
				           		<a class="button" href="https://www.facebook.com/dialog/oauth?client_id=<c:out value="${appId}"/>&redirect_uri=<c:out value="${appBaseUrl}"/>/account&scope=manage_pages">Continue...</a>
				           	</p>
			           	</div>
		           	</div>
		           	<div class="item-list-wrapper" data-bind="visible: loaded">
		           		<div class="no-pages-found" data-bind="visible: items().length == 0">
			           		<p class="title">No Facebook pages were found in your account.</p>
			           		<p>After you create a <a href="http://www.facebook.com/pages/create.php" target="_blank">Facebook Page</a>, you can add it here as an item.</p>
		           		</div>
						<ul id="itemsList" class="item-list" data-bind="foreach: sortedItems, visible: items().length > 0">
							<li class="item-box" data-bind="click: $root.selectItem, attr: {id: 'item_' + id}">
								<div class="thumb-wrapper">
									<div class="thumb">
										<img data-bind="attr: {id: 'img_' + id, src: thumb}, event: {load: imageLoaded, error: refreshImage}, visible: displayImage"/>
									</div>
									<div class="rounded-frame"></div>
								</div>
								<div class="info-wrapper">
									<p class="title">
										<a data-bind="text: title, attr: {href: url}" target="_blank"></a>&nbsp;&nbsp;&nbsp;
										<span class="item-marker" data-bind="visible: ownerId != null">&nbsp;Item&nbsp;</span>
									</p>
									<p data-bind="text: desc"></p>
									<p class="media-section" data-bind="visible: ownerId != null">
										<span class="play-button">&nbsp;&#9658;&nbsp;</span> Add a YouTube video ID &nbsp;&nbsp;&nbsp;<span>i.e,&nbsp;&nbsp;www.youtube.com/watch?v=</span>
										<input data-bind="attr: {id: 'youtubeVideo_' + id}, value: mediaPureId" class="media-input" type="text"/>&nbsp;&nbsp;&nbsp;
										<a href="#" data-bind="attr: {id: 'updateVideoLink_' + id}, click: $root.updateVideo">Update</a>&nbsp;&nbsp;
										<span class="update-media-success" data-bind="attr: {id: 'updateVideoSuccess_' + id}"></span>
									</p>
									<p>
										<a href="#" data-bind="visible: ownerId == null, click: $root.updateItem">Add to My Items</a>
										<span data-bind="visible: ownerId != null">
											<a href="#" data-bind="click: $root.removeItem">Remove from My Items</a>
											&nbsp;|&nbsp;
											<a href="#" data-bind="visible: ownerId != null, click: $root.updateItem">Update item details</a>
										</span>
									</p>
								</div>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</section>
	</div>
	<c:import url="footer.jsp"/>
</div>

</body>
 
</html>