package chess;

import java.util.Objects;

public class ChessPositionImp implements ChessPosition{

    private int row;
    private int column;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPositionImp that = (ChessPositionImp) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    public ChessPositionImp(int r, int c) { //counting from one?
        row = r - 1;
        column = c - 1;
    }

    @Override
    public void setPos(int r, int c) {
        row = r - 1;
        column = c - 1;
    }

    @Override
    public void adjust(int vertical, int horizontal) {
        row += vertical;
        column += horizontal;
    }

    @Override
    public boolean onBoard() {
        return (row >= 0 && row <= 7 && column >= 0 && column <= 7);
    }

    @Override
    public ChessPosition copy() {
        return new ChessPositionImp(row + 1, column + 1);
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.valueOf((char) (column + 'a')) + (row + 1);
    }
}
