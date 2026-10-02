package snake.event;

import snake.Board;
import snake.Cell;
import snake.GameEngine;
import snake.Snake;

/**
 * Observer that renders the game board to the console after each tick.
 *
 * <p>Prints a text-based grid showing the snake head, body, food,
 * and current score/length.</p>
 */
public class ConsoleRenderer implements GameEventListener {

    private final GameEngine engine;

    public ConsoleRenderer(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onEvent(GameEvent event) {
        switch (event) {
            case GAME_STARTED:
            case GAME_TICK:
            case FOOD_EATEN:
                render();
                break;
            case COLLISION_WALL:
                render();
                System.out.println(">> GAME OVER — Hit the wall!");
                break;
            case COLLISION_SELF:
                render();
                System.out.println(">> GAME OVER — Hit yourself!");
                break;
        }
    }

    /**
     * Renders the board as a text grid.
     *
     * <pre>
     * ┌──────────┐
     * │          │
     * │  ■■▓▓▓▓  │
     * │      @@  │
     * │          │
     * └──────────┘
     * </pre>
     */
    private void render() {
        Board board = engine.getBoard();
        Snake snake = engine.getSnake();

        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        // Top border
        sb.append("+");
        for (int c = 0; c < board.getCols(); c++) sb.append("--");
        sb.append("+\n");

        // Board rows
        for (int r = 0; r < board.getRows(); r++) {
            sb.append("|");
            for (int c = 0; c < board.getCols(); c++) {
                Cell cell = new Cell(r, c);
                if (cell.equals(snake.getHead())) {
                    sb.append("HH");  // Head
                } else if (snake.occupies(cell)) {
                    sb.append("##");  // Body
                } else if (board.hasFood(cell)) {
                    sb.append("@@");  // Food
                } else {
                    sb.append("  ");  // Empty
                }
            }
            sb.append("|\n");
        }

        // Bottom border
        sb.append("+");
        for (int c = 0; c < board.getCols(); c++) sb.append("--");
        sb.append("+\n");

        sb.append("Length: ").append(snake.length())
          .append(" | Direction: ").append(snake.getDirection())
          .append("\n");

        System.out.print(sb);
    }
}
