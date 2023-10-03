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
}
