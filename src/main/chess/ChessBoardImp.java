package chess;

public class ChessBoardImp implements ChessBoard{

    private ChessPiece[][] board;

    public ChessBoardImp() {
        board = new ChessPiece[8][8];
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()][position.getColumn()] = piece;
    }

    @Override
    public void movePiece(ChessMove move) {
        ChessPiece piece = getPiece(move.getStartPosition());
        board[move.getStartPosition().getRow()][move.getStartPosition().getColumn()] = null;
        if (move.getPromotionPiece() != null) {
            switch (move.getPromotionPiece()) {
                case QUEEN -> piece = new Queen(piece.getTeamColor());
                case BISHOP -> piece = new Bishop(piece.getTeamColor());
                case KNIGHT -> piece = new Knight(piece.getTeamColor());
                case ROOK -> piece = new Rook(piece.getTeamColor());
            }
        }
        board[move.getEndPosition().getRow()][move.getEndPosition().getColumn()] = piece;
    }


    @Override
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()][position.getColumn()];
    }

    @Override
    public void resetBoard() {
        for (int i = 0; i < 8; ++i) { //pawns first
            ChessPiece w = new Pawn(ChessGame.TeamColor.WHITE);
            ChessPiece b = new Pawn(ChessGame.TeamColor.BLACK);
            board[1][i] = w;
            board[6][i] = b;
        }
        for (int i = 2; i <= 5; ++i) {   //empty spots
            for (int k = 0; k < 8; ++k) {
                board[i][k] = null;
            }
        }
        board[0][0] = new Rook(ChessGame.TeamColor.WHITE);      //ugly hardcoding goooo
        board[0][7] = new Rook(ChessGame.TeamColor.WHITE);
        board[7][0] = new Rook(ChessGame.TeamColor.BLACK);
        board[7][7] = new Rook(ChessGame.TeamColor.BLACK);

        board[0][1] = new Knight(ChessGame.TeamColor.WHITE);
        board[0][6] = new Knight(ChessGame.TeamColor.WHITE);
        board[7][1] = new Knight(ChessGame.TeamColor.BLACK);
        board[7][6] = new Knight(ChessGame.TeamColor.BLACK);

        board[0][2] = new Bishop(ChessGame.TeamColor.WHITE);
        board[0][5] = new Bishop(ChessGame.TeamColor.WHITE);
        board[7][2] = new Bishop(ChessGame.TeamColor.BLACK);
        board[7][5] = new Bishop(ChessGame.TeamColor.BLACK);

        board[0][3] = new Queen(ChessGame.TeamColor.WHITE);
        board[7][3] = new Queen(ChessGame.TeamColor.BLACK);

        board[0][4] = new King(ChessGame.TeamColor.WHITE);
        board[7][4] = new King(ChessGame.TeamColor.BLACK);
    }


    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 7; i >= 0; --i) {
            out.append("|");
            for (int k = 0; k <= 7; ++k) {
                if (board[i][k] != null) out.append(board[i][k].toString());
                else out.append(" ");
                out.append("|");
            }
            out.append('\n');
        }
        return out.toString();
    }

    @Override
    public ChessBoard copy() {
        ChessBoard copy = new ChessBoardImp();
        ChessPosition spot = new ChessPositionImp(1,1);
        for (int i = 1; i <= 8; ++i) {
            for (int k = 1; k <= 8; ++k) {
                spot.setPos(i,k);
                ChessPiece piece = getPiece(spot);
                if (piece != null) piece = piece.copy();
                copy.addPiece(spot, piece);
            }
        }
        return copy;
    }

}
