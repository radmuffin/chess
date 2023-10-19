package services;

import services.requests.JoinGameRequest;
import services.responses.ResponseMessage;

/**
 * JoinGameService class
 */
public class JoinGameService {
    /**
     * Verifies that the specified game exists,
     * and, if a color is specified, adds the caller
     * as the requested color to the game.
     * If no color is specified the user is joined as an observer.
     * This request is idempotent.
     * @param request object
     * @return nothing or failure message
     */
    public ResponseMessage joinGame(JoinGameRequest request) {
        return null;
    }
}
