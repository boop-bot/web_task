<%@ taglib prefix="ctg" uri="customtags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
  Created by IntelliJ IDEA.
  User: egork
  Date: 2/17/2021
  Time: 2:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<c:set var="page" value="/pages/common/all_movies.jsp" scope="session"/>
<fmt:setLocale value="${sessionScope.lang}" scope="session" />
<fmt:setBundle basename="property/pagecontent"/>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css" />
    <title><fmt:message key="label.all.movies"/></title>
</head>

<c:import url="/pages/module/header.jsp"/>
<c:import url="/pages/module/messages.jsp"/>
<body class="home">
<section class="section main">
    <section class="section-movies">
        <ctg:all_movies/>
    </section>
</section>
</body>
</html>
<c:remove var="errorMessage"/>
<c:remove var="validationErrors"/>
<c:remove var="confirmMessage"/>
