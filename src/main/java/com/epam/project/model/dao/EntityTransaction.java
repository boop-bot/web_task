package com.epam.project.model.dao;

import com.epam.project.exception.ConnectionException;
import com.epam.project.model.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The type Entity transaction.
 */
public class EntityTransaction {
    private Connection connection;
    private static Logger logger = LogManager.getLogger(EntityTransaction.class.getName());

    /**
     * Instantiates a new Entity transaction.
     */
    public EntityTransaction(){}

    /**
     * Init transaction.
     *
     * @param daos the daos
     */
    public void initTransaction(AbstractBaseDao ... daos) {
        try {
            if (connection == null) {
                connection = ConnectionPool.getInstance().getConnection();
            }
            connection.setAutoCommit(false);
            for(AbstractBaseDao dao : daos){
                dao.setConnection(connection);
            }
        } catch (SQLException | ConnectionException e) {
            logger.error(e);
        }
    }

    /**
     * End transaction.
     */
    public void endTransaction() {
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        connection = null;
    }

    /**
     * Commit.
     */
    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    /**
     * Rollback.
     */
    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    /**
     * Init.
     *
     * @param dao the dao
     */
    public void init(AbstractBaseDao dao) {
        if (connection == null) {
            try {
                connection = ConnectionPool.getInstance().getConnection();
            } catch (ConnectionException e) {
                logger.error(e);
            }
        }
        dao.setConnection(connection);
    }

    /**
     * End.
     */
    public void end() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error(e);
            }
        }
        connection = null;
    }
}
