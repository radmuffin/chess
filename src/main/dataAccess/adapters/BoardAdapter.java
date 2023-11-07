package dataAccess.adapters;

import chess.ChessBoard;
import chess.ChessBoardImp;
import chess.ChessPiece;
import com.google.gson.*;

import java.lang.reflect.Type;

public class BoardAdapter implements JsonDeserializer<ChessBoard> {
    @Override
    public ChessBoard deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPiece.class, new PieceAdapter());

        return builder.create().fromJson(jsonElement, ChessBoardImp.class);
    }
}
