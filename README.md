# Tetris Game - Java Edition

A feature-rich Tetris implementation in Java with JavaFX, including single-player, two-player modes, and competitive features.

## Features

### Core Gameplay
- All 7 classic Tetromino pieces (I, O, T, S, Z, J, L)
- Smooth piece movement and rotation with wall-kick support
- Ghost piece preview showing drop location
- Hard drop and soft drop mechanics
- Hold piece functionality

### Game Modes
- **Single Player**: Classic Tetris experience with progressive difficulty
- **Two Player**: Competitive side-by-side gameplay

### Progression System
- **Scoring**: Points awarded for line clears (1-4 lines) with level multipliers
  - Single: 100 × level
  - Double: 300 × level
  - Triple: 500 × level
  - Tetris: 800 × level
- **Levels**: Difficulty increases every 10 lines cleared
- **Speed**: Game speed increases with each level

### Additional Features
- **Next Piece Preview**: See what's coming next
- **Leaderboard**: Top 10 scores saved with timestamps
- **Visual Polish**: Colorful pieces with 3D-style shading
- **Pause Functionality**: Pause/resume gameplay
- **Clean UI**: Modern dark theme interface

## Controls

### Single Player Mode
- **← →**: Move piece left/right
- **↓**: Soft drop (move down faster)
- **↑**: Rotate piece clockwise
- **SPACE**: Hard drop (instant drop)
- **C**: Hold current piece
- **P**: Pause game
- **ESC**: Return to menu

### Two Player Mode

#### Player 1 (Left Side - WASD)
- **A / D**: Move piece left/right
- **S**: Soft drop
- **W**: Rotate piece
- **Q**: Hold current piece
- **Shift + Space**: Hard drop

#### Player 2 (Right Side - Arrow Keys)
- **← / →**: Move piece left/right
- **↓**: Soft drop
- **↑**: Rotate piece
- **SHIFT**: Hold current piece
- **ENTER**: Hard drop

#### Both Players
- **P**: Pause game
- **ESC**: Return to menu

## Requirements

- Java JDK 11 or higher
- Apache Maven 3.6 or higher

## Installation & Setup

### Quick Start with Maven

1. **Clone or download the project**

2. **Compile the project**:
   ```bash
   mvn clean compile
   ```

3. **Run the game**:
   ```bash
   mvn javafx:run
   ```

### Building an Executable JAR

To create a standalone JAR file:

```bash
mvn clean package
```

This will create a JAR file in the `target/` directory. Note: Due to JavaFX module requirements, you'll need to run it with:

```bash
java -jar target/tetris-1.0.0.jar
```

### Maven Commands Reference

- **Compile**: `mvn compile`
- **Run**: `mvn javafx:run`
- **Package**: `mvn package`
- **Clean**: `mvn clean`
- **Clean + Compile + Run**: `mvn clean javafx:run`

## Project Structure

```
tetris/
├── pom.xml                                 # Maven configuration
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── vajradevam/
│                   └── tetris/
│                       ├── TetrisGame.java          # Main application
│                       ├── Tetromino.java           # Piece definitions
│                       ├── GameBoard.java           # Game logic
│                       ├── GamePanel.java           # Single player UI
│                       ├── TwoPlayerPanel.java      # Two player UI
│                       ├── LeaderboardManager.java  # Score persistence
│                       └── LeaderboardPanel.java    # Leaderboard UI
└── tetris_leaderboard.dat                 # Saved scores (auto-generated)
```

## Gameplay Tips

1. **Use Ghost Pieces**: The transparent preview shows where your piece will land
2. **Hold Strategy**: Save pieces for later to set up better plays
3. **Hard Drop**: Use SPACE for instant placement and quick scoring
4. **Level Up**: Clear 10 lines to advance to the next level
5. **Tetris Bonus**: Clear 4 lines at once for maximum points!
6. **Two Player**: Outlast your opponent by surviving longer

## Scoring Strategy

- Focus on setting up Tetris (4-line clears) for maximum points
- Higher levels give more points per line cleared
- Hard drops give bonus points (2 points per row)
- Use the hold feature strategically to set up combos

## Troubleshooting

### Maven build errors
- Ensure you have Maven installed: `mvn --version`
- Verify Java version is 11 or higher: `java -version`
- Try cleaning the project: `mvn clean`

### Game doesn't start
- Make sure you're using `mvn javafx:run` (not just `mvn run`)
- Check that all dependencies downloaded correctly
- Try: `mvn clean install` then `mvn javafx:run`

### Controls not working
- Make sure the game window has focus (click on it)
- Check that you're using the correct keys for your player

## Development

### Maven Dependencies

The project uses the following dependencies:
- **JavaFX Controls** (21.0.1) - UI framework
- **JavaFX Graphics** (21.0.1) - Graphics rendering
- **JavaFX Base** (21.0.1) - Core JavaFX functionality

All dependencies are managed automatically by Maven.

### Building from Source

```bash
# Clone the repository
git clone <repository-url>
cd tetris

# Build and run
mvn clean javafx:run
```

## Credits

**Group ID**: org.vajradevam
**Artifact ID**: tetris
**Version**: 1.0.0

Built with Java and JavaFX using Maven. Includes classic Tetris gameplay mechanics with modern enhancements.

Enjoy the game!
