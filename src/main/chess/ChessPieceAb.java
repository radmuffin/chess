package chess;

import java.util.Collection;

public abstract class ChessPieceAb implements ChessPiece{

    protected ChessGame.TeamColor team;
    protected PieceType type;
    protected ChessPosition spot;

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return team;
    }

    @Override
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return null;
    }
}
