package services.requests;

/**
 * JoinGameRequest class
 */
public class JoinGameRequest {
    private String playerColor;
    private int gameID;

    /**
     * @param playerColor
     * @param gameID
     */
    public JoinGameRequest(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
