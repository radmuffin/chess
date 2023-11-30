package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCmd extends UserGameCommand{

    private int gameID;
    private ChessGame.TeamColor playerColor;

    public JoinPlayerCmd(String authToken) {
        super(authToken);
        commandType = CommandType.JOIN_PLAYER;
    }

    public JoinPlayerCmd(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }
}
