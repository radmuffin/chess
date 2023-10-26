package services.responses;

import models.Game;

import java.util.Collection;

/**
 * ListGamesResult class
 */
public class ListGamesResult {
    private String message;
    private Collection<Game> games;
    private transient int returnCode;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Collection<Game> getGames() {
        return games;
    }

    public void setGames(Collection<Game> games) {
        this.games = games;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
