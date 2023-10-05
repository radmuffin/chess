package chess;

import java.util.Collection;
import java.util.HashSet;

public class King extends ChessPieceAb{

    public King(ChessGame.TeamColor color) {
        team = color;
        type = PieceType.KING;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> options = new HashSet<ChessMove>();
        probe(1,0, myPosition, options, board);     //up
        probe(1,1, myPosition, options, board);     //up right
        probe(0,1, myPosition, options, board);     //right
        probe(-1,1, myPosition, options, board);    //down right
        probe(-1,0, myPosition, options, board);    //down
        probe(-1,-1, myPosition, options, board);   //down left
        probe(0,-1, myPosition, options, board);    //left
        probe(1,-1, myPosition, options, board);    //up left
        return options;
    }

    //adds move per adjust h, v if valid
    private void probe(int h, int v, ChessPosition start, Collection<ChessMove> stuff, ChessBoard board) {
        ChessPosition look = start.copy();
        look.adjust(h, v);
        if (look.onBoard()) {
            ChessPiece inSpot = board.getPiece(look);
            if (inSpot == null) {
                stuff.add(new ChessMoveImp(start, look, null));
            }
            else {
                if (inSpot.getTeamColor() != team) {
                    stuff.add(new ChessMoveImp(start, look, null));
                }
            }
        }
    }

    @Override
    public String toString() {
        if (team == ChessGame.TeamColor.WHITE) return "K";
        else return "k";
    }
}
