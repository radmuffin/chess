package chess;

public class ChessMoveImp implements ChessMove{

    private ChessPosition start;
    private ChessPosition end;

    public ChessMoveImp(ChessPosition st, ChessPosition en) {
        start = st;
        end = en;
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
    public ChessPiece.PieceType getPromotionPiece() {// TODO: 10/2/2023
        return null;
    }
}
