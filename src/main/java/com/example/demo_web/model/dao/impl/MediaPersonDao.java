package com.example.demo_web.model.dao.impl;

import com.example.demo_web.model.dao.column.MediaPersonsColumn;
import com.example.demo_web.model.entity.OccupationType;
import com.example.demo_web.model.dao.AbstractMediaPersonDao;
import com.example.demo_web.model.entity.MediaPerson;
import com.example.demo_web.exception.DaoException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MediaPersonDao extends AbstractMediaPersonDao {
    private static final AbstractMediaPersonDao instance = new MediaPersonDao();

    private static final String SQL_SELECT_ALL_MEDIA_PERSONS_WITH_LIMIT = "SELECT MP.media_person_id, MP.media_person_first_name, MP.media_person_second_name, MPO.media_person_occupation_name, MP.media_person_bio, MP.media_person_birthday, MP.media_person_picture FROM media_persons MP INNER JOIN media_person_occupation MPO on MP.media_person_occupation_id = MPO.media_person_occupation_id ORDER BY MP.media_person_second_name LIMIT ?, ?;";

    private static final String SQL_COUNT_MEDIA_PERSONS = "SELECT COUNT(*) AS media_person_count FROM media_persons;";

    private static final String SQL_SELECT_MEDIA_PERSONS_BY_MOVIE_ID = "SELECT MP.media_person_id, MP.media_person_first_name, MP.media_person_second_name, MPO.media_person_occupation_name, MP.media_person_bio, MP.media_person_birthday, MP.media_person_picture FROM media_persons MP INNER JOIN media_person_occupation MPO on MP.media_person_occupation_id = MPO.media_person_occupation_id INNER JOIN media_persons_movies MPM on MP.media_person_id = MPM.media_person_id INNER JOIN movies M on MPM.movie_id = M.movie_id WHERE M.movie_id = ? ORDER BY MP.media_person_second_name;";

    private static final String SQL_SELECT_MEDIA_PERSON_BY_ID = "SELECT MP.media_person_id, MP.media_person_first_name, MP.media_person_second_name, MPO.media_person_occupation_name, MP.media_person_bio, MP.media_person_birthday, MP.media_person_picture FROM media_persons MP INNER JOIN media_person_occupation MPO on MP.media_person_occupation_id = MPO.media_person_occupation_id WHERE MP.media_person_id = ?;";

    private static final String SQL_UPDATE_MEDIA_PERSON = "UPDATE media_persons MP SET MP.media_person_first_name = ?, MP.media_person_second_name = ?, MP.media_person_occupation_id = ?, MP.media_person_bio = ?, MP.media_person_birthday = ?, MP.media_person_picture = ? WHERE MP.media_person_id = ?;";

    private static final String SQL_INSERT_MEDIA_PERSON = "INSERT INTO media_persons (media_person_first_name, media_person_second_name, media_person_occupation_id, media_person_bio, media_person_birthday, media_person_picture) VALUES (?, ?, ?, ?, ?, ?);";

    private static final String SQL_DELETE_MEDIA_PERSON_MOVIES = "DELETE FROM media_persons_movies MPM WHERE MPM.media_person_id = ?";

    private static final String SQL_INSERT_MEDIA_PERSON_MOVIE = "INSERT INTO media_persons_movies (media_person_id, movie_id) VALUES (?, ?);";

    private static final String SQL_SELECT_ALL_MOVIES = "SELECT MP.media_person_id, MP.media_person_first_name, MP.media_person_second_name, MPO.media_person_occupation_name, MP.media_person_bio, MP.media_person_birthday, MP.media_person_picture FROM media_persons MP INNER JOIN media_person_occupation MPO on MP.media_person_occupation_id = MPO.media_person_occupation_id ORDER BY MP.media_person_second_name;";

    private static final String SQL_DELETE_MEDIA_PERSON = "DELETE FROM media_persons MP WHERE MP.media_person_id = ?;";

    private static final String SQL_EXISTS_ID = "SELECT EXISTS (SELECT media_person_id FROM media_persons WHERE media_person_id = ?) AS media_person_existence;";

    private MediaPersonDao(){}

    public static AbstractMediaPersonDao getInstance() {
        return instance;
    }

    @Override
    public List<MediaPerson> findAllBetween(int begin, int end) throws DaoException {
        List<MediaPerson> mediaPersonList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ALL_MEDIA_PERSONS_WITH_LIMIT)) {
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
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean idExists(int id) throws DaoException {
        return idExists(id, SQL_EXISTS_ID);
    }

    @Override
    public int countMediaPersons() throws DaoException {
        int mediaPersonsCount = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_COUNT_MEDIA_PERSONS)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    mediaPersonsCount = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return mediaPersonsCount;
    }

    @Override
    public List<MediaPerson> findByMovieId(Integer id) throws DaoException {
        List<MediaPerson> crew = new ArrayList<>();
        MediaPerson mediaPerson;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MEDIA_PERSONS_BY_MOVIE_ID)) {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    mediaPerson = buildMediaPerson(resultSet);
                    crew.add(mediaPerson);
                }
            }
            return crew;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<MediaPerson> findAll() throws DaoException {
        List<MediaPerson> mediaPeople = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ALL_MOVIES)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                MediaPerson mediaPerson;
                while (resultSet.next()) {
                    mediaPerson = buildMediaPerson(resultSet);
                    mediaPeople.add(mediaPerson);
                }
            }
            return mediaPeople;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public MediaPerson findEntityById(Integer id) throws DaoException {
        MediaPerson mediaPerson = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MEDIA_PERSON_BY_ID)) {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    mediaPerson = buildMediaPerson(resultSet);
                }
            }
            return mediaPerson;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DaoException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_MEDIA_PERSON)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public MediaPerson create(MediaPerson mediaPerson) throws DaoException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_MEDIA_PERSON, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, mediaPerson.getFirstName());
            preparedStatement.setString(2, mediaPerson.getSecondName());
            preparedStatement.setInt(3, mediaPerson.getOccupationType().ordinal());
            preparedStatement.setString(4, mediaPerson.getBio());
            preparedStatement.setDate(5, java.sql.Date.valueOf(mediaPerson.getBirthday()));
            preparedStatement.setString(6, mediaPerson.getPicture());
            int id = executeUpdateAndGetGeneratedId(preparedStatement);
            mediaPerson.setId(id);
            return mediaPerson;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public MediaPerson update(MediaPerson mediaPerson) throws DaoException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_MEDIA_PERSON)) {
            preparedStatement.setString(1, mediaPerson.getFirstName());
            preparedStatement.setString(2, mediaPerson.getSecondName());
            preparedStatement.setInt(3, mediaPerson.getOccupationType().ordinal());
            preparedStatement.setString(4, mediaPerson.getBio());
            preparedStatement.setDate(5, java.sql.Date.valueOf(mediaPerson.getBirthday()));
            preparedStatement.setString(6, mediaPerson.getPicture());
            preparedStatement.setInt(7, mediaPerson.getId());
            preparedStatement.executeUpdate();
            return mediaPerson;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean deleteMediaPersonMovies(Integer mediaPersonId) throws DaoException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_MEDIA_PERSON_MOVIES)) {
            preparedStatement.setInt(1, mediaPersonId);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean insertMediaPersonMovie(Integer mediaPersonId, Integer movieId) throws DaoException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_MEDIA_PERSON_MOVIE)) {
            preparedStatement.setInt(1, mediaPersonId);
            preparedStatement.setInt(2, movieId);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private MediaPerson buildMediaPerson(ResultSet resultSet) throws SQLException {
        if (resultSet.wasNull()) {
            return null;
        }
        MediaPerson mediaPerson = new MediaPerson();
        Integer actorId = resultSet.getInt(MediaPersonsColumn.ID);
        mediaPerson.setId(actorId);
        String actorFirstName = resultSet.getString(MediaPersonsColumn.FIRST_NAME);
        mediaPerson.setFirstName(actorFirstName);
        String actorSecondName = resultSet.getString(MediaPersonsColumn.SECOND_NAME);
        mediaPerson.setSecondName(actorSecondName);
        OccupationType occupationType = OccupationType.valueOf(resultSet.getString(MediaPersonsColumn.OCCUPATION_TYPE));
        mediaPerson.setOccupationType(occupationType);
        String actorBio = resultSet.getString(MediaPersonsColumn.BIO);
        mediaPerson.setBio(actorBio);
        LocalDate actorBirthday = resultSet.getDate(MediaPersonsColumn.BIRTHDAY).toLocalDate();
        mediaPerson.setBirthday(actorBirthday);
        String actorPicture = resultSet.getString(MediaPersonsColumn.PICTURE);
        mediaPerson.setPicture(actorPicture);
        return mediaPerson;
    }
}
