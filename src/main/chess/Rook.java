package chess;

import java.util.Collection;
import java.util.HashSet;

public class Rook extends ChessPieceAb{

    public Rook(ChessGame.TeamColor color) {
        team = color;
        type = PieceType.ROOK;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> options = new HashSet<ChessMove>();
        probe(1,0, myPosition, options, board);     //right
        probe(0,1, myPosition, options, board);     //forward
        probe(-1,0, myPosition, options, board);    //left
        probe(0,-1, myPosition, options, board);    //right
        return options;
    }

    //adds moves going in direction per adjust h, v
    private void probe(int v, int h, ChessPosition start, Collection<ChessMove> stuff, ChessBoard board) {
        ChessPosition look = start.copy();
        look.adjust(v, h);
        while (look.onBoard()) {
            ChessPiece inSpot = board.getPiece(look);
            if (inSpot == null) {
                stuff.add(new ChessMoveImp(start, look, null));
            }
            else {
                if (inSpot.getTeamColor() != team) {
                    stuff.add(new ChessMoveImp(start, look, null));
                }
                break;
            }
            look = look.copy();
            look.adjust(v, h);
        }
    }


    @Override
    public String toString() {
        if (team == ChessGame.TeamColor.WHITE) return "R";
        else return "r";
    }
}
