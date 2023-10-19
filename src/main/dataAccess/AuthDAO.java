package dataAccess;

import models.AuthToken;

/**
 * AuthDAO class
 */
public class AuthDAO {
    /**
     * Insert authToken into the database
     * @param authToken to insert
     * @throws DataAccessException if error
     */
    public void insert(AuthToken authToken) throws DataAccessException {

    }

    /**
     * finds AuthToken for a given username
     * @param username to look for
     * @return authToken
     * @throws DataAccessException if error
     */
    public AuthToken find(String username) throws DataAccessException {
        return null;
    }

    /**
     * clears all authTokens from database
     * @throws DataAccessException if error
     */
    public void clear() throws DataAccessException {

    }
}
