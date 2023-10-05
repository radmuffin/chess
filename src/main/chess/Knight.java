package chess;

import java.util.Collection;

public class Knight extends ChessPieceAb{

    public Knight(ChessGame.TeamColor color) {
        team = color;
        type = PieceType.KNIGHT;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return super.pieceMoves(board, myPosition);
    }

    @Override
    public String toString() {
        if (team == ChessGame.TeamColor.WHITE) return "N";
        else return "n";
    }
}
