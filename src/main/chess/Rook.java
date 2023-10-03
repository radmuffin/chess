package chess;

import java.util.Collection;

public class Rook extends ChessPieceAb{

    public Rook(ChessGame.TeamColor color) {
        team = color;
        type = PieceType.ROOK;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return super.pieceMoves(board, myPosition);
    }
}
