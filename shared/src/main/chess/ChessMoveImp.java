package chess;

import java.util.Objects;

import static java.lang.Math.abs;

public class ChessMoveImp implements ChessMove{

    private final ChessPosition start;
    private final ChessPosition end;
    private Boolean isEnPassant;

    private final ChessPiece.PieceType promoPiece;

    public ChessMoveImp(ChessPosition st, ChessPosition en, ChessPiece.PieceType promo) {
        start = st;
        end = en;
        promoPiece = promo;
        isEnPassant = false;
    }


    @Override
    public ChessPosition getStartPosition() {
        return start;
    }

    @Override
    public int jump() {
        return abs(end.getRow() - start.getRow());
    }

    @Override
    public ChessPosition getEndPosition() {
        return end;
    }

    @Override
    public boolean isEnPassant() {
        return isEnPassant;
    }

    @Override
    public void setPassant() {
        isEnPassant = true;
    }

    @Override
    public boolean sideways() {
        return (start.getColumn() - end.getColumn() != 0);
    }

    @Override
    public boolean leapSideways() {
        return (abs(start.getColumn() - end.getColumn()) > 1);
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
        return start.equals(that.start) && end.equals(that.end) && promoPiece == that.promoPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promoPiece);
    }

    @Override
    public String toString() {
        return start.toString() +
                ">>" +
                end.toString();
    }
}
