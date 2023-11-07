package services;

import dataAccess.*;
import services.responses.ListGamesResult;

/**
 * ListGameService class
 */
public class ListGameService {

    private AuthDAO authDAO = new DbAuthDAO();
    private GameDAO gameDAO = new DbGameDAO();

    /**
     * Gives a list of all games.
     * @return ListGamesResult
     */
    public ListGamesResult listGames(String authToken) {
        ListGamesResult result = new ListGamesResult();

        try {
            authDAO.find(authToken);
            result.setGames(gameDAO.findAll());
            result.setReturnCode(200);
        }
        catch (DataAccessException e) {
            result.setReturnCode(401);
            result.setMessage("Error: unauthorized");
        }
        return result;
    }
}
