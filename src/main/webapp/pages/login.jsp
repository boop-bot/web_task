<%--
  Created by IntelliJ IDEA.
  User: egork
  Date: 12/30/2020
  Time: 3:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<fmt:setLocale value="${sessionScope.lang}" scope="session" />
<fmt:setBundle basename="pagecontent" var="rb" />
<c:set var="page" value="/pages/login.jsp" scope="session"/>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/core.css">
    <link href='http://fonts.googleapis.com/css?family=Cookie' rel='stylesheet' type='text/css'>
    <title><fmt:message key="label.login" bundle="${ rb }" /></title>
</head>
<body>
<jsp:include page="/pages/module/header.jsp"/>
<div class="user">
    <form class="form" name="loginForm" method="POST" action="<c:url value="/controller"/>">
        <input type="hidden" name="command" value="login" />
        <div class="form__group">
            <input type="text" class="form__input" name="login" value="<c:if test="${requestScope.login != null}">${requestScope.login}</c:if>" placeholder=<fmt:message key="label.login" bundle="${ rb }" /> pattern="[A-Za-zА-Яа-яЁё0-9]{4,}" value=""/>
        </div>
        <div class="form__group">
            <input type="password" class="form__input" name="password" value="" placeholder=<fmt:message key="label.password" bundle="${ rb }" /> pattern=".{8,}" title="Eight or more characters"/>
        </div>
        <input type="submit" class="btn" value=<fmt:message key="label.submit" bundle="${ rb }" />>
    </form>
    <br/>
    ${errorMessage}
</div>
</body>
</html>
