package webSocketMessages.userCommands;

import chess.ChessGame;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JoinPlayerCmd that)) return false;
        if (!super.equals(o)) return false;
        return gameID == that.gameID && playerColor == that.playerColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameID, playerColor);
    }
}
