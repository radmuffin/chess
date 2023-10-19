package dataAccess;

import models.AuthToken;
import models.Game;
import models.User;

/**
 * UserDAO
 */
public class UserDAO {
    /**
     * Insert user into the database
     * @param user to insert
     * @throws DataAccessException if error
     */
    public void insert(User user) throws DataAccessException {

    }

    /**
     * finds user for a given username
     * @param username to look for
     * @return user
     * @throws DataAccessException if error
     */
    public User find(String username) throws DataAccessException {
        return null;
    }

    /**
     * clears all authTokens from database
     * @throws DataAccessException if error
     */
    public void clear() throws DataAccessException {

    }


}
