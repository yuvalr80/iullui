<%@ page language="java" session="false" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="metadata" class="sitemap">
	<p><b><c:out value="${appDesc}"/>. <c:out value="${engageText}"/></b></p> 
 	<c:if test="${not empty parent}">
		<p>What will you recommend for people who like <c:out value="${parent}"/>?</p> 	
	</c:if>
	<c:if test="${not empty matches}">
		<p><c:out value="${matches.votesCount}"/> people recommended:  			 
			<c:forEach var="matchChild" items="${matches.children}">
				<span>&nbsp;</span>
				<span>
					<a href="/matches?parent=<c:out value="${matchChild.id}"/>"> 
						<c:out value="${matchChild.title}"/> (<c:out value="${matchChild.votesCount}"/>)
					</a>
					&nbsp;|&nbsp;
				</span>
			</c:forEach>
		</p>
	</c:if>
	<c:if test="${empty matches and not empty latestMatches}">
		<p><b>Latest recommendations...</b>
			<c:forEach var="match" items="${latestMatches}">
				<span>
					<a href="/matches?parent=<c:out value="${match.parent.id}"/>&child=<c:out value="${match.child.id}"/>">
						<c:out value="${match.parent.title}"/>&nbsp;&#45;&gt;&nbsp;<c:out value="${match.child.title}"/>
					</a>
					&nbsp;|&nbsp; 
				</span>
			</c:forEach>
		</p>
	</c:if>		
</div>