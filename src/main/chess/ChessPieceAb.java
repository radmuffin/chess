package chess;

import java.util.Collection;
import java.util.Objects;

public abstract class ChessPieceAb implements ChessPiece{

    protected ChessGame.TeamColor team;

    protected PieceType type;

    protected int moves;

    @Override
    public void incMoves() {
        ++moves;
    }

    @Override
    public int numPastMoves() { return moves; }

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

    public abstract ChessPiece copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPieceAb that)) return false;
        return moves == that.moves && team == that.team && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, type, moves);
    }
}
