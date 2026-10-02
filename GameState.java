package snake;

/**
 * Game states — used with the <b>State Pattern</b>.
 *
 * <p>Input handling behaviour changes based on the current state:
 * direction changes are only accepted in {@code PLAYING},
 * and the game loop exits on {@code GAME_OVER}.</p>
 */
public enum GameState {
    PLAYING,
    PAUSED,
    GAME_OVER
}
