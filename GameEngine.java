package snake;

import snake.event.GameEvent;
import snake.event.GameEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Central game orchestrator — owns the game loop, checks collisions,
 * and publishes events to observers.
 *
 * <h3>Design Patterns</h3>
 * <ul>
 *   <li><b>Observer</b> — listeners are notified of game events
 *       (food eaten, collision, tick) without coupling</li>
 *   <li><b>State</b> — behaviour changes based on {@link GameState}
 *       (e.g., direction changes are rejected when not PLAYING)</li>
 * </ul>
 *
 * <h3>Thread Safety</h3>
 * <p>The game loop runs in a dedicated daemon thread. User input
 * arrives from a different thread via {@link #changeDirection}.
 * Direction is stored as {@code volatile} in {@link Snake}, so
 * no explicit synchronization is needed for this single-field update.</p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>{@link Snake} — body management (Deque + HashSet), grow, move</li>
 *   <li>{@link Board} — grid boundaries, food placement</li>
 *   <li>{@code GameEngine} — orchestration only: tick sequencing,
 *       collision detection, event dispatch</li>
 * </ul>
 */
public class GameEngine {

    private final Board board;
    private final Snake snake;
    private volatile GameState state;
    private final List<GameEventListener> listeners;
    private final int tickDelayMs;
    private final CountDownLatch gameOverLatch;
    private int score;

    /**
     * Creates a new game with the snake starting at the center of the board.
     *
     * @param rows        board height
     * @param cols        board width
     * @param tickDelayMs delay between ticks in milliseconds (controls speed)
     */
    public GameEngine(int rows, int cols, int tickDelayMs) {
        this.board = new Board(rows, cols);
        Cell startCell = new Cell(rows / 2, cols / 2);
        this.snake = new Snake(startCell, Direction.RIGHT);
        this.state = GameState.PLAYING;
        this.listeners = new ArrayList<>();
        this.tickDelayMs = tickDelayMs;
        this.gameOverLatch = new CountDownLatch(1);
        this.score = 0;
        board.spawnFood(snake);
    }

    // ──────────────────── Observer management ────────────────────

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    private void publishEvent(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    // ──────────────────── User input ────────────────────

    /**
     * Changes the snake's direction. Only accepted when the game is PLAYING.
     * Called from the input thread — thread-safe via volatile direction in Snake.
     *
     * @param direction the new direction
     */
    public void changeDirection(Direction direction) {
        if (state == GameState.PLAYING) {
            snake.setDirection(direction);
        }
    }

    // ──────────────────── Game loop ────────────────────

    /**
     * Executes a single game tick:
     * <ol>
     *   <li>Compute next head position</li>
     *   <li>Check wall collision → GAME_OVER</li>
     *   <li>Check self collision → GAME_OVER</li>
     *   <li>If food: grow + respawn food + score++</li>
     *   <li>Else: move forward (add head, remove tail)</li>
     *   <li>Publish tick event</li>
     * </ol>
     *
     * @return {@code true} if the game is still running
     */
    public boolean tick() {
        if (state != GameState.PLAYING) {
            return false;
        }

        Cell nextHead = snake.computeNextHead();

        // Wall collision
        if (board.isOutOfBounds(nextHead)) {
            state = GameState.GAME_OVER;
            publishEvent(GameEvent.COLLISION_WALL);
            gameOverLatch.countDown();
            return false;
        }

        // Self collision — O(1) via HashSet
        if (snake.occupies(nextHead)) {
            state = GameState.GAME_OVER;
            publishEvent(GameEvent.COLLISION_SELF);
            gameOverLatch.countDown();
            return false;
        }

        // Food eaten
        if (board.hasFood(nextHead)) {
            snake.grow(nextHead);
            score++;
            board.spawnFood(snake);
            publishEvent(GameEvent.FOOD_EATEN);
        } else {
            // Normal move — add head, remove tail
            snake.moveForward(nextHead);
        }

        publishEvent(GameEvent.GAME_TICK);
        return true;
    }

    /**
     * Starts the game loop in a dedicated daemon thread.
     * The loop calls {@link #tick()} at regular intervals defined by {@code tickDelayMs}.
     */
    public void start() {
        publishEvent(GameEvent.GAME_STARTED);

        Thread gameThread = new Thread(() -> {
            while (tick()) {
                try {
                    Thread.sleep(tickDelayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "snake-game-loop");

        gameThread.setDaemon(true);
        gameThread.start();
    }

    /**
     * Blocks the calling thread until the game is over and all
     * game-over events have been published to listeners.
     *
     * <p>Solves the race condition where the main thread could
     * exit before the game thread publishes the final event.</p>
     */
    public void awaitGameOver() throws InterruptedException {
        gameOverLatch.await();
    }

    // ──────────────────── Accessors ────────────────────

    public Snake getSnake() {
        return snake;
    }

    public Board getBoard() {
        return board;
    }

    public GameState getState() {
        return state;
    }

    public int getScore() {
        return score;
    }
}
