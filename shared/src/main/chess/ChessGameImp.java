package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class ChessGameImp implements ChessGame{

    private TeamColor turn;
    private ChessBoard board;
    private GameState state;

    private ChessPosition enPassantAble;

    public ChessGameImp() {
        turn = TeamColor.WHITE;
        board = new ChessBoardImp();
        enPassantAble = null;
        state = GameState.NEW_GAME;
    }


    @Override
    public void setGameState(GameState state) {
        this.state = state;
    }

    @Override
    public GameState getGameState() {
        return state;
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
        castleShallWe(startPosition, ops);
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

    private void castleShallWe(ChessPosition start, Collection<ChessMove> moves) {
        ChessPiece piece = board.getPiece(start);
        if (isInCheck(piece.getTeamColor())) return;
        if (piece.getPieceType() != ChessPiece.PieceType.KING || piece.numPastMoves() > 0) return;
        ChessPosition leftSpot = new ChessPositionImp(start.getRow() + 1, 1);
        ChessPosition rightSpot = new ChessPositionImp(start.getRow() + 1, 8);
        ChessPiece left = board.getPiece(leftSpot);
        ChessPiece right = board.getPiece(rightSpot);
        if (left != null && left.getPieceType() == ChessPiece.PieceType.ROOK && left.numPastMoves() == 0) {
            boolean free = true;
            for (int i = 2; i <= 4; ++i) {
                ChessPosition blank = new ChessPositionImp(start.getRow() + 1, i);
                if (board.getPiece(blank) != null) free = false;
            }
            if (free) {
                ChessPosition rookSpot = new ChessPositionImp(start.getRow() + 1, 4);
                if (!isThreatened(rookSpot, piece.getTeamColor())) {
                    leftSpot.setPos(start.getRow() + 1, 3);
                    moves.add(new ChessMoveImp(start, leftSpot, null));
                }
            }
        }
        if (right != null && right.getPieceType() == ChessPiece.PieceType.ROOK && right.numPastMoves() == 0) {
            boolean free = true;
            for (int i = 6; i <= 7; ++i) {
                ChessPosition blank = new ChessPositionImp(start.getRow() + 1, i);
                if (board.getPiece(blank) != null) free = false;
            }
            if (free) {
                ChessPosition rookSpot = new ChessPositionImp(start.getRow() + 1, 6);
                if (!isThreatened(rookSpot, piece.getTeamColor())) {
                    rightSpot.setPos(start.getRow() + 1, 7);
                    moves.add(new ChessMoveImp(start, rightSpot, null));
                }
            }
        }
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!move.getStartPosition().onBoard()) throw new InvalidMoveException("you're off the map");
        ChessPiece subject = board.getPiece(move.getStartPosition());
        if (subject.getTeamColor() != turn) throw new InvalidMoveException("not your piece");
        Collection<ChessMove> options = validMoves(move.getStartPosition());
        if (options != null && options.contains(move)) {
            if (subject.getPieceType() == ChessPiece.PieceType.PAWN && move.sideways()
                    && board.getPiece(move.getEndPosition()) == null) {         //fricking en passant
                shootThePawn(move);
            }
            if (subject.getPieceType() == ChessPiece.PieceType.KING && move.leapSideways()) {   //castling
                castleRook(move);
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

    private void castleRook(ChessMove kingsMove) {
        if (kingsMove.getEndPosition().getColumn() == 2) {   //left castle rook move
            ChessPosition rookStart = new ChessPositionImp(kingsMove.getEndPosition().getRow() + 1,1);
            ChessPosition rookEnd = new ChessPositionImp(kingsMove.getEndPosition().getRow() + 1, 4);
            board.movePiece(new ChessMoveImp(rookStart, rookEnd, null));
        }
        else {
            ChessPosition rookStart = new ChessPositionImp(kingsMove.getEndPosition().getRow() + 1,8);
            ChessPosition rookEnd = new ChessPositionImp(kingsMove.getEndPosition().getRow() + 1, 6);
            board.movePiece(new ChessMoveImp(rookStart, rookEnd, null));
        }
    }

    private void shootThePawn(ChessMove pawnAttack) {
        int forward = 1;
        if (turn == TeamColor.BLACK) forward = -1;
        ChessPosition deadPawn = new ChessPositionImp(pawnAttack.getEndPosition().getRow() + 1 - forward, pawnAttack.getEndPosition().getColumn() + 1);
        board.removePiece(deadPawn);
    }

    private void yourTurn() {
        if (turn == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
            state = GameState.BLACK_TURN;
        }
        else {
            turn = TeamColor.WHITE;
            state = GameState.WHITE_TURN;
        }
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

    private boolean isThreatened(ChessPosition victim, TeamColor color) {
        ChessPosition spot = new ChessPositionImp(1,1);
        for (int i = 1; i <= 8; ++i) {
            for (int k = 1; k <= 8; ++k) {
                spot.setPos(i,k);
                ChessPiece kingKiller = board.getPiece(spot);
                if (kingKiller != null && kingKiller.getTeamColor() != color) {
                    for (ChessMove attack : kingKiller.pieceMoves(board, spot)) {
                        if (attack.getEndPosition().equals(victim)) return true;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGameImp that = (ChessGameImp) o;
        return turn == that.turn && board.equals(that.board) && Objects.equals(enPassantAble, that.enPassantAble);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board, enPassantAble);
    }
}
