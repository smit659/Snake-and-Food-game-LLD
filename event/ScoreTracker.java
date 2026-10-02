package snake.event;

/**
 * Observer that tracks the player's score.
 *
 * <p>Increments on {@link GameEvent#FOOD_EATEN} and logs the update.
 * Decoupled from the game engine — just listens and reacts.</p>
 */
public class ScoreTracker implements GameEventListener {

    private int score;

    @Override
    public void onEvent(GameEvent event) {
        if (event == GameEvent.FOOD_EATEN) {
            score++;
            System.out.println("[ScoreTracker] Score: " + score);
        }
    }

    public int getScore() {
        return score;
    }
}
