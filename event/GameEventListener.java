package snake.event;

/**
 * Observer interface for game events.
 *
 * <p>Implementations react to specific {@link GameEvent} types without
 * coupling to the game engine. Examples: score tracking, rendering, sound.</p>
 *
 * <pre>
 * GameEngine fires event
 *   ├─ ScoreTracker   → increments score
 *   ├─ ConsoleRenderer → redraws the board
 *   └─ SoundPlayer    → plays sound effect
 * </pre>
 */
public interface GameEventListener {

    /**
     * Called when a game event occurs.
     *
     * @param event the event that was fired
     */
    void onEvent(GameEvent event);
}
