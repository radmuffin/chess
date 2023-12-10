package adapters;

import chess.ChessMove;
import chess.ChessMoveImp;
import chess.ChessPosition;
import com.google.gson.*;

import java.lang.reflect.Type;

public class MoveAdapter implements JsonDeserializer<ChessMove> {

    @Override
    public ChessMove deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPosition.class, new PosAdapter());

        return builder.create().fromJson(jsonElement, ChessMoveImp.class);
    }
}
