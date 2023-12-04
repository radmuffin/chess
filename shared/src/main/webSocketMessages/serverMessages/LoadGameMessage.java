package webSocketMessages.serverMessages;

import adapters.BoardAdapter;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGameImp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

public class LoadGameMessage extends ServerMessage {

    private String game;

    public LoadGameMessage() {
        super(ServerMessageType.LOAD_GAME);
    }

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = new Gson().toJson(game);
    }
    public ChessGame getGame() {
        var builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessBoard.class, new BoardAdapter());

        return builder.create().fromJson(game, ChessGameImp.class);
    }

    public void setGame(String game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoadGameMessage that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), game);
    }
}
