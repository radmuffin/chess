package chess;

import java.util.Collection;
import java.util.HashSet;

public class Knight extends ChessPieceAb{

    public Knight(ChessGame.TeamColor color) {
        team = color;
        type = PieceType.KNIGHT;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> options = new HashSet<ChessMove>();
        probe(1,2, myPosition, options, board);     //up right right
        probe(-1,2, myPosition, options, board);    //down right right
        probe(-2,1, myPosition, options, board);    //down down right
        probe(-2,-1, myPosition, options, board);   //down down left
        probe(-1,-2, myPosition, options, board);   //down left left
        probe(1,-2, myPosition, options, board);    //up left left
        probe(2,-1, myPosition, options, board);    //up up left
        probe(2,1, myPosition, options, board);     //up up right
        return options;
    }

    @Override
    public ChessPiece copy() {
        return new Knight(team);
    }

    //adds move per adjust h, v if valid
    private void probe(int v, int h, ChessPosition start, Collection<ChessMove> stuff, ChessBoard board) {
        ChessPosition look = start.copy();
        look.adjust(v, h);
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
        if (team == ChessGame.TeamColor.WHITE) return "N";
        else return "n";
    }
}
