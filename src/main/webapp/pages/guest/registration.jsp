<%--
  Created by IntelliJ IDEA.
  User: egork
  Date: 12/30/2020
  Time: 8:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="page" value="/pages/guest/registration.jsp" scope="session"/>
<fmt:setLocale value="${sessionScope.lang}" scope="session" />
<fmt:setBundle basename="property/pagecontent"/>

<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/core.css">
    <title><fmt:message key="label.registration"/></title>
</head>
<body>
<c:import url="/pages/module/header.jsp"/>
<c:import url="/pages/module/messages.jsp"/>
    <div class="user">
        <form class="form" name="registerForm" method="POST" action="<c:url value="/controller"/>">
            <input type="hidden" name="command" value="register"/>
            <div class="form__group">
                <input type="text" class="form__input" name="login" placeholder=<fmt:message key="user.login"/> pattern="[\wА-Яа-яЁё0-9_.]{1,40}"
                       value="${sessionScope.login}"
                />
            </div>
            <div class="form__group">
                <input type="email" class="form__input" name="email" value="${sessionScope.email}" placeholder=<fmt:message key="user.email"/> pattern="(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])"/>
            </div>
            <div class="form__group">
                <input type="text" class="form__input" name="firstName" value="${sessionScope.firstName}" placeholder="<fmt:message key="user.name.first"/>" pattern="[A-Za-zА-Яа-яЁё]{1,40}"/>
            </div>
            <div class="form__group">
                <input type="text" class="form__input" name="secondName" value="${sessionScope.secondName}" placeholder="<fmt:message key="user.name.second"/>" pattern="[A-Za-zА-Яа-яЁё]{1,40}"/>
            </div>
            <div class="form__group">
                <input type="password" class="form__input" name="password" placeholder=<fmt:message key="user.password"/> pattern="\w{8,100}" title="Eight or more characters"/>
            </div>
            <div class="form__group">
                <input type="password" class="form__input" name="passwordRepeat" placeholder=<fmt:message key="user.password"/> pattern="\w{8,}" title="Eight or more characters"/>
            </div>
            <input type="submit" class="btn" value=<fmt:message key="label.register"/>>
        </form>
        <br/>

    </div>
</body>
</html>
<c:remove var="firstName"/>
<c:remove var="secondName"/>
<c:remove var="email"/>
<c:remove var="login"/>
<c:remove var="validationErrors"/>
<c:remove var="errorMessage"/>
<c:remove var="confirmMessage"/>