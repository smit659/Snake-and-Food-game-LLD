package snake;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * The snake entity — owns its body and knows how to move.
 *
 * <h3>Data Structures</h3>
 * <ul>
 *   <li>{@link Deque} — ordered body cells (head at front, tail at back).
 *       Gives O(1) addFirst / removeLast for movement.</li>
 *   <li>{@link HashSet} — mirrors the Deque for O(1) {@code occupies()} checks
 *       used in self-collision detection.</li>
 * </ul>
 *
 * <h3>Thread Safety</h3>
 * <p>The {@code direction} field is {@code volatile} so the input thread
 * can write it while the game-loop thread reads it safely.</p>
 */
public class Snake {

    private final Deque<Cell> body;
    private final Set<Cell> bodySet;
    private volatile Direction direction;

    /**
     * Creates a snake with a single-cell body at the given position.
     *
     * @param initialHead      starting cell
     * @param initialDirection initial movement direction
     */
    public Snake(Cell initialHead, Direction initialDirection) {
        this.body = new ArrayDeque<>();
        this.bodySet = new HashSet<>();
        this.direction = initialDirection;
        body.addFirst(initialHead);
        bodySet.add(initialHead);
    }

    /** Returns the cell at the front of the snake. */
    public Cell getHead() {
        return body.peekFirst();
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * Changes direction, rejecting 180-degree turns.
     *
     * @param newDirection the requested direction
     */
    public void setDirection(Direction newDirection) {
        if (direction.isOpposite(newDirection)) {
            return;
        }
        this.direction = newDirection;
    }

    /**
     * Computes where the head will be on the next tick
     * based on the current direction.
     */
    public Cell computeNextHead() {
        Cell head = getHead();
        return new Cell(head.row + direction.dr, head.col + direction.dc);
    }

    /**
     * Grows the snake by one cell (food eaten).
     * Adds the new head without removing the tail.
     *
     * @param newHead the cell to become the new head
     */
    public void grow(Cell newHead) {
        body.addFirst(newHead);
        bodySet.add(newHead);
    }

    /**
     * Moves the snake forward by one cell (no food).
     * Adds the new head and removes the tail.
     *
     * @param newHead the cell to become the new head
     * @return the removed tail cell
     */
    public Cell moveForward(Cell newHead) {
        body.addFirst(newHead);
        bodySet.add(newHead);
        Cell removedTail = body.removeLast();
        bodySet.remove(removedTail);
        return removedTail;
    }

    /**
     * O(1) check — is this cell part of the snake's body?
     * Used for self-collision detection.
     */
    public boolean occupies(Cell cell) {
        return bodySet.contains(cell);
    }

    /** Current length of the snake (starts at 1). */
    public int length() {
        return body.size();
    }

    /** Returns the body for rendering. Head is first, tail is last. */
    public Deque<Cell> getBody() {
        return body;
    }
}
