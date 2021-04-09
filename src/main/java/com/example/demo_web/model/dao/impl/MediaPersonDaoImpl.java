package com.example.demo_web.model.dao.impl;

import com.example.demo_web.model.entity.OccupationType;
import com.example.demo_web.model.pool.ConnectionPool;
import com.example.demo_web.model.dao.MediaPersonDao;
import com.example.demo_web.model.entity.MediaPerson;
import com.example.demo_web.exception.ConnectionException;
import com.example.demo_web.exception.DaoException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MediaPersonDaoImpl implements MediaPersonDao {
    private static MediaPersonDao instance = new MediaPersonDaoImpl();

    private static final String SQL_SELECT_ALL_MEDIA_PERSONS_WITH_LIMIT = "SELECT MP.media_person_id, MP.media_person_first_name, MP.media_person_second_name, MPO.media_person_occupation_name, MP.media_person_bio, MP.media_person_birthday, MP.media_person_picture FROM media_persons MP INNER JOIN media_person_occupation MPO on MP.media_person_occupation_id = MPO.media_person_occupation_id WHERE MP.media_person_is_deleted = 0 LIMIT ?, ?;";

    private static final String SQL_COUNT_MEDIA_PERSONS = "SELECT COUNT(*) AS media_person_count FROM media_persons WHERE media_person_is_deleted = 0;";

    private static final String SQL_SELECT_MEDIA_PERSONS_BY_MOVIE_ID = "SELECT MP.media_person_id, MP.media_person_first_name, MP.media_person_second_name, MPO.media_person_occupation_name, MP.media_person_bio, MP.media_person_birthday, MP.media_person_picture FROM media_persons MP INNER JOIN media_person_occupation MPO on MP.media_person_occupation_id = MPO.media_person_occupation_id INNER JOIN media_persons_movies MPM on MP.media_person_id = MPM.media_person_id INNER JOIN movies M on MPM.movie_id = M.movie_id WHERE M.movie_id = ? AND MP.media_person_is_deleted = 0 AND M.movie_is_deleted = 0;";

    private static final String SQL_SELECT_MEDIA_PERSON_BY_ID = "SELECT MP.media_person_id, MP.media_person_first_name, MP.media_person_second_name, MPO.media_person_occupation_name, MP.media_person_bio, MP.media_person_birthday, MP.media_person_picture FROM media_persons MP INNER JOIN media_person_occupation MPO on MP.media_person_occupation_id = MPO.media_person_occupation_id WHERE MP.media_person_id = ? AND MP.media_person_is_deleted = 0;";

    private static final String SQL_UPDATE_MEDIA_PERSON = "UPDATE media_persons MP SET MP.media_person_first_name = ?, MP.media_person_second_name = ?, MP.media_person_occupation_id = ?, MP.media_person_bio = ?, MP.media_person_birthday = ?, MP.media_person_picture = ? WHERE MP.media_person_id = ?;";

    private static final String SQL_INSERT_MEDIA_PERSON = "INSERT INTO media_persons (media_person_first_name, media_person_second_name, media_person_occupation_id, media_person_bio, media_person_birthday, media_person_picture, media_person_is_deleted) VALUES (?, ?, ?, ?, ?, ?, 0);";

    private static final String SQL_DELETE_MEDIA_PERSON_MOVIES = "DELETE FROM media_persons_movies MPM WHERE MPM.media_person_id = ?";

    private static final String SQL_INSERT_MEDIA_PERSON_MOVIES = "INSERT INTO media_persons_movies (media_person_id, movie_id) VALUES (?, ?);";

    private static final String DEFAULT_ID_COLUMN = "media_person_id";
    private static final String FIRST_NAME_COLUMN = "media_person_first_name";
    private static final String SECOND_NAME_COLUMN = "media_person_second_name";
    private static final String OCCUPATION_TYPE_COLUMN = "media_person_occupation_name";
    private static final String BIO_COLUMN = "media_person_bio";
    private static final String BIRTHDAY_COLUMN = "media_person_birthday";
    private static final String PICTURE_COLUMN = "media_person_picture";

    private MediaPersonDaoImpl(){}

    public static MediaPersonDao getInstance() {
        return instance;
    }

    @Override
    public List<MediaPerson> findAllBetween(int begin, int end) throws DaoException {
        List<MediaPerson> mediaPersonList = new ArrayList<>();
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ALL_MEDIA_PERSONS_WITH_LIMIT)) {
            preparedStatement.setInt(1, begin);
            preparedStatement.setInt(2, end);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                MediaPerson mediaPerson;
                while (resultSet.next()) {
                    mediaPerson = buildMediaPerson(resultSet);
                    mediaPersonList.add(mediaPerson);
                }
            }
            return mediaPersonList;
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public int countMediaPersons() throws DaoException {
        int actorsCount = 0;
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_COUNT_MEDIA_PERSONS)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                actorsCount = resultSet.getInt(1);
            }
        } catch (ConnectionException | SQLException e) {
            //logger.error(e);
            throw new DaoException(e);
        }
        return actorsCount;
    }

    @Override
    public List<MediaPerson> findByMovieId(Integer id) throws DaoException {
        List<MediaPerson> crew = new ArrayList<>();
        MediaPerson mediaPerson;
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MEDIA_PERSONS_BY_MOVIE_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                mediaPerson = buildMediaPerson(resultSet);
                crew.add(mediaPerson);
            }
            return crew;
        } catch (ConnectionException | SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<MediaPerson> findAll() throws DaoException {
        return null;
    }

    @Override
    public MediaPerson findEntityById(Integer id) throws DaoException {
        MediaPerson mediaPerson = null;
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MEDIA_PERSON_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                mediaPerson = buildMediaPerson(resultSet);
            }
            return mediaPerson;
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DaoException {
        return false;
    }

    @Override
    public MediaPerson create(MediaPerson mediaPerson) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_MEDIA_PERSON, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, mediaPerson.getFirstName());
            preparedStatement.setString(2, mediaPerson.getSecondName());
            preparedStatement.setInt(3, mediaPerson.getOccupationType().ordinal());
            preparedStatement.setString(4, mediaPerson.getBio());
            preparedStatement.setDate(5, java.sql.Date.valueOf(mediaPerson.getBirthday()));
            preparedStatement.setString(6, mediaPerson.getPicture());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                mediaPerson.setId(generatedKeys.getInt(1));
            }
            return mediaPerson;
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public MediaPerson update(MediaPerson mediaPerson) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_MEDIA_PERSON)) {
            preparedStatement.setString(1, mediaPerson.getFirstName());
            preparedStatement.setString(2, mediaPerson.getSecondName());
            preparedStatement.setInt(3, mediaPerson.getOccupationType().ordinal());
            preparedStatement.setString(4, mediaPerson.getBio());
            preparedStatement.setDate(5, java.sql.Date.valueOf(mediaPerson.getBirthday()));
            preparedStatement.setString(6, mediaPerson.getPicture());
            preparedStatement.setInt(7, mediaPerson.getId());
            preparedStatement.executeUpdate();
            return mediaPerson;
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean deleteMediaPersonMovie(Integer mediaPersonId) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_MEDIA_PERSON_MOVIES)) {
            preparedStatement.setInt(1, mediaPersonId);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean insertMediaPersonMovie(Integer mediaPersonId, Integer movieId) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_MEDIA_PERSON_MOVIES)) {
            preparedStatement.setInt(1, mediaPersonId);
            preparedStatement.setInt(2, movieId);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
    }

    private MediaPerson buildMediaPerson(ResultSet resultSet) throws SQLException {
        if (resultSet.wasNull()) {
            return null;
        }
        MediaPerson mediaPerson = new MediaPerson();
        Integer actorId = resultSet.getInt(DEFAULT_ID_COLUMN);
        mediaPerson.setId(actorId);
        String actorFirstName = resultSet.getString(FIRST_NAME_COLUMN);
        mediaPerson.setFirstName(actorFirstName);
        String actorSecondName = resultSet.getString(SECOND_NAME_COLUMN);
        mediaPerson.setSecondName(actorSecondName);
        OccupationType occupationType = OccupationType.valueOf(resultSet.getString(OCCUPATION_TYPE_COLUMN));
        mediaPerson.setOccupationType(occupationType);
        String actorBio = resultSet.getString(BIO_COLUMN);
        mediaPerson.setBio(actorBio);
        LocalDate actorBirthday = resultSet.getDate(BIRTHDAY_COLUMN).toLocalDate();
        mediaPerson.setBirthday(actorBirthday);
        String actorPicture = resultSet.getString(PICTURE_COLUMN);
        mediaPerson.setPicture(actorPicture);
        return mediaPerson;
    }
}
