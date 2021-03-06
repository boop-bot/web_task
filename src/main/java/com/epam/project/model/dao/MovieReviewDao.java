package com.epam.project.model.dao;

import com.epam.project.exception.DaoException;
import com.epam.project.model.dao.column.MovieReviewsColumn;
import com.epam.project.model.entity.MovieReview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intellij.lang.annotations.Language;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Movie review dao.
 */
public class MovieReviewDao extends AbstractMovieReviewDao {
    private static final Logger logger = LogManager.getLogger(MovieReview.class);

    @Language("SQL")
    private static final String SQL_SELECT_MOVIE_REVIEWS_BY_MOVIE_ID = "SELECT MR.review_id, MR.review_title, MR.review_body, MR.review_creation_date, MR.movie_id, MR.user_id, M.movie_title, U.user_login FROM movie_reviews MR INNER JOIN users U on MR.user_id = u.user_id INNER JOIN movies M on MR.movie_id = M.movie_id WHERE M.movie_id = ?;";

    @Language("SQL")
    private static final String SQL_SELECT_MOVIE_REVIEWS_BY_USER_ID = "SELECT MR.review_id, MR.review_title, MR.review_body, MR.review_creation_date, MR.movie_id, MR.user_id, M.movie_title, U.user_login FROM movie_reviews MR INNER JOIN users U on MR.user_id = u.user_id INNER JOIN movies M on MR.movie_id = M.movie_id WHERE U.user_id = ?;";

    @Language("SQL")
    private static final String SQL_INSERT_MOVIE_REVIEW = "INSERT INTO movie_reviews (review_title, review_body, review_creation_date, movie_id, user_id) VALUES (?, ?, ?, ?, ?);";

    @Language("SQL")
    private static final String SQL_UPDATE_MOVIE_REVIEW = "UPDATE movie_reviews MR SET MR.review_title = ?, MR.review_body = ?, MR.review_creation_date = ? WHERE MR.review_id = ?;";

    @Language("SQL")
    private static final String SQL_DELETE_MOVIE_REVIEW = "DELETE FROM movie_reviews MR WHERE MR.review_id = ?;";

    @Language("SQL")
    private static final String SQL_SELECT_MOVIE_REVIEW_BY_ID = "SELECT MR.review_id, MR.review_title, MR.review_body, MR.review_creation_date, MR.movie_id, MR.user_id, M.movie_title, U.user_login FROM movie_reviews MR INNER JOIN users U on MR.user_id = u.user_id INNER JOIN movies M on MR.movie_id = M.movie_id WHERE MR.review_id = ?;";

    @Language("SQL")
    private static final String SQL_MOVIE_REVIEW_IS_UNIQUE = "SELECT EXISTS (SELECT review_id FROM movie_reviews WHERE movie_id = ? AND user_id = ?) AS movie_review_existence;";

    @Language("SQL")
    private static final String SQL_EXISTS = "SELECT EXISTS (SELECT review_id FROM movie_reviews WHERE review_id = ? AND movie_id = ? AND user_id = ?) AS movie_review_existence;";

    private static final AbstractMovieReviewDao instance = new MovieReviewDao();

    private MovieReviewDao(){}

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static AbstractMovieReviewDao getInstance() {
        return instance;
    }

    @Override
    public MovieReview findEntityById(Integer id) throws DaoException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MOVIE_REVIEW_BY_ID)) {
            MovieReview movieReview = new MovieReview();
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    movieReview = buildMovieReview(resultSet);
                }
            }
            return movieReview;
        } catch (SQLException e) {
            logger.error(e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DaoException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_MOVIE_REVIEW)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error(e);
            throw new DaoException(e);
        }
    }

    @Override
    public MovieReview create(MovieReview movieReview) throws DaoException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_MOVIE_REVIEW, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, movieReview.getTitle());
            preparedStatement.setString(2, movieReview.getBody());
            preparedStatement.setDate(3, java.sql.Date.valueOf(movieReview.getCreationDate()));
            preparedStatement.setInt(4, movieReview.getMovieId());
            preparedStatement.setInt(5, movieReview.getUserId());
            int id = executeUpdateAndGetGeneratedId(preparedStatement);
            movieReview.setId(id);
            return movieReview;
        } catch (SQLException e) {
            logger.error(e);
            throw new DaoException(e);
        }
    }

    @Override
    public MovieReview update(MovieReview movieReview) throws DaoException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_MOVIE_REVIEW)) {
            preparedStatement.setString(1, movieReview.getTitle());
            preparedStatement.setString(2, movieReview.getBody());
            preparedStatement.setDate(3, java.sql.Date.valueOf(movieReview.getCreationDate()));
            preparedStatement.setInt(4, movieReview.getId());
            preparedStatement.executeUpdate();
            return movieReview;
        } catch (SQLException e) {
            logger.error(e);
            throw new DaoException(e);
        }
    }

    @Override
    public List<MovieReview> findByMovieId(Integer id) throws DaoException {
        List<MovieReview> movieReviews = new ArrayList<>();
        MovieReview movieReview;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MOVIE_REVIEWS_BY_MOVIE_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    movieReview = buildMovieReview(resultSet);
                    movieReviews.add(movieReview);
                }
            }
            return movieReviews;
        } catch (SQLException e) {
            logger.error(e);
            throw new DaoException(e);
        }
    }

    @Override
    public List<MovieReview> findByUserId(Integer id) throws DaoException {
        List<MovieReview> movieReviews = new ArrayList<>();
        MovieReview movieReview;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MOVIE_REVIEWS_BY_USER_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    movieReview = buildMovieReview(resultSet);
                    movieReviews.add(movieReview);
                }
            }
            return movieReviews;
        } catch (SQLException e) {
            logger.error(e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean isUnique(int movieId, int userId) throws DaoException {
        boolean result;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_MOVIE_REVIEW_IS_UNIQUE)) {
            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                result = resultSet.getInt(1) == 0;
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new DaoException(e);
        }
        return result;
    }

    @Override
    public boolean exists(int reviewId, int movieId, int userId) throws DaoException {
        boolean result;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_EXISTS)) {
            preparedStatement.setInt(1, reviewId);
            preparedStatement.setInt(2, movieId);
            preparedStatement.setInt(3, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                result = resultSet.getInt(1) == 1;
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new DaoException(e);
        }
        return result;
    }

    @Override
    public List<MovieReview> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    /**
     * Build movie review movie review.
     *
     * @param resultSet the result set
     * @return the movie review
     * @throws SQLException the sql exception
     */
    public MovieReview buildMovieReview(ResultSet resultSet) throws SQLException {
        if (resultSet.wasNull()) {
            return null;
        }
        MovieReview movieReview = new MovieReview();
        Integer movieReviewId = resultSet.getInt(MovieReviewsColumn.ID);
        movieReview.setId(movieReviewId);
        String movieReviewTitle = resultSet.getString(MovieReviewsColumn.TITLE);
        movieReview.setTitle(movieReviewTitle);
        String movieReviewBody = resultSet.getString(MovieReviewsColumn.BODY);
        movieReview.setBody(movieReviewBody);
        LocalDate movieReviewCreationDate = resultSet.getDate(MovieReviewsColumn.CREATION_DATE).toLocalDate();
        movieReview.setCreationDate(movieReviewCreationDate);
        Integer movieId = resultSet.getInt(MovieReviewsColumn.MOVIE_ID);
        movieReview.setMovieId(movieId);
        Integer userId = resultSet.getInt(MovieReviewsColumn.USER_ID);
        movieReview.setUserId(userId);
        String movieTitle = resultSet.getString(MovieReviewsColumn.MOVIE_TITLE);
        movieReview.setMovieTitle(movieTitle);
        String userLogin = resultSet.getString(MovieReviewsColumn.USER_LOGIN);
        movieReview.setUserLogin(userLogin);
        return movieReview;
    }
}
