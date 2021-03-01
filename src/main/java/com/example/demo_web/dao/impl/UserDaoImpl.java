package com.example.demo_web.dao.impl;

import com.example.demo_web.builder.BaseBuilder;
import com.example.demo_web.builder.impl.UserBuilder;
import com.example.demo_web.connection.ConnectionPool;
import com.example.demo_web.dao.UserDao;
import com.example.demo_web.entity.User;
import com.example.demo_web.exception.ConnectionException;
import com.example.demo_web.exception.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final String SQL_CREATE_USER = "INSERT INTO users (user_login, user_email, user_password, user_first_name, user_second_name, user_role_id, user_state_id) VALUES (?, ?, ?, ?, ?, ?, ?);";

    private static final String SQL_SELECT_USER_BY_LOGIN = "SELECT U.user_id, U.user_login, U.user_email, U.user_first_name, U.user_second_name, UR.user_role_name, US.user_state_name FROM users U INNER JOIN user_roles UR ON U.user_role_id = UR.user_role_id INNER JOIN user_states US ON U.user_state_id = US.user_state_id WHERE U.user_login = ?;";

    private static final String SQL_SELECT_USER_BY_ID = "SELECT U.user_id, U.user_login, U.user_email, U.user_first_name, U.user_second_name, UR.user_role_name, US.user_state_name FROM users U INNER JOIN user_roles UR ON U.user_role_id = UR.user_role_id INNER JOIN user_states US ON U.user_state_id = US.user_state_id WHERE U.user_id = ?;";

    private static final String SQL_SELECT_PASSWORD_BY_LOGIN = "SELECT user_password FROM users WHERE user_login = ?;";

    private static final String SQL_LOGIN_EXISTS = "SELECT EXISTS(SELECT user_login FROM users WHERE user_login = ?) AS user_existence;";

    private static final String SQL_ACTIVATE_USER = "UPDATE users SET user_state_id = 1 WHERE user_id = ?;";

    private static UserDao instance = new UserDaoImpl();

    private UserDaoImpl(){}

    public static UserDao getInstance() {
        return instance;
    }

    @Override
    public List<User> findAll()  throws DaoException {
        return new ArrayList<>();
    }

    @Override
    public User findEntityById(Integer id) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    BaseBuilder<User> baseBuilder = new UserBuilder();
                    return baseBuilder.build(resultSet);
                }
            }
            return null;
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }

    @Override
    public boolean delete(User user) {
        return true;
    }

    @Override
    public boolean create(User user) throws DaoException{
        return false;
    }

    @Override
    public int create(User user, String encryptedPassword) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE_USER, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, encryptedPassword);
            preparedStatement.setString(4, user.getFirstName());
            preparedStatement.setString(5, user.getSecondName());
            preparedStatement.setInt(6, user.getUserState().ordinal());
            preparedStatement.setInt(7, user.getUserRole().ordinal());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public Optional<User> findUserByLogin(String login) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_USER_BY_LOGIN)) {
            preparedStatement.setString(1, login);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    BaseBuilder<User> baseBuilder = new UserBuilder();
                    return Optional.of(baseBuilder.build(resultSet));
                }
            }
            return Optional.empty();
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<String> findPasswordByLogin(String login) throws DaoException {
        Optional<String> foundPassword = Optional.empty();
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_PASSWORD_BY_LOGIN)) {
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                foundPassword = Optional.ofNullable(resultSet.getString("user_password"));
            }
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
        return foundPassword;
    }

    @Override
    public boolean loginExists(String login) throws DaoException {
        boolean result = false;
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_LOGIN_EXISTS)) {
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1) != 0;
            }
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
        return result;
    }

    @Override
    public boolean activateUser(int id) throws DaoException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_ACTIVATE_USER)) {
            statement.setInt(1, id);
            return (statement.executeUpdate() == 1);
        } catch (SQLException | ConnectionException e) {
            throw new DaoException(e);
        }
    }
}