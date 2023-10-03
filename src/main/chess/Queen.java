package chess;

import java.util.Collection;

public class Queen extends ChessPieceAb{

    public Queen(ChessGame.TeamColor color) {
        team = color;
        type = PieceType.QUEEN;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return super.pieceMoves(board, myPosition);
    }
}
