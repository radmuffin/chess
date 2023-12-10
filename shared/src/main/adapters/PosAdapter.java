package adapters;

import chess.ChessPosition;
import chess.ChessPositionImp;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PosAdapter implements JsonDeserializer<ChessPosition> {
    @Override
    public ChessPosition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new Gson().fromJson(jsonElement, ChessPositionImp.class);
    }
}
