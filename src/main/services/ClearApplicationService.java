package services;

import dataAccess.*;
import services.responses.ResponseMessage;

/**
 * ClearApplicationService class
 */
public class ClearApplicationService {

    private AuthDAO authDAO = new MemAuthDAO();
    private GameDAO gameDAO = new MemGameDAO();
    private UserDAO userDAO = new MemUserDAO();


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
