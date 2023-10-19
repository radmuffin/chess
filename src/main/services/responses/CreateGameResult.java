package services.responses;

/**
 * CreateGame class
 */
public class CreateGameResult {
    private String message;
    private int gameID;

    /**
     * @param message for fail
     */
    public CreateGameResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
