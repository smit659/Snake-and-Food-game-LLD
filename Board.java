package snake;

import java.util.Random;

/**
 * The game board — a fixed-size grid that manages food placement.
 *
 * <p>Food spawns at a random empty cell (not occupied by the snake).
 * There is always exactly one food item on the board.</p>
 */
public class Board {

    private final int rows;
    private final int cols;
    private Cell food;
    private final Random random;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.random = new Random();
    }

    /**
     * Spawns food at a random cell not occupied by the snake.
     *
     * <p>Uses a while-loop retry. If the snake fills most of the board,
     * this degrades — a production version could maintain a set of empty
     * cells for O(1) placement.</p>
     *
     * @param snake the current snake (to avoid placing food on it)
     */
    public void spawnFood(Snake snake) {
        Cell candidate;
        do {
            candidate = new Cell(random.nextInt(rows), random.nextInt(cols));
        } while (snake.occupies(candidate));
        this.food = candidate;
    }

    /** Checks if the given cell contains food. */
    public boolean hasFood(Cell cell) {
        return food != null && food.equals(cell);
    }

    /** Returns the current food position. */
    public Cell getFood() {
        return food;
    }

    /** Checks if a cell is outside the board boundaries. */
    public boolean isOutOfBounds(Cell cell) {
        return cell.row < 0 || cell.row >= rows
            || cell.col < 0 || cell.col >= cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
