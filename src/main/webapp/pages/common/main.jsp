<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
  Created by IntelliJ IDEA.
  User: egork
  Date: 12/30/2020
  Time: 3:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<fmt:setLocale value="${sessionScope.lang}" scope="session" />
<fmt:setBundle basename="property/pagecontent"/>
<c:set var="page" value="/pages/common/main.jsp" scope="session"/>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <title><fmt:message key="label.found.movies"/></title>
</head>
<c:import url="/pages/module/header.jsp"/>
<body class="home">
<section class="section main">
    <div class="section-title">
        <h2>
            <c:choose>
                <c:when test="${not empty sessionScope.queryName}">
                     ${sessionScope.queryName}:
                </c:when>
                <c:otherwise>
                    MovieRating
                </c:otherwise>
            </c:choose>
        </h2>
    </div>
    <c:import url="/pages/module/messages.jsp"/>
    <c:if test="${not empty sessionScope.foundMovies}">
        <section class="section-movies">
            <c:forEach var="movie" items="${sessionScope.foundMovies}">
                <ul>
                    <li>
                        <div class="movie">
                            <a href="${pageContext.request.contextPath}/controller?command=open_movie_page&movieId=${movie.id}">
                                <h4 class="title"><c:out value="${movie.title}"/></h4>
                            </a>

                            <div class="poster">
                                <a href="${pageContext.request.contextPath}/controller?command=open_movie_page&movieId=${movie.id}">
                                    <img src="${pageContext.request.contextPath}/picture?currentPicture=${movie.picture}"
                                         alt="${movie.title}"/>
                                </a>
                            </div>

                            <p class="description"><c:out value="${movie.averageRating}"/></p>
                        </div>
                    </li>
                </ul>
            </c:forEach>
        </section>
    </c:if>
</section>
</body>
</html>
<c:remove var="foundMovies"/>
<c:remove var="validationErrors"/>
<c:remove var="errorMessage"/>
<c:remove var="queryName"/>
<c:remove var="confirmMessage"/>