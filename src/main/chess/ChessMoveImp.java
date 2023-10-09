package chess;

import java.util.Objects;

import static java.lang.Math.abs;

public class ChessMoveImp implements ChessMove{

    private ChessPosition start;
    private ChessPosition end;
    private Boolean isEnPassant;

    private ChessPiece.PieceType promoPiece;

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

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(start.toString());
        out.append(">>");
        out.append(end.toString());
        return out.toString();
    }
}
