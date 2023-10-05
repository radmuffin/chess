package chess;

import java.util.Collection;

public class Pawn extends ChessPieceAb{

    public Pawn(ChessGame.TeamColor color) {
        type = PieceType.PAWN;
        team = color;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return super.pieceMoves(board, myPosition);
    }

    @Override
    public String toString() {
        if (team == ChessGame.TeamColor.WHITE) return "P";
        else return "p";
    }
}
