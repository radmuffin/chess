package services.responses;

import models.Game;

/**
 * ListGamesResult class
 */
public class ListGamesResult {
    private String message;
    private Game[] games;

    /**
     * @param message when fails
     */
    public ListGamesResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Game[] getGames() {
        return games;
    }

    public void setGames(Game[] games) {
        this.games = games;
    }
}
