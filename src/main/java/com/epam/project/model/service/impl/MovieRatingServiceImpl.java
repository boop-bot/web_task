package com.epam.project.model.service.impl;

import com.epam.project.exception.DaoException;
import com.epam.project.exception.ServiceException;
import com.epam.project.model.dao.AbstractMovieDao;
import com.epam.project.model.dao.AbstractMovieRatingDao;
import com.epam.project.model.dao.AbstractUserDao;
import com.epam.project.model.dao.EntityTransaction;
import com.epam.project.model.dao.impl.MovieDao;
import com.epam.project.model.dao.impl.MovieRatingDao;
import com.epam.project.model.dao.impl.UserDao;
import com.epam.project.model.entity.Movie;
import com.epam.project.model.entity.MovieRating;
import com.epam.project.model.entity.User;
import com.epam.project.model.service.MovieRatingService;
import com.epam.project.model.util.evaluator.UserRatingEvaluator;
import com.epam.project.model.util.message.ErrorMessage;
import com.epam.project.model.validator.MovieValidator;
import com.epam.project.model.validator.ValidationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MovieRatingServiceImpl implements MovieRatingService {
    private static final MovieRatingService instance = new MovieRatingServiceImpl();
    private AbstractMovieRatingDao movieRatingDao = MovieRatingDao.getInstance();
    private AbstractUserDao userDao = UserDao.getInstance();
    private AbstractMovieDao movieDao = MovieDao.getInstance();

    private MovieRatingServiceImpl() {};

    public static MovieRatingService getInstance() {
        return instance;
    }

    @Override
    public Optional<String> create(String stringMovieId, String stringUserId, String stringValue) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        Optional<String> errorMessage = Optional.empty();
        try {
            transaction.initTransaction(movieRatingDao, movieDao, userDao);
            MovieRating movieRating = convertToMovieRating(stringMovieId, stringUserId, stringValue);
            if (!movieDao.idExists(movieRating.getMovieId())) {
                errorMessage = Optional.of(ErrorMessage.MOVIE_ID_NOT_EXIST);
            } else if (!userDao.idExists(movieRating.getUserId())) {
                errorMessage = Optional.of(ErrorMessage.USER_ID_NOT_EXIST);
            } else {
                if (!movieRatingDao.isUnique(movieRating.getMovieId(), movieRating.getUserId())) {
                    errorMessage = Optional.of(ErrorMessage.MOVIE_RATING_IS_NOT_UNIQUE);
                } else {
                    movieRatingDao.create(movieRating);
                    updateUserRating(movieRating);
                    int movieId = movieRating.getMovieId();
                    int difference = movieRating.getValue();
                    updateMovieRating(movieId, difference);
                }
            }
            transaction.commit();
        } catch (DaoException e) {
            transaction.rollback();
            throw new ServiceException(e);
        } finally {
            transaction.endTransaction();
        }
        return errorMessage;
    }

    public Optional<String> update(String stringRatingId, String stringMovieId, String stringUserId, String stringValue) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        Optional<String> errorMessage = Optional.empty();
        if (!MovieValidator.isValidId(stringRatingId)) {
            errorMessage = Optional.of(ErrorMessage.INCORRECT_UPDATE_MOVIE_RATING_ID_PARAMETER);
        } else {
            int ratingId = Integer.valueOf(stringRatingId);
            try {
                transaction.initTransaction(movieRatingDao, movieDao, userDao);
                MovieRating movieRating = convertToMovieRating(stringMovieId, stringUserId, stringValue);
                movieRating.setId(ratingId);
                if (!movieDao.idExists(movieRating.getMovieId())) {
                    errorMessage = Optional.of(ErrorMessage.MOVIE_ID_NOT_EXIST);
                } else if (!userDao.idExists(movieRating.getUserId())) {
                    errorMessage = Optional.of(ErrorMessage.USER_ID_NOT_EXIST);
                } else {
                    if (!movieRatingDao.exists(ratingId, movieRating.getMovieId(), movieRating.getUserId() )) {
                        errorMessage = Optional.of(ErrorMessage.TRY_UPDATE_NOT_EXISTING_MOVIE_RATING);
                    } else {
                        MovieRating oldMovieRating = movieRatingDao.findEntityById(ratingId);
                        movieRatingDao.update(movieRating);
                        int movieId = movieRating.getMovieId();
                        float difference = movieRating.getValue() - oldMovieRating.getValue();
                        updateMovieRating(movieId, difference);
                    }
                }
                transaction.commit();
            } catch (DaoException e) {
                transaction.rollback();
                throw new ServiceException(e);
            } finally {
                transaction.endTransaction();
            }
        }
        return errorMessage;
    }

    @Override
    public Optional<String> delete(String stringRatingId, String stringMovieId, String stringUserId) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        Optional<String> errorMessage = Optional.empty();
        if (!MovieValidator.isValidId(stringRatingId)) {
            errorMessage = Optional.of(ErrorMessage.INCORRECT_DELETE_MOVIE_RATING_ID_PARAMETER);
        } else {
            try {
                transaction.init(movieRatingDao);
                int ratingId = Integer.valueOf(stringRatingId);
                int movieId = Integer.valueOf(stringMovieId);
                int userId = Integer.valueOf(stringUserId);
                if (movieRatingDao.exists(ratingId, movieId, userId)) {
                    MovieRating movieRating = movieRatingDao.findEntityById(ratingId);
                    movieRatingDao.delete(ratingId);
                    float difference = -movieRating.getValue();
                    updateMovieRating(movieId, difference);
                } else {
                    errorMessage = Optional.of(ErrorMessage.TRY_DELETE_NOT_EXISTING_MOVIE_RATING);
                }
            } catch (DaoException e) {
                throw new ServiceException(e);
            } finally {
                transaction.end();
            }
        }
        return errorMessage;
    }

    @Override
    public List<String> validateData(String stringMovieId, String stringUserId, String stringValue) {
        Map<String, Boolean> validationResult = MovieValidator.validateMovieRatingData(stringMovieId, stringUserId, stringValue);
        List<String> errorMessages = new ArrayList<>();
        validationResult.entrySet().stream().filter(entry -> Boolean.FALSE.equals(entry.getValue())).forEach(entry ->
                errorMessages.add(ValidationHelper.defineErrorValidationMessage(entry)));
        return errorMessages;
    }



    private void updateMovieRating(int movieId, float difference) throws DaoException {
        float averageRating = movieDao.findRatingById(movieId);
        int movieRatingsCount = movieRatingDao.countRatingsByMovieId(movieId);
        int ratingPoints = Math.round(averageRating * movieRatingsCount);
        ratingPoints += difference;
        if (movieRatingsCount != 0) {
            averageRating = ratingPoints / movieRatingsCount;
        } else {
            averageRating = 0;
        }
        movieDao.updateRatingById(averageRating, movieId);
    }

    private void updateUserRating(MovieRating movieRating) throws DaoException {
        User user = userDao.findEntityById(movieRating.getUserId());
        int newUserRating = UserRatingEvaluator.updateRating(user.getRating(), calculateMovieRatingDifference(movieRating));
        userDao.updateUserRating(user.getId(), newUserRating);
    }

    private float calculateMovieRatingDifference(MovieRating movieRating) throws DaoException {
        Movie movie = movieDao.findEntityById(movieRating.getMovieId());
        return Math.abs(movie.getAverageRating() - movieRating.getValue());
    }

    private MovieRating convertToMovieRating(String stringMovieId, String stringUserId, String stringValue) {
        MovieRating movieRating = new MovieRating();
        int movieId = Integer.valueOf(stringMovieId);
        movieRating.setMovieId(movieId);
        int userId = Integer.valueOf(stringUserId);
        movieRating.setUserId(userId);
        int value = Integer.valueOf(stringValue);
        movieRating.setValue(value);
        return movieRating;
    }
}