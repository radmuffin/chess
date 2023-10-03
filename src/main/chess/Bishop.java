package chess;

import java.util.Collection;

public class Bishop extends ChessPieceAb{

    public Bishop(ChessGame.TeamColor color) {
        team = color;
        type = PieceType.BISHOP;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return super.pieceMoves(board, myPosition);
    }
}
