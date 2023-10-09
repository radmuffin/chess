package chess;

import java.util.Collection;
import java.util.HashSet;

public class ChessGameImp implements ChessGame{

    private TeamColor turn;
    private ChessBoard board;

    private ChessPosition enPassantAble;

    public ChessGameImp() {
        turn = TeamColor.WHITE;
        board = new ChessBoardImp();
        enPassantAble = null;
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
        enPassantLook(startPosition, ops);
        ChessBoard og = board;
        for (ChessMove move : ops) {    // need to take check and stuff into account, remove invalids
            board = og.copy();
            board.movePiece(move);
            if (!isInCheck(piece.getTeamColor())) goodOps.add(move);
        }
        board = og;
        return goodOps;
    }

    private void enPassantLook(ChessPosition start, Collection<ChessMove> moves) {
        if (board.getPiece(start).getPieceType() != ChessPiece.PieceType.PAWN) return;
        if (enPassantAble == null) return;
        int forward = 1;
        if (turn == TeamColor.BLACK) forward = -1;
        ChessPosition nextTo = new ChessPositionImp(start.getRow() + 1, start.getColumn()); //look left first
        if (nextTo.onBoard() && enPassantAble.equals(nextTo)) {
            nextTo.adjust(forward,0); //changing to actual move end position
            ChessMove yeet = new ChessMoveImp(start, nextTo, null);
            yeet.setPassant();
            moves.add(yeet);
        }
        nextTo = new ChessPositionImp(start.getRow() + 1, start.getColumn() + 2);       //looking right
        if (nextTo.onBoard() && enPassantAble.equals(nextTo)) {
            nextTo.adjust(forward,0); //changing to actual move end position
            ChessMove yeet = new ChessMoveImp(start, nextTo, null);
            yeet.setPassant();
            moves.add(yeet);
        }
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!move.getStartPosition().onBoard()) throw new InvalidMoveException("you're off the map");
        ChessPiece subject = board.getPiece(move.getStartPosition());
        if (subject.getTeamColor() != turn) throw new InvalidMoveException("not you");
        Collection<ChessMove> options = validMoves(move.getStartPosition());
        if (options != null && options.contains(move)) {
            if (subject.getPieceType() == ChessPiece.PieceType.PAWN && move.sideways()
                    && board.getPiece(move.getEndPosition()) == null) {         //fricking en passant
                int forward = 1;
                if (turn == TeamColor.BLACK) forward = -1;
                ChessPosition deadPawn = new ChessPositionImp(move.getEndPosition().getRow() + 1 - forward, move.getEndPosition().getColumn() + 1);
                board.removePiece(deadPawn);
            }
            board.movePiece(move);
            board.getPiece(move.getEndPosition()).incMoves();
            if (subject.getPieceType() == ChessPiece.PieceType.PAWN && move.jump() == 2) {  //priming en passant vulnerability
                enPassantAble = move.getEndPosition();
            }
            else enPassantAble = null;
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
        if (!isInCheck(teamColor)) return false;
        ChessPosition spot = new ChessPositionImp(1,1);
        for (int i = 1; i <= 8; ++i) {
            for (int k = 1; k <= 8; ++k) {
                spot.setPos(i, k);
                ChessPiece piece = board.getPiece(spot);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(spot).isEmpty()) return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) return false;
        ChessPosition spot = new ChessPositionImp(1,1);
        for (int i = 1; i <= 8; ++i) {
            for (int k = 1; k <= 8; ++k) {
                spot.setPos(i, k);
                ChessPiece piece = board.getPiece(spot);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(spot).isEmpty()) return false;
                }
            }
        }
        return true;
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
