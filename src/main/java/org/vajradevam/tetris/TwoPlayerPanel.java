package org.vajradevam.tetris;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashSet;
import java.util.Set;

public class TwoPlayerPanel extends BorderPane {
    private static final int CELL_SIZE = 25;

    private GameBoard player1Board;
    private GameBoard player2Board;

    private Canvas player1Canvas;
    private Canvas player2Canvas;
    private Canvas p1NextCanvas;
    private Canvas p2NextCanvas;
    private Canvas p1HeldCanvas;
    private Canvas p2HeldCanvas;

    private GraphicsContext p1gc;
    private GraphicsContext p2gc;
    private GraphicsContext p1NextGc;
    private GraphicsContext p2NextGc;
    private GraphicsContext p1HeldGc;
    private GraphicsContext p2HeldGc;

    private Label p1ScoreLabel;
    private Label p2ScoreLabel;
    private Label p1LevelLabel;
    private Label p2LevelLabel;

    private AnimationTimer gameLoop;
    private long lastP1DropTime;
    private long lastP2DropTime;

    private boolean player1GameOver;
    private boolean player2GameOver;
    private boolean isPaused;

    private TetrisGame mainApp;
    private Set<KeyCode> activeKeys;

    public TwoPlayerPanel(TetrisGame mainApp) {
        this.mainApp = mainApp;
        this.player1GameOver = false;
        this.player2GameOver = false;
        this.isPaused = false;
        this.activeKeys = new HashSet<>();

        player1Board = new GameBoard();
        player2Board = new GameBoard();

        setStyle("-fx-background-color: #1a1a2e;");

        setupUI();
        setupControls();
    }

    private void setupUI() {
        HBox centerBox = new HBox(40);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        // Player 1 section
        VBox player1Section = createPlayerSection("PLAYER 1 (WASD)", true);

        // Player 2 section
        VBox player2Section = createPlayerSection("PLAYER 2 (ARROWS)", false);

        centerBox.getChildren().addAll(player1Section, player2Section);
        setCenter(centerBox);

        // Top info
        Label pauseInfo = new Label("Press P to Pause | ESC for Menu");
        pauseInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaa;");
        StackPane topPane = new StackPane(pauseInfo);
        topPane.setPadding(new Insets(10));
        setTop(topPane);
    }

    private VBox createPlayerSection(String playerName, boolean isPlayer1) {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(playerName);
        nameLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #00ff88; -fx-font-weight: bold;");

        HBox gameArea = new HBox(10);
        gameArea.setAlignment(Pos.CENTER);

        // Left info panel
        VBox leftPanel = new VBox(10);
        leftPanel.setAlignment(Pos.CENTER);

        VBox heldBox = createSmallInfoBox("HOLD");
        Canvas heldCanvas = new Canvas(80, 80);
        heldBox.getChildren().add(heldCanvas);

        VBox scoreBox = createSmallInfoBox("SCORE");
        Label scoreLabel = new Label("0");
        scoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #00ff88; -fx-font-weight: bold;");
        scoreBox.getChildren().add(scoreLabel);

        VBox levelBox = createSmallInfoBox("LEVEL");
        Label levelLabel = new Label("1");
        levelLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #00ff88; -fx-font-weight: bold;");
        levelBox.getChildren().add(levelLabel);

        leftPanel.getChildren().addAll(heldBox, scoreBox, levelBox);

        // Main canvas
        Canvas canvas = new Canvas(GameBoard.getCols() * CELL_SIZE, GameBoard.getRows() * CELL_SIZE);
        StackPane canvasPane = new StackPane(canvas);
        canvasPane.setStyle("-fx-background-color: #16213e; -fx-padding: 5;");

        // Right panel
        VBox rightPanel = new VBox(10);
        rightPanel.setAlignment(Pos.CENTER);

        VBox nextBox = createSmallInfoBox("NEXT");
        Canvas nextCanvas = new Canvas(80, 80);
        nextBox.getChildren().add(nextCanvas);

        VBox controlsBox = createSmallInfoBox("KEYS");
        String controls = isPlayer1 ? "A/D Move\nS Drop\nW Rotate\nQ Hold" : "← / → Move\n↓ Drop\n↑ Rotate\nShift Hold";
        Label controlsLabel = new Label(controls);
        controlsLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #ccc;");
        controlsBox.getChildren().add(controlsLabel);

        rightPanel.getChildren().addAll(nextBox, controlsBox);

        gameArea.getChildren().addAll(leftPanel, canvasPane, rightPanel);

        section.getChildren().addAll(nameLabel, gameArea);

        if (isPlayer1) {
            player1Canvas = canvas;
            p1gc = canvas.getGraphicsContext2D();
            p1NextCanvas = nextCanvas;
            p1NextGc = nextCanvas.getGraphicsContext2D();
            p1HeldCanvas = heldCanvas;
            p1HeldGc = heldCanvas.getGraphicsContext2D();
            p1ScoreLabel = scoreLabel;
            p1LevelLabel = levelLabel;
        } else {
            player2Canvas = canvas;
            p2gc = canvas.getGraphicsContext2D();
            p2NextCanvas = nextCanvas;
            p2NextGc = nextCanvas.getGraphicsContext2D();
            p2HeldCanvas = heldCanvas;
            p2HeldGc = heldCanvas.getGraphicsContext2D();
            p2ScoreLabel = scoreLabel;
            p2LevelLabel = levelLabel;
        }

        return section;
    }

    private VBox createSmallInfoBox(String title) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #0f3460; -fx-padding: 8; -fx-background-radius: 5;");
        box.setPrefWidth(100);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-font-weight: bold;");
        box.getChildren().add(titleLabel);

        return box;
    }

    private void setupControls() {
        setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            activeKeys.add(code);

            if (code == KeyCode.ESCAPE) {
                stopGame();
                mainApp.returnToMenu();
                return;
            }

            if (code == KeyCode.P) {
                togglePause();
                return;
            }

            if (isPaused) return;

            // Player 1 controls (WASD + Q for hold)
            if (!player1GameOver) {
                switch (code) {
                    case A:
                        player1Board.moveLeft();
                        break;
                    case D:
                        player1Board.moveRight();
                        break;
                    case S:
                        player1Board.moveDown();
                        break;
                    case W:
                        player1Board.rotate();
                        break;
                    case Q:
                        player1Board.holdPiece();
                        break;
                    case SPACE:
                        if (event.isShiftDown()) {
                            // Player 1 hard drop with Shift+Space
                            player1Board.hardDrop();
                        }
                        break;
                }
            }

            // Player 2 controls (Arrow keys + Shift for hold)
            if (!player2GameOver) {
                switch (code) {
                    case LEFT:
                        player2Board.moveLeft();
                        break;
                    case RIGHT:
                        player2Board.moveRight();
                        break;
                    case DOWN:
                        player2Board.moveDown();
                        break;
                    case UP:
                        player2Board.rotate();
                        break;
                    case SHIFT:
                        player2Board.holdPiece();
                        break;
                    case ENTER:
                        // Player 2 hard drop
                        player2Board.hardDrop();
                        break;
                }
            }

            render();
        });

        setOnKeyReleased(event -> {
            activeKeys.remove(event.getCode());
        });
    }

    public void startGame() {
        lastP1DropTime = System.nanoTime();
        lastP2DropTime = System.nanoTime();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isPaused) return;

                // Player 1 auto-drop
                if (!player1GameOver) {
                    long p1DropInterval = (long) (1_000_000_000L / (1 + player1Board.getLevel() * 0.5));
                    if (now - lastP1DropTime > p1DropInterval) {
                        if (!player1Board.moveDown()) {
                            if (player1Board.isGameOver()) {
                                player1GameOver = true;
                                checkGameEnd();
                            }
                        }
                        lastP1DropTime = now;
                    }
                }

                // Player 2 auto-drop
                if (!player2GameOver) {
                    long p2DropInterval = (long) (1_000_000_000L / (1 + player2Board.getLevel() * 0.5));
                    if (now - lastP2DropTime > p2DropInterval) {
                        if (!player2Board.moveDown()) {
                            if (player2Board.isGameOver()) {
                                player2GameOver = true;
                                checkGameEnd();
                            }
                        }
                        lastP2DropTime = now;
                    }
                }

                render();
            }
        };

        gameLoop.start();
    }

    private void togglePause() {
        isPaused = !isPaused;
    }

    private void render() {
        renderPlayer(p1gc, player1Board, player1Canvas, !player1GameOver);
        renderPlayer(p2gc, player2Board, player2Canvas, !player2GameOver);

        drawPreviewPiece(p1NextGc, player1Board.getNextPiece(), p1NextCanvas);
        drawPreviewPiece(p2NextGc, player2Board.getNextPiece(), p2NextCanvas);

        drawPreviewPiece(p1HeldGc, player1Board.getHeldPiece(), p1HeldCanvas);
        drawPreviewPiece(p2HeldGc, player2Board.getHeldPiece(), p2HeldCanvas);

        p1ScoreLabel.setText(String.valueOf(player1Board.getScore()));
        p1LevelLabel.setText(String.valueOf(player1Board.getLevel()));

        p2ScoreLabel.setText(String.valueOf(player2Board.getScore()));
        p2LevelLabel.setText(String.valueOf(player2Board.getLevel()));
    }

    private void renderPlayer(GraphicsContext gc, GameBoard board, Canvas canvas, boolean isActive) {
        // Clear canvas
        gc.setFill(isActive ? Color.rgb(22, 33, 62) : Color.rgb(40, 40, 40));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw grid
        gc.setStroke(Color.rgb(30, 40, 70));
        for (int i = 0; i <= GameBoard.getRows(); i++) {
            gc.strokeLine(0, i * CELL_SIZE, canvas.getWidth(), i * CELL_SIZE);
        }
        for (int j = 0; j <= GameBoard.getCols(); j++) {
            gc.strokeLine(j * CELL_SIZE, 0, j * CELL_SIZE, canvas.getHeight());
        }

        if (!isActive) {
            gc.setFill(Color.rgb(0, 0, 0, 0.6));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.RED);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            gc.fillText("GAME OVER", canvas.getWidth() / 2 - 65, canvas.getHeight() / 2);
            return;
        }

        // Draw locked pieces
        Color[][] boardData = board.getBoard();
        for (int i = 0; i < GameBoard.getRows(); i++) {
            for (int j = 0; j < GameBoard.getCols(); j++) {
                if (boardData[i][j] != null) {
                    drawCell(gc, j, i, boardData[i][j]);
                }
            }
        }

        // Draw ghost piece
        Tetromino current = board.getCurrentPiece();
        int ghostY = board.getGhostY();
        int[][] shape = current.getShape();
        gc.setFill(Color.rgb(255, 255, 255, 0.2));
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    gc.fillRect((current.getX() + j) * CELL_SIZE + 1,
                              (ghostY + i) * CELL_SIZE + 1,
                              CELL_SIZE - 2, CELL_SIZE - 2);
                }
            }
        }

        // Draw current piece
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    drawCell(gc, current.getX() + j, current.getY() + i, current.getColor());
                }
            }
        }

        if (isPaused) {
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            gc.fillText("PAUSED", canvas.getWidth() / 2 - 50, canvas.getHeight() / 2);
        }
    }

    private void drawCell(GraphicsContext gc, int x, int y, Color color) {
        gc.setFill(color);
        gc.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);

        gc.setFill(color.brighter());
        gc.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 2, 2);
        gc.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, 2, CELL_SIZE - 2);

        gc.setFill(color.darker());
        gc.fillRect(x * CELL_SIZE + CELL_SIZE - 3, y * CELL_SIZE + 1, 2, CELL_SIZE - 2);
        gc.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + CELL_SIZE - 3, CELL_SIZE - 2, 2);
    }

    private void drawPreviewPiece(GraphicsContext gc, Tetromino piece, Canvas canvas) {
        gc.setFill(Color.rgb(15, 52, 96));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (piece == null) return;

        int[][] shape = piece.getShape();
        int previewSize = 18;
        int offsetX = (int)(canvas.getWidth() - shape[0].length * previewSize) / 2;
        int offsetY = (int)(canvas.getHeight() - shape.length * previewSize) / 2;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    gc.setFill(piece.getColor());
                    gc.fillRect(offsetX + j * previewSize + 1, offsetY + i * previewSize + 1,
                              previewSize - 2, previewSize - 2);
                }
            }
        }
    }

    private void checkGameEnd() {
        if (player1GameOver && player2GameOver) {
            gameLoop.stop();
            // Both players lost
        } else if (player1GameOver) {
            gameLoop.stop();
            showWinner("PLAYER 2 WINS!", player2Board.getScore());
        } else if (player2GameOver) {
            gameLoop.stop();
            showWinner("PLAYER 1 WINS!", player1Board.getScore());
        }
    }

    private void showWinner(String message, int score) {
        GraphicsContext gc = player1Canvas.getGraphicsContext2D();
        gc.setFill(Color.rgb(0, 200, 0, 0.3));
        gc.fillRect(0, 0, player1Canvas.getWidth(), player1Canvas.getHeight());

        gc = player2Canvas.getGraphicsContext2D();
        gc.setFill(Color.rgb(0, 200, 0, 0.3));
        gc.fillRect(0, 0, player2Canvas.getWidth(), player2Canvas.getHeight());
    }

    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}
