package snake;

/**
 * Movement directions with row/column deltas.
 *
 * <p>Each direction stores a (dr, dc) pair so movement computation
 * is a simple addition: {@code newRow = row + dr, newCol = col + dc}.</p>
 */
public enum Direction {

    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    public final int dr;
    public final int dc;

    Direction(int dr, int dc) {
        this.dr = dr;
        this.dc = dc;
    }

    /**
     * Checks if this direction is directly opposite to the given one.
     * Used to prevent 180-degree turns (instant self-collision).
     *
     * @param other the direction to compare against
     * @return {@code true} if the directions are opposite
     */
    public boolean isOpposite(Direction other) {
        return this.dr + other.dr == 0 && this.dc + other.dc == 0;
    }
}
