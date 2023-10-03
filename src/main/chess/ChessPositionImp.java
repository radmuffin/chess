package chess;

public class ChessPositionImp implements ChessPosition{

    private int row;
    private int column;


    public ChessPositionImp(int r, int c) {
        row = r - 1;
        column = c - 1;
    }

    public void setPos(int r, int c) {
        row = r - 1;
        column = c - 1;
    }

    @Override
    public int getRow() {
        return row + 1;
    }

    @Override
    public int getColumn() {
        return column + 1;
    }
}
