package chess;

import java.util.Collection;

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
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null || piece.getTeamColor() != turn) return null;
        Collection<ChessMove> ops = piece.pieceMoves(board, startPosition);
        //TODO need to take check and stuff into account, remove invalids
        return ops;
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!move.getStartPosition().onBoard()) throw new InvalidMoveException("There's no piece there");
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
        return false;
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
