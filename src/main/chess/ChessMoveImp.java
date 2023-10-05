package chess;

import java.util.Objects;

public class ChessMoveImp implements ChessMove{

    private ChessPosition start;
    private ChessPosition end;

    private ChessPiece.PieceType promoPiece;

    public ChessMoveImp(ChessPosition st, ChessPosition en, ChessPiece.PieceType promo) {
        start = st;
        end = en;
        promoPiece = promo;
    }


    @Override
    public ChessPosition getStartPosition() {
        return start;
    }

    @Override
    public ChessPosition getEndPosition() {
        return end;
    }

    @Override
    public ChessPiece.PieceType getPromotionPiece() {
        return promoPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMoveImp that = (ChessMoveImp) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end) && promoPiece == that.promoPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promoPiece);
    }
}
