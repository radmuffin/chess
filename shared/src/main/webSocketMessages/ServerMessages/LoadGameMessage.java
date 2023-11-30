package webSocketMessages.ServerMessages;

import adapters.BoardAdapter;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGameImp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import webSocketMessages.serverMessages.ServerMessage;

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


}
