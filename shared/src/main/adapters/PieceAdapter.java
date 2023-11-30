package adapters;

import chess.*;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PieceAdapter implements JsonDeserializer<ChessPiece> {
    @Override
    public ChessPiece deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        ChessPiece temp = new Gson().fromJson(jsonElement, Pawn.class);     //temporary to get piece type
        return switch (temp.getPieceType()) {

            case KING -> new King(temp.getTeamColor());
            case QUEEN -> new Queen(temp.getTeamColor());
            case BISHOP -> new Bishop(temp.getTeamColor());
            case KNIGHT -> new Knight(temp.getTeamColor());
            case ROOK -> new Rook(temp.getTeamColor());
            case PAWN -> temp;
        };
    }
}
