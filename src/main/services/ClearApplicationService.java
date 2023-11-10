package services;

import dataAccess.*;
import responses.ResponseMessage;

/**
 * ClearApplicationService class
 */
public class ClearApplicationService {

    private final AuthDAO authDAO = new DbAuthDAO();
    private final GameDAO gameDAO = new DbGameDAO();
    private final UserDAO userDAO = new DbUserDAO();


    /**
     * Clears the database. Removes all users, games, and authTokens.
     * @return success or failure message response
     */
    public ResponseMessage clearApplication() {
        try {
            authDAO.clear();
            gameDAO.clear();
            userDAO.clear();
        }
        catch (DataAccessException ignored) {
            //no possible errors rn :)
        }
        return new ResponseMessage();
    }
}
