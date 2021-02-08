package com.example.demo_web.service.impl;

import com.example.demo_web.connection.ConnectionPool;
import com.example.demo_web.dao.api.UserDao;
import com.example.demo_web.dao.api.impl.UserDaoImpl;
import com.example.demo_web.entity.User;
import com.example.demo_web.exception.ConnectionException;
import com.example.demo_web.exception.DaoException;
import com.example.demo_web.exception.ServiceException;
import com.example.demo_web.service.RegisterService;
import com.example.demo_web.service.UserService;
import com.example.demo_web.validator.UserValidator;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final UserDao userDao = UserDaoImpl.getInstance();

    public UserServiceImpl() {
        Connection connection = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
        } catch (ConnectionException e) {
            logger.error(e);
        }
        userDao.setConnection(connection);
    }

    @Override
    public Optional<User> login(String login, String password) throws ServiceException {
        Optional<User> user = Optional.empty();
        if (UserValidator.isValidLogin(login) && UserValidator.isValidPassword(password)) {
            try {
                String encryptedPassword = DigestUtils.md5Hex(password);
                user = userDao.findByLoginAndPassword(login, encryptedPassword);
            } catch (DaoException e) {
                throw new ServiceException(e.getMessage(), e);
            }
        } else {
            logger.error("Login or password is not valid.");
            throw new ServiceException("Login or password is not valid.");
        }
        return user;
    }

    @Override
    public boolean registerUser(String login, String email, String password) throws ServiceException {
        try {
            String passwordHash = DigestUtils.md5Hex(password);
            userDao.create(new User(login, email, passwordHash));
            return true;
        } catch (DaoException e) {
            logger.info("error: " + e);
            return false;
        }
    }
}