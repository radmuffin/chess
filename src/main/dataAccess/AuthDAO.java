package dataAccess;

import models.AuthToken;

public interface AuthDAO {
    /**
     * Insert authToken into the database
     * @param authToken to insert
     * @throws DataAccessException if error
     */
    void insert(AuthToken authToken) throws DataAccessException;

    /**
     * finds AuthToken for a given username
     * @param username to look for
     * @return authToken
     * @throws DataAccessException if error
     */
    AuthToken find(String username) throws DataAccessException;

    void remove(String username) throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;

    /**
     * clears all authTokens from database
     * @throws DataAccessException if error
     */
    void clear() throws DataAccessException;
}
