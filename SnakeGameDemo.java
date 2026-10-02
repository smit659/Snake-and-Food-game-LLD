package snake;

import snake.event.ConsoleRenderer;
import snake.event.ScoreTracker;

import java.util.Scanner;

/**
 * Entry point for the Snake Game.
 *
 * <p>
 * Runs a 10×10 board with a console renderer and score tracker.
 * Input is accepted via stdin: W/A/S/D + Enter to change direction.
 * </p>
 *
 * <h3>Architecture Summary</h3>
 * 
 * <pre>
 *   User Input (stdin)
 *       │
 *       ▼
 *   GameEngine  ──► tick() ──► Snake.moveForward() / grow()
 *       │                      Board.spawnFood()
 *       │
 *       ▼ publishEvent()
 *   ┌───────────────────┐
 *   │  ScoreTracker      │  → tracks score
 *   │  ConsoleRenderer   │  → renders board
 *   └───────────────────┘
 * </pre>
 *
 * <h3>Design Patterns Used</h3>
 * <ul>
 * <li><b>Observer</b> — GameEngine publishes events; ScoreTracker,
 * ConsoleRenderer listen</li>
 * <li><b>State</b> — GameState enum (PLAYING, PAUSED, GAME_OVER) changes input
 * handling</li>
 * <li><b>SRP</b> — Snake owns its body, Board owns food, GameEngine
 * orchestrates</li>
 * </ul>
 */
public class SnakeGameDemo {

    public static void main(String[] args) {
        System.out.println("=== Snake Game ===");
        System.out.println("Controls: W=UP, S=DOWN, A=LEFT, D=RIGHT (then Enter)");
        System.out.println("Starting in 1 second...\n");

        // --- Setup ---
        int rows = 10;
        int cols = 10;
        int tickDelayMs = 500;

        GameEngine engine = new GameEngine(rows, cols, tickDelayMs);

        // Register observers
        ScoreTracker scoreTracker = new ScoreTracker();
        ConsoleRenderer renderer = new ConsoleRenderer(engine);
        engine.addListener(scoreTracker);
        engine.addListener(renderer);

        // --- Start game loop (daemon thread) ---
        engine.start();

        // --- Input loop (main thread) ---
        Scanner scanner = new Scanner(System.in);
        while (engine.getState() == GameState.PLAYING) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim().toUpperCase();
                switch (input) {
                    case "W":
                        engine.changeDirection(Direction.UP);
                        break;
                    case "S":
                        engine.changeDirection(Direction.DOWN);
                        break;
                    case "A":
                        engine.changeDirection(Direction.LEFT);
                        break;
                    case "D":
                        engine.changeDirection(Direction.RIGHT);
                        break;
                    default:
                        System.out.println("Invalid input. Use W/A/S/D.");
                        break;
                }
            }
        }

        // Wait for the game thread to finish publishing all events
        // (fixes race: main could exit before COLLISION event is rendered)
        try {
            engine.awaitGameOver();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // --- Game over ---
        System.out.println("\nFinal Score: " + scoreTracker.getScore());
        System.out.println("Snake Length: " + engine.getSnake().length());
        scanner.close();
    }
}

