package chess;

import java.util.Collection;
import java.util.HashSet;

public class Bishop extends ChessPieceAb{

    public Bishop(ChessGame.TeamColor color) {
        team = color;
        type = PieceType.BISHOP;
        moves = 0;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> options = new HashSet<>();
        probe(1,1, myPosition, options, board);     //upper right
        probe(1,-1, myPosition, options, board);    //upper left
        probe(-1,1, myPosition, options, board);    //lower right
        probe(-1,-1, myPosition, options, board);   //lower left
        return options;
    }

    @Override
    public ChessPiece copy() {
        return new Bishop(team);
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
        if (team == ChessGame.TeamColor.WHITE) return "B";
        else return "b";
    }


}
