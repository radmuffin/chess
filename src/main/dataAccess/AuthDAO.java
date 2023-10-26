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
     * @param authToken to look for
     * @return authToken
     * @throws DataAccessException if error
     */
    String find(String authToken) throws DataAccessException;

    void remove(String authToken) throws DataAccessException;

    int size();

    /**
     * clears all authTokens from database
     * @throws DataAccessException if error
     */
    void clear() throws DataAccessException;
}
