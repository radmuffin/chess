package chess;

import java.util.Collection;
import java.util.HashSet;

public class ChessGameImp implements ChessGame{

    private TeamColor turn;
    private ChessBoard board;

    public ChessGameImp() {
        turn = TeamColor.WHITE;
        board = new ChessBoardImp();
    }

    @Override
    public TeamColor getTeamTurn() {
        return turn;
    }

    @Override
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        HashSet<ChessMove> goodOps = new HashSet<>();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) return goodOps;
        Collection<ChessMove> ops = piece.pieceMoves(board, startPosition);
        ChessBoard og = board;
        for (ChessMove move : ops) {    // need to take check and stuff into account, remove invalids
            board = og.copy();
            board.movePiece(move);
            if (!isInCheck(piece.getTeamColor())) goodOps.add(move);
        }
        board = og;
        return goodOps;
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!move.getStartPosition().onBoard()) throw new InvalidMoveException("you're off the map");
        ChessPiece subject = board.getPiece(move.getStartPosition());
        if (subject.getTeamColor() != turn) throw new InvalidMoveException("not you");
        Collection<ChessMove> options = validMoves(move.getStartPosition());
        if (options != null && options.contains(move)) {
            board.movePiece(move);
            yourTurn();
        }
        else throw new InvalidMoveException("Invalid move!");

    }

    private void yourTurn() {
        if (turn == TeamColor.WHITE) turn = TeamColor.BLACK;
        else turn = TeamColor.WHITE;
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingAt = findKing(teamColor);
        ChessPosition spot = new ChessPositionImp(1,1);
        for (int i = 1; i <= 8; ++i) {
            for (int k = 1; k <= 8; ++k) {
                spot.setPos(i,k);
                ChessPiece kingKiller = board.getPiece(spot);
                if (kingKiller != null && kingKiller.getTeamColor() != teamColor) {
                    for (ChessMove attack : kingKiller.pieceMoves(board, spot)) {
                        if (attack.getEndPosition().equals(kingAt)) return true;
                    }
                }
            }
        }
        return false;
    }

    ChessPosition findKing(TeamColor color) {
        ChessPosition spot = new ChessPositionImp(1, 1);
        for (int i = 1; i <= 8; ++i) {
            for (int k = 1; k <= 8; ++k) {
                spot.setPos(i,k);
                ChessPiece look = board.getPiece(spot);
                if (look != null && look.getPieceType() == ChessPiece.PieceType.KING && look.getTeamColor() == color) {
                    return spot;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
    }

    @Override
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    @Override
    public ChessBoard getBoard() {
        return board;
    }
}
