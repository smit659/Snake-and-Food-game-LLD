# Snake Game — Low-Level Design

A classic **Snake Game** built in Java, demonstrating clean LLD principles — no external libraries.

## Architecture

```
User Input (stdin)
    │
    ▼
GameEngine  ──► tick() ──► Snake.moveForward() / grow()
    │                       Board.spawnFood()
    │
    ▼ publishEvent()
┌─────────────────────┐
│  ScoreTracker        │  → tracks score
│  ConsoleRenderer     │  → renders board to console
└─────────────────────┘
```

## Design Patterns

| Pattern | Where | Purpose |
|---------|-------|---------|
| **Observer** | `GameEventListener` + `ScoreTracker`, `ConsoleRenderer` | Decouple game logic from rendering, scoring, sound |
| **State** | `GameState` enum (`PLAYING`, `PAUSED`, `GAME_OVER`) | Input handling changes based on game state |
| **SRP** | `Snake` owns body, `Board` owns food, `GameEngine` orchestrates | Each class has a single clear responsibility |

## Key Data Structures

### Snake Body — `Deque<Cell>` + `HashSet<Cell>`

| Operation | Data Structure | Complexity |
|-----------|---------------|------------|
| Add new head | `Deque.addFirst()` | O(1) |
| Remove tail | `Deque.removeLast()` | O(1) |
| Self-collision check | `HashSet.contains()` | O(1) |

## Project Structure

```
snake/
├── Cell.java               # Board position (row, col) with equals/hashCode
├── Direction.java           # Enum with (dr, dc) deltas + isOpposite()
├── Snake.java               # Body management (Deque + HashSet)
├── Board.java               # Grid boundaries + food placement
├── GameState.java           # State pattern enum
├── GameEngine.java          # Game loop + collision + event dispatch
├── SnakeGameDemo.java       # Entry point with interactive input
└── event/
    ├── GameEvent.java       # Event types enum
    ├── GameEventListener.java  # Observer interface
    ├── ScoreTracker.java    # Score observer
    └── ConsoleRenderer.java # Rendering observer
```

## How to Run

```bash
# Compile
javac -d out snake/event/*.java snake/*.java

# Run
java -cp out snake.SnakeGameDemo
```

Controls: `W`=UP, `S`=DOWN, `A`=LEFT, `D`=RIGHT (then Enter)

## Threading Model

```
Main Thread          Game Loop Thread
    │                     │
    │ scanner.nextLine()  │ while (tick()) {
    │ changeDirection()──►│   computeNextHead()
    │                     │   checkCollisions()
    │                     │   snake.grow/moveForward()
    │                     │   publishEvent()
    │                     │   Thread.sleep(500ms)
    │                     │ }
```

- **Game loop** runs in a daemon thread, ticks at configurable intervals
- **User input** runs on the main thread, writes to `volatile direction`
- No explicit locking needed — single volatile field for cross-thread communication

## How a Single Tick Works

```
tick()
  │
  ├─ nextHead = snake.computeNextHead()
  │
  ├─ board.isOutOfBounds(nextHead)?  ──► GAME_OVER (wall)
  │
  ├─ snake.occupies(nextHead)?       ──► GAME_OVER (self)  [O(1) via HashSet]
  │
  ├─ board.hasFood(nextHead)?
  │    ├─ YES: snake.grow(nextHead)
  │    │       board.spawnFood(snake)
  │    │       publish(FOOD_EATEN)
  │    └─ NO:  snake.moveForward(nextHead)
  │
  └─ publish(GAME_TICK)
```

## Extending the System

**Add a new observer** (e.g., sound effects):
```java
public class SoundPlayer implements GameEventListener {
    @Override
    public void onEvent(GameEvent event) {
        if (event == GameEvent.FOOD_EATEN) {
            playSound("crunch.wav");
        }
    }
}

engine.addListener(new SoundPlayer());
```

**Add new game events** — just add to `GameEvent` enum and publish from `GameEngine`.

## License

MIT
