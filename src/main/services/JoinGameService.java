package services;

import dataAccess.*;
import models.Game;
import services.requests.JoinGameRequest;
import services.responses.ResponseMessage;

import java.util.Objects;

/**
 * JoinGameService class
 */
public class JoinGameService {

    private AuthDAO authDAO = new MemAuthDAO();
    private UserDAO userDAO = new MemUserDAO();
    private GameDAO gameDAO = new MemGameDAO();

    /**
     * Verifies that the specified game exists,
     * and, if a color is specified, adds the caller
     * as the requested color to the game.
     * If no color is specified the user is joined as an observer.
     * This request is idempotent.
     * @param request object
     * @return nothing or failure message
     */
    public ResponseMessage joinGame(JoinGameRequest request, String authToken) {
        ResponseMessage result = new ResponseMessage();

        if (authToken == null || request.getGameID() == 0) {
            result.setReturnCode(400);
            result.setMessage("Error: bad request");
            return result;
        }

        try {
            String username = authDAO.find(authToken);        //authenticate :)
            Game game = gameDAO.find(request.getGameID());

            if ((Objects.equals(request.getPlayerColor(), "WHITE") && game.getWhiteUsername() != null)
                    || (Objects.equals(request.getPlayerColor(), "BLACK") && game.getBlackUsername() != null)) {
                result.setReturnCode(403);
                result.setMessage("Error: already taken");
                return result;
            }

            gameDAO.claimSpot(username, request.getPlayerColor(), request.getGameID());
            result.setReturnCode(200);

        }
        catch (DataAccessException e) {
            result.setReturnCode(401);
            result.setMessage("Error: unauthorized");
        }

        return result;
    }
}
