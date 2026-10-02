package snake;

import java.util.Objects;

/**
 * Represents a single position on the game board.
 *
 * <p>{@code equals()} and {@code hashCode()} are implemented based on
 * (row, col) so that cells can be stored in a {@link java.util.HashSet}
 * for O(1) collision detection.</p>
 */
public class Cell {

    public final int row;
    public final int col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
