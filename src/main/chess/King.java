package chess;

import java.util.Collection;

public class King extends ChessPieceAb{

    public King(ChessGame.TeamColor color) {
        team = color;
        type = PieceType.KING;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return super.pieceMoves(board, myPosition);
    }
}
