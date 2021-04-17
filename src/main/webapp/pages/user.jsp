<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
  Created by IntelliJ IDEA.
  User: egork
  Date: 4/15/2021
  Time: 4:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:useBean id="user" class="com.example.demo_web.model.entity.User" scope="session"/>
<jsp:useBean id="someUser" scope="session" class="com.example.demo_web.model.entity.User"/>
<fmt:setLocale value="${sessionScope.lang}" scope="session" />
<fmt:setBundle basename="pagecontent" var="rb" />
<c:set var="page" value="/pages/user.jsp" scope="session"/>
<head>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/moviePage.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <title>Title</title>
</head>
<body class="home">
<jsp:include page="/pages/module/header.jsp"/>
<jsp:useBean id="reviewToUpdate" class="com.example.demo_web.model.entity.MovieReview" scope="session"/>
<section class="section main">
    <div class="section-title">
        <h2>${someUser.login}</h2>
        <c:set var="admin" value="ADMIN"/>
        <c:if test="${user.id == someUser.id or user.role == admin}">
            <form action="<c:url value="/controller"/>" method="POST" >
                <input type="hidden" name="command" value="open_edit_user_page"/>
                <input type="hidden" name="userId" value="${someUser.id}">
                <div class="btn">
                    <button class="edit-btn" id="${user.id}">
                        <i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
                </div>
            </form>
        </c:if>
    </div>
    <section class="section-movies">
        <div class="movie">

            <div class="poster">
                <a href="#">
                    <img src="${someUser.picture}"
                         alt="${someUser.login}"/>
                </a>
            </div>

            <c:if test="${not empty someUser.rating}">
                <p><strong><fmt:message key="user.rating"/>: </strong>${someUser.rating}</p>
            </c:if>

            <c:if test="${not empty someUser.firstName}">
                <p><strong><fmt:message key="first.name"/>: </strong>${someUser.firstName}</p>
            </c:if>

            <c:if test="${not empty someUser.secondName}">
                <p><strong><fmt:message key="last.name"/>: </strong>${someUser.secondName}</p>
            </c:if>

            <c:if test="${not empty someUser.email}">
                <p><strong><fmt:message key="user.email"/>: </strong>${someUser.email}</p>
            </c:if>

            <c:if test="${not empty someUser.role}">
                <p><strong><fmt:message key="user.role"/>: </strong>${someUser.role}</p>
            </c:if>

            <c:if test="${not empty someUser.state}">
                <p><strong><fmt:message key="user.state"/>: </strong>${someUser.state}</p>
            </c:if>

            <c:forEach var="validationException" items="${requestScope.validationExceptions}">
                <h4>${validationException}</h4>
            </c:forEach>

            <c:if test="${not empty someUser.movieReviews}">
                <p><strong><fmt:message key="reviews"/> : </strong></p>
                <c:forEach var="review" items="${someUser.movieReviews}">
                    <c:if test="${reviewToUpdate.id != review.id}">
                    <div class="review" style="background: #d0cecd">
                        <form action="<c:url value="/controller"/>" method="POST">
                            <input type="hidden" name="command" value="open_movie_page"/>
                            <input type="hidden" name="movieId" value="${review.movieId}"/>
                            <h4><button class="link"><c:out value="${review.movieTitle}"/></button></h4>
                        </form>

                        <div class="btn-row">
                            <c:if test="${sessionScope.user.id == someUser.id}">
                                <form action="<c:url value="/controller"/>" method="POST" >
                                    <input type="hidden" name="command" value="prepare_movie_review_update"/>
                                    <input type="hidden" name="movieReviewId" value="${review.id}"/>
                                    <input type="hidden" name="userId" value="${someUser.id}">
                                    <div class="btn">
                                        <button class="edit-btn" id="${review.id}">
                                            <i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
                                    </div>
                                </form>

                                <form action="<c:url value="/controller"/>" method="POST" class="delete-review-form">
                                    <input type="hidden" name="command" value="delete_movie_review"/>
                                    <input type="hidden" name="userId" value="${someUser.id}">
                                    <input type="hidden" name="movieReviewId" value="${review.id}"/>
                                    <button class="delete-btn"><i class="fa fa-trash-o" aria-hidden="true"></i></button>
                                </form>
                            </c:if>
                        </div>

                        <h3 id="title-${review.id}" class="review-title"><c:out value="${review.title}"/></h3>
                        <p id="body-${review.id}" class="review-body"><c:out value="${review.body}"/></p>
                        <p>
                            <c:out value="${review.creationDate}"/>
                            <br>
                            <br>
                        </p>
                    </div>
                </c:if>
                </c:forEach>
            </c:if>

            <c:if test="${not empty someUser.movieRatings}">
            <p><strong><fmt:message key="ratings"/>: </strong></p>
            <c:forEach var="rating" items="${someUser.movieRatings}">
                <c:if test="${sessionScope.user.id == someUser.id}">
                    <form action="<c:url value="/controller"/>" method="POST" class="delete-rating-form">
                        <input type="hidden" name="command" value="delete_movie_rating"/>
                        <input type="hidden" name="movieRatingId" value="${rating.id}"/>
                        <input type="hidden" name="userId" value="${someUser.id}">
                        <div class="btn">
                            <button class="delete-rating-btn"><i class="fa fa-trash-o" aria-hidden="true"></i></button>
                        </div>
                    </form>
                </c:if>
                <p>
                <form action="<c:url value="/controller"/>" method="POST">
                    <input type="hidden" name="command" value="open_movie_page"/>
                    <input type="hidden" name="movieId" value="${rating.movieId}"/>
                    <button class="link">${rating.movieTitle}</button>
                    : ${rating.value}
                    <br>
                </form>
                </p>

            </c:forEach>
        </div>
        </c:if>

        <c:set var="active" value="ACTIVE"/>
        <c:if test="${user.state == active and reviewToUpdate.id != 0}">
            <form action="<c:url value="/controller"/>" method="POST">
                <input type="hidden" name="movieReviewId" value="${reviewToUpdate.id}"/>
                <input type="hidden" name="command" value="update_movie_review"/>
                <input type="hidden" name="userId" value="${user.id}"/>
                <input type="hidden" name="movieId" value="${movie.id}"/>
                <input type="text" required name="movieReviewTitle" class="review-title-input" value="${reviewToUpdate.title}"
                       placeholder="<fmt:message key="review.title"/>"/>
                <textarea required cols="60" rows="5" name="movieReviewBody" class="review-body-input"
                          placeholder="<fmt:message key="review.body"/> ">${reviewToUpdate.body}</textarea>
                <input type="submit" class="leave-review-btn" value="<fmt:message key="leave.review"/> ">
            </form>
        </c:if>
        <c:remove var="reviewToUpdate"/>
        <a href="${requestScope.previous_page}"><fmt:message key="back"/></a>
    </section>
</section>
</body>
</html>
