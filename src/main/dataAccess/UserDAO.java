package dataAccess;

import models.User;

public interface UserDAO {
    /**
     * Insert user into the database
     *
     * @param user to insert
     * @throws DataAccessException if error
     */
    void insert(User user) throws DataAccessException;

    /**
     * finds user for a given username
     *
     * @param username to look for
     * @return user
     * @throws DataAccessException if error
     */
    User find(String username) throws DataAccessException;

    /**
     * clears all authTokens from database
     *
     * @throws DataAccessException if error
     */
    void clear() throws DataAccessException;
}
