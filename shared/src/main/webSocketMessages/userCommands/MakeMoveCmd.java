package webSocketMessages.userCommands;

import chess.ChessMove;

import java.util.Objects;

public class MakeMoveCmd extends UserGameCommand{
    private int gameID;
    private ChessMove move;
    public MakeMoveCmd(String authToken) {
        super(authToken);
        commandType = CommandType.MAKE_MOVE;
    }

    public MakeMoveCmd(String authToken, int gameID, ChessMove move) {
        super(authToken);
        commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public ChessMove getMove() {
        return move;
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MakeMoveCmd that)) return false;
        if (!super.equals(o)) return false;
        return gameID == that.gameID && Objects.equals(move, that.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameID, move);
    }
}
