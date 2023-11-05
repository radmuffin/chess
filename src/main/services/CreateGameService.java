package services;

import dataAccess.*;
import models.Game;
import services.requests.CreateGameRequest;
import services.responses.CreateGameResult;
import services.responses.ResponseMessage;

/**
 * CreateGameService class
 */
public class CreateGameService {

    private AuthDAO authDAO = new DbAuthDAO();
    private GameDAO gameDAO = new MemGameDAO();

    /**
     * Creates a new game.
     * @param request object
     * @return error message or gameID through object
     */
    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        CreateGameResult result = new CreateGameResult();

        if (request.getGameName() == null || authToken == null) {
            result.setReturnCode(400);
            result.setMessage("Error: bad request");
            return result;
        }

        try {
            authDAO.find(authToken);    //validate authentication, throws if dne

            Game game = new Game(request.getGameName());
            gameDAO.insert(game);

            result.setGameID(game.getGameID());
            result.setReturnCode(200);
        }
        catch (DataAccessException e) {
            result.setReturnCode(401);
            result.setMessage("Error: unauthorized");
        }
        return result;
    }
}
