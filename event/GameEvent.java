package snake.event;

/**
 * Events emitted by the game engine.
 *
 * <p>Part of the <b>Observer Pattern</b> — {@link GameEventListener}
 * implementations react to these events without the engine knowing
 * about specific listeners.</p>
 */
public enum GameEvent {
    /** The game loop has started. */
    GAME_STARTED,

    /** The snake ate food — score and length increased. */
    FOOD_EATEN,

    /** The snake hit a wall — game over. */
    COLLISION_WALL,

    /** The snake hit itself — game over. */
    COLLISION_SELF,

    /** A normal game tick completed (snake moved). */
    GAME_TICK
}
