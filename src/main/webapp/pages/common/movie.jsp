<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ctg" uri="customtags" %>
<%--
  Created by IntelliJ IDEA.
  User: egork
  Date: 3/9/2021
  Time: 11:26 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<fmt:setLocale value="${sessionScope.lang}" scope="session" />
<fmt:setBundle basename="property/pagecontent"/>
<c:set var="page" value="/pages/common/movie.jsp" scope="session"/>
<html>
<jsp:useBean id="movie" scope="session" class="com.epam.project.model.entity.Movie"/>
<head>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/moviePage.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <title>${movie.title}</title>
</head>
<c:import url="/pages/module/header.jsp"/>
<body class="home">

<jsp:useBean id="user" class="com.epam.project.model.entity.User" scope="session"/>
<jsp:useBean id="reviewToUpdate" class="com.epam.project.model.entity.MovieReview" scope="session"/>
<section class="section main">

    <div class="section-title">
        <h2>${movie.title}
            <c:set var="admin" value="ADMIN"/>
            <c:if test="${user.role == admin}">
            <form action="<c:url value="/controller"/>" method="POST" >
                <input type="hidden" name="command" value="open_edit_movie_page">
                <input type="hidden" name="movieId" value="${movie.id}">
                <div class="btn">
                    <button class="edit-by-admin-btn"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
                </div>
            </form>
            <form action="<c:url value="/controller"/>" method="POST" >
                <input type="hidden" name="command" value="delete_movie">
                <input type="hidden" name="movieId" value="${movie.id}">
                <div class="btn">
                    <button class="delete-btn"><i class="fa fa-trash-o" aria-hidden="true"></i></a>
                    </button>
                </div>
            </form>
            </c:if>
        </h2>
    </div>


    <section class="section-movies">
        <div class="movie">
            <div class="poster">
                <a href="#">
                    <img src="${pageContext.request.contextPath}/picture?currentPicture=${movie.picture}"
                         alt="${movie.title}"/>
                </a>
            </div>

            <c:if test="${not empty movie.description}">
                <p class="description">
                        ${movie.description}
                </p>
            </c:if>

            <c:if test="${not empty movie.releaseDate}">
                <p><strong><fmt:message key="movie.release.date"/>: </strong>${movie.releaseDate}</p>
            </c:if>

            <c:if test="${not empty movie.genres}">
                <p><strong><fmt:message key="movie.genres"/>: </strong>
                    <c:forEach var="genre" items="${movie.genres}">
                        ${genre},
                    </c:forEach>
                </p>
            </c:if>

            <c:if test="${not empty movie.averageRating}">
                <p><strong><fmt:message key="movie.rating"/>: </strong>${movie.averageRating}
                </p>
            </c:if>

            <c:if test="${not empty movie.crew}">
                <p><strong><fmt:message key="movie.crew"/>: </strong></p>
                <c:forEach var="mediaPerson" items="${movie.crew}">
                    <div class="crew">
                        <p>
                        <form action="<c:url value="/controller"/>" method="POST" >
                            <input type="hidden" name="command" value="open_media_person_page">
                            <input type="hidden" name="mediaPersonId" value="${mediaPerson.id}">
                            <button class="link">
                                <c:out value="${mediaPerson.occupationType}"/>
                                <c:out value="${mediaPerson.firstName}"/>
                                <c:out value="${mediaPerson.secondName}"/>
                            </button>
                            <br>
                        </form>
                        </p>
                    </div>
                </c:forEach>
            </c:if>

            <c:import url="/pages/module/messages.jsp"/>

            <c:set var="active" value="ACTIVE"/>
            <c:set var="userRate" value="${ctg:getUserRate(movie.ratingList, user.id)}"/>
            <c:if test="${not empty userRate}">
                <p>Your rate: ${userRate.value}</p>
                <form action="<c:url value="/controller"/>" method="POST" class="delete-review-form">
                    <input type="hidden" name="command" value="delete_movie_rating"/>
                    <input type="hidden" name="movieRatingId" value="${userRate.id}"/>
                    <input type="hidden" name="movieId" value="${movie.id}"/>
                    <input type="hidden" name="userId" value="${user.id}"/>
                    <div class="btn">
                        <button class="delete-btn"><i class="fa fa-trash-o" aria-hidden="true"></i>
                        </button>
                    </div>
                </form>
            </c:if>
            <c:if test="${user.id != 0 and user.state == active}">
                <form action="<c:url value="/controller"/>" method="POST" class="movieRatingForm">
                    <fieldset class="rating">
                        <input type="radio" id="star10" name="movieRatingValue" value="10" onclick="submit()" <c:if test="${userRate.value == 10}">checked="checked"</c:if>/>
                        <label class="full" for="star10"
                               title="Awesome - 10 stars">10</label>

                        <input type="radio" id="star9" name="movieRatingValue" value="9" onclick="submit()" <c:if test="${userRate.value == 9}">checked="checked"</c:if>/>
                        <label class="full" for="star9"
                               title="Really good - 9 stars">9</label>

                        <input type="radio" id="star8" name="movieRatingValue" value="8" onclick="submit()" <c:if test="${userRate.value == 8}">checked="checked"</c:if>/>
                        <label class="full" for="star8"
                               title="Pretty good - 8 stars">8</label>

                        <input type="radio" id="star7" name="movieRatingValue" value="7" onclick="submit()" <c:if test="${userRate.value == 7}">checked="checked"</c:if>/>
                        <label class="full" for="star7"
                               title="I'd watch it again with a beer - 7 stars">7</label>

                        <input type="radio" id="star6" name="movieRatingValue" value="6" onclick="submit()" <c:if test="${userRate.value == 6}">checked="checked"</c:if>/>
                        <label class="full" for="star6"
                               title="Not so bad - 6 stars">6</label>

                        <input type="radio" id="star5" name="movieRatingValue" value="5" onclick="submit()" <c:if test="${userRate.value == 5}">checked="checked"</c:if>/>
                        <label class="full" for="star5"
                               title="Kinda bad - 5 stars">5</label>

                        <input type="radio" id="star4" name="movieRatingValue" value="4" onclick="submit()" <c:if test="${userRate.value == 4}">checked="checked"</c:if>/>
                        <label class="full" for="star4"
                               title="Bad - 4 stars">4</label>

                        <input type="radio" id="star3" name="movieRatingValue" value="3" onclick="submit()" <c:if test="${userRate.value == 3}">checked="checked"</c:if>/>
                        <label class="full" for="star3"
                               title="Really bad - 3 stars">3</label>

                        <input type="radio" id="star2" name="movieRatingValue" value="2" onclick="submit()" <c:if test="${userRate.value == 2}">checked="checked"</c:if>/>
                        <label class="full" for="star2"
                               title="Lame - 2 stars">2</label>

                        <input type="radio" id="star1" name="movieRatingValue" value="1" onclick="submit()" <c:if test="${userRate.value == 1}">checked="checked"</c:if>/>
                        <label class="full" for="star1"
                               title="Awful - 1 star">1</label>
                    </fieldset>

                    <c:choose>
                        <c:when test="${empty userRate}">
                            <input type="hidden" name="command" value="create_movie_rating"/>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="command" value="update_movie_rating"/>
                        </c:otherwise>
                    </c:choose>

                    <input type="hidden" name="movieRatingId" value="${userRate.id}"/>
                    <input type="hidden" name="movieId" value="${movie.id}"/>
                    <input type="hidden" name="userId" id="userId" value="${user.id}"/>
                </form>
            </c:if>
            <br/>
            <br/>

            <c:set var="addedReview" value="false"/>


            <c:if test="${not empty movie.reviews}">
                <p><strong><fmt:message key="label.reviews"/> : </strong></p>
                <c:forEach var="review" items="${movie.reviews}">
                    <c:if test="${reviewToUpdate.id != review.id and sessionScope.movieReviewId != review.id}">
                        <div class="review">
                            <form action="<c:url value="/controller"/>" method="POST" >
                                <input type="hidden" name="command" value="open_user_page"/>
                                <input type="hidden" name="userId" value="${review.userId}">
                                <h4><button class="link"><c:out
                                        value="${review.userLogin}"/></button></h4>
                            </form>

                            <div class="btn-row">
                                <c:if test="${(user.id == review.userId or user.role == admin)}">
                                    <form action="<c:url value="/controller"/>" method="POST" >
                                        <input type="hidden" name="command" value="prepare_movie_review_update"/>
                                        <input type="hidden" name="movieReviewId" value="${review.id}"/>
                                        <input type="hidden" name="movieId" value="${movie.id}"/>
                                        <input type="hidden" name="userId" value="${review.userId}"/>
                                        <div class="btn">
                                            <button class="edit-btn" id="${review.id}">
                                                <i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
                                        </div>
                                    </form>

                                    <form action="<c:url value="/controller"/>" method="POST" class="delete-review-form">
                                        <input type="hidden" name="command" value="delete_movie_review"/>
                                        <input type="hidden" name="movieReviewId" value="${review.id}"/>
                                        <input type="hidden" name="movieId" value="${movie.id}"/>
                                        <input type="hidden" name="userId" value="${review.userId}"/>
                                        <div class="btn">
                                            <button class="delete-btn"><i class="fa fa-trash-o" aria-hidden="true"></i>
                                            </button>
                                        </div>
                                    </form>

                                    <c:set var="addedReview" value="true"/>
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


            <c:if test="${user.id != 0 and user.state == active and (not addedReview or reviewToUpdate.id != 0 or not empty sessionScope.movieReviewTitle or not empty sessionScope.movieReviewBody)}">
                <form action="<c:url value="/controller"/>" method="POST">
                    <c:choose>
                        <c:when test="${reviewToUpdate.id == 0 and empty sessionScope.movieReviewId}">
                            <input type="hidden" name="command" value="create_movie_review"/>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="movieReviewId"
                                    <c:choose>
                                        <c:when test="${not empty sessionScope.movieReviewId}">
                                            value="${sessionScope.movieReviewId}"
                                        </c:when>
                                        <c:when test="${not empty reviewToUpdate.id}">
                                            value="${reviewToUpdate.id}"
                                        </c:when>
                                    </c:choose>
                            />
                            <input type="hidden" name="command" value="update_movie_review"/>
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="userId"
                            <c:choose>
                                <c:when test="${not empty sessionScope.userId}">
                                    value="${sessionScope.userId}"
                                </c:when>
                                <c:when test="${not empty reviewToUpdate.userId}">
                                    value="${reviewToUpdate.userId}"
                                </c:when>
                            </c:choose>
                    />
                    <input type="hidden" name="movieId" value="${movie.id}"/>
                    <input type="text" required name="movieReviewTitle" class="review-title-input" placeholder="<fmt:message key="review.title"/>"
                            <c:choose>
                                <c:when test="${not empty sessionScope.movieReviewTitle}">
                                    value="${sessionScope.movieReviewTitle}"
                                </c:when>
                                <c:when test="${not empty reviewToUpdate.title}">
                                    value="${reviewToUpdate.title}"
                                </c:when>
                            </c:choose>
                    />
                    <textarea required cols="60" rows="5" name="movieReviewBody" class="review-body-input"
                              placeholder="<fmt:message key="review.body"/> "><c:choose><c:when test="${not empty sessionScope.movieReviewBody}">${sessionScope.movieReviewBody}</c:when><c:when test="${not empty reviewToUpdate.body}">${reviewToUpdate.body}</c:when></c:choose></textarea>
                    <input type="submit" class="leave-review-btn" value="<fmt:message key="label.leave.review"/> ">
                </form>
            </c:if>
            <c:remove var="reviewToUpdate"/>
        </div>
    </section>
</section>
</body>
</html>
<c:remove var="validationErrors"/>
<c:remove var="errorMessage"/>
<c:remove var="movieReviewTitle"/>
<c:remove var="movieReviewBody"/>
<c:remove var="movieReviewId"/>
<c:remove var="movieId"/>
<c:remove var="userIf"/>
<c:remove var="confirmMessage"/>