package chess;

import java.util.Collection;
import java.util.HashSet;

public class Pawn extends ChessPieceAb{

    public Pawn(ChessGame.TeamColor color) {
        type = PieceType.PAWN;
        team = color;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> options = new HashSet<ChessMove>();
        int forward;
        if (team == ChessGame.TeamColor.WHITE) forward = 1;
        else forward = -1;
        ChessPosition look = myPosition.copy();
        look.adjust(forward, 0);                //forward march
        ChessPiece atSpot = board.getPiece(look);
        if (atSpot == null) {
            if ((team == ChessGame.TeamColor.WHITE && look.getRow() == 7) ||        //check for promo
                    (team == ChessGame.TeamColor.BLACK && look.getRow() == 0)) {
                options.add(new ChessMoveImp(myPosition, look, PieceType.QUEEN));
                options.add(new ChessMoveImp(myPosition, look, PieceType.ROOK));
                options.add(new ChessMoveImp(myPosition, look, PieceType.BISHOP));
                options.add(new ChessMoveImp(myPosition, look, PieceType.KNIGHT));
            }
            else options.add(new ChessMoveImp(myPosition, look, null));
            if ((team == ChessGame.TeamColor.WHITE && myPosition.getRow() == 1) ||          //double start move
                    (team == ChessGame.TeamColor.BLACK && myPosition.getRow() == 6)) {
                look = look.copy();
                look.adjust(forward, 0);
                atSpot = board.getPiece(look);
                if (atSpot == null) options.add(new ChessMoveImp(myPosition, look, null));
            }
        }
        look = myPosition.copy();
        look.adjust(forward, 1);        //capture right diagonal
        if (look.onBoard()) {
            atSpot = board.getPiece(look);
            if (atSpot != null && atSpot.getTeamColor() != team) {
                if ((team == ChessGame.TeamColor.WHITE && look.getRow() == 7) ||        //check for promo
                        (team == ChessGame.TeamColor.BLACK && look.getRow() == 0)) {
                    options.add(new ChessMoveImp(myPosition, look, PieceType.QUEEN));
                    options.add(new ChessMoveImp(myPosition, look, PieceType.ROOK));
                    options.add(new ChessMoveImp(myPosition, look, PieceType.BISHOP));
                    options.add(new ChessMoveImp(myPosition, look, PieceType.KNIGHT));
                }
                else options.add(new ChessMoveImp(myPosition, look, null));
            }
        }
        look = myPosition.copy();
        look.adjust(forward, -1);        //capture left diagonal
        if (look.onBoard()) {
            atSpot = board.getPiece(look);
            if (atSpot != null && atSpot.getTeamColor() != team) {
                if ((team == ChessGame.TeamColor.WHITE && look.getRow() == 7) ||        //check for promo
                        (team == ChessGame.TeamColor.BLACK && look.getRow() == 0)) {
                    options.add(new ChessMoveImp(myPosition, look, PieceType.QUEEN));
                    options.add(new ChessMoveImp(myPosition, look, PieceType.ROOK));
                    options.add(new ChessMoveImp(myPosition, look, PieceType.BISHOP));
                    options.add(new ChessMoveImp(myPosition, look, PieceType.KNIGHT));
                }
                else options.add(new ChessMoveImp(myPosition, look, null));
            }
        }
        return options;
    }

    @Override
    public String toString() {
        if (team == ChessGame.TeamColor.WHITE) return "P";
        else return "p";
    }
}
