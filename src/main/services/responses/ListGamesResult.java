package services.responses;

import models.Game;

/**
 * ListGamesResult class
 */
public class ListGamesResult {
    private String message;
    private Game[] games;
    private transient int returnCode;

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

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
