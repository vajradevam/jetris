package org.vajradevam.tetris;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GamePanel extends BorderPane {
    private static final int CELL_SIZE = 30;

    private GameBoard gameBoard;
    private Canvas mainCanvas;
    private Canvas nextPieceCanvas;
    private Canvas heldPieceCanvas;
    private GraphicsContext gc;
    private GraphicsContext nextGc;
    private GraphicsContext heldGc;

    private AnimationTimer gameLoop;
    private long lastDropTime;
    private boolean isGameOver;

    private Label scoreLabel;
    private Label levelLabel;
    private Label linesLabel;
    private TetrisGame mainApp;
    private boolean isPaused;

    public GamePanel(TetrisGame mainApp, boolean isMultiplayer) {
        this.mainApp = mainApp;
        this.isGameOver = false;
        this.isPaused = false;

        gameBoard = new GameBoard();

        setStyle("-fx-background-color: #1a1a2e;");

        setupUI();
        setupControls();
    }

    private void setupUI() {
        // Main game canvas
        mainCanvas = new Canvas(GameBoard.getCols() * CELL_SIZE, GameBoard.getRows() * CELL_SIZE);
        gc = mainCanvas.getGraphicsContext2D();

        StackPane canvasPane = new StackPane(mainCanvas);
        canvasPane.setStyle("-fx-background-color: #16213e; -fx-padding: 10;");
        setCenter(canvasPane);

        // Right panel
        VBox rightPanel = new VBox(20);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle("-fx-background-color: #16213e;");
        rightPanel.setPrefWidth(200);

        // Score display
        VBox scoreBox = createInfoBox("SCORE");
        scoreLabel = new Label("0");
        scoreLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #00ff88; -fx-font-weight: bold;");
        scoreBox.getChildren().add(scoreLabel);

        // Level display
        VBox levelBox = createInfoBox("LEVEL");
        levelLabel = new Label("1");
        levelLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #00ff88; -fx-font-weight: bold;");
        levelBox.getChildren().add(levelLabel);

        // Lines display
        VBox linesBox = createInfoBox("LINES");
        linesLabel = new Label("0");
        linesLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #00ff88; -fx-font-weight: bold;");
        linesBox.getChildren().add(linesLabel);

        // Next piece
        VBox nextBox = createInfoBox("NEXT");
        nextPieceCanvas = new Canvas(120, 120);
        nextGc = nextPieceCanvas.getGraphicsContext2D();
        nextBox.getChildren().add(nextPieceCanvas);

        // Held piece
        VBox heldBox = createInfoBox("HOLD");
        heldPieceCanvas = new Canvas(120, 120);
        heldGc = heldPieceCanvas.getGraphicsContext2D();
        heldBox.getChildren().add(heldPieceCanvas);

        // Controls info
        VBox controlsBox = createInfoBox("CONTROLS");
        Label controls = new Label("← → Move\n↓ Soft Drop\nSPACE Hard Drop\n↑ Rotate\nC Hold\nP Pause\nESC Menu");
        controls.setStyle("-fx-font-size: 11px; -fx-text-fill: #ccc;");
        controlsBox.getChildren().add(controls);

        rightPanel.getChildren().addAll(scoreBox, levelBox, linesBox, nextBox, heldBox, controlsBox);
        setRight(rightPanel);
    }

    private VBox createInfoBox(String title) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #0f3460; -fx-padding: 10; -fx-background-radius: 5;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaa; -fx-font-weight: bold;");
        box.getChildren().add(titleLabel);

        return box;
    }

    private void setupControls() {
        setOnKeyPressed(event -> {
            if (isGameOver) return;

            KeyCode code = event.getCode();

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

            switch (code) {
                case LEFT:
                    gameBoard.moveLeft();
                    break;
                case RIGHT:
                    gameBoard.moveRight();
                    break;
                case DOWN:
                    gameBoard.moveDown();
                    break;
                case UP:
                    gameBoard.rotate();
                    break;
                case SPACE:
                    gameBoard.hardDrop();
                    break;
                case C:
                    gameBoard.holdPiece();
                    break;
            }

            render();
        });
    }

    public void startGame() {
        lastDropTime = System.nanoTime();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isPaused) return;

                long dropInterval = (long) (1_000_000_000L / (1 + gameBoard.getLevel() * 0.5));

                if (now - lastDropTime > dropInterval) {
                    if (!gameBoard.moveDown()) {
                        if (gameBoard.isGameOver()) {
                            gameOver();
                            return;
                        }
                    }
                    lastDropTime = now;
                }

                render();
            }
        };

        gameLoop.start();
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 36));
            gc.fillText("PAUSED", mainCanvas.getWidth() / 2 - 70, mainCanvas.getHeight() / 2);
        }
    }

    private void render() {
        // Clear canvases
        gc.setFill(Color.rgb(22, 33, 62));
        gc.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

        // Draw grid
        gc.setStroke(Color.rgb(30, 40, 70));
        for (int i = 0; i <= GameBoard.getRows(); i++) {
            gc.strokeLine(0, i * CELL_SIZE, mainCanvas.getWidth(), i * CELL_SIZE);
        }
        for (int j = 0; j <= GameBoard.getCols(); j++) {
            gc.strokeLine(j * CELL_SIZE, 0, j * CELL_SIZE, mainCanvas.getHeight());
        }

        // Draw locked pieces
        Color[][] board = gameBoard.getBoard();
        for (int i = 0; i < GameBoard.getRows(); i++) {
            for (int j = 0; j < GameBoard.getCols(); j++) {
                if (board[i][j] != null) {
                    drawCell(gc, j, i, board[i][j], CELL_SIZE);
                }
            }
        }

        // Draw ghost piece
        Tetromino current = gameBoard.getCurrentPiece();
        int ghostY = gameBoard.getGhostY();
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
                    drawCell(gc, current.getX() + j, current.getY() + i, current.getColor(), CELL_SIZE);
                }
            }
        }

        // Draw next piece
        drawPreviewPiece(nextGc, gameBoard.getNextPiece(), nextPieceCanvas);

        // Draw held piece
        drawPreviewPiece(heldGc, gameBoard.getHeldPiece(), heldPieceCanvas);

        // Update labels
        scoreLabel.setText(String.valueOf(gameBoard.getScore()));
        levelLabel.setText(String.valueOf(gameBoard.getLevel()));
        linesLabel.setText(String.valueOf(gameBoard.getLinesCleared()));
    }

    private void drawCell(GraphicsContext gc, int x, int y, Color color, int size) {
        gc.setFill(color);
        gc.fillRect(x * size + 1, y * size + 1, size - 2, size - 2);

        gc.setFill(color.brighter());
        gc.fillRect(x * size + 1, y * size + 1, size - 2, 3);
        gc.fillRect(x * size + 1, y * size + 1, 3, size - 2);

        gc.setFill(color.darker());
        gc.fillRect(x * size + size - 4, y * size + 1, 3, size - 2);
        gc.fillRect(x * size + 1, y * size + size - 4, size - 2, 3);
    }

    private void drawPreviewPiece(GraphicsContext gc, Tetromino piece, Canvas canvas) {
        gc.setFill(Color.rgb(15, 52, 96));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (piece == null) return;

        int[][] shape = piece.getShape();
        int previewSize = 25;
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

    private void gameOver() {
        isGameOver = true;
        gameLoop.stop();

        LeaderboardManager.addScore(gameBoard.getScore());

        gc.setFill(Color.rgb(0, 0, 0, 0.8));
        gc.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

        gc.setFill(Color.RED);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        gc.fillText("GAME OVER", mainCanvas.getWidth() / 2 - 110, mainCanvas.getHeight() / 2 - 40);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        gc.fillText("Score: " + gameBoard.getScore(), mainCanvas.getWidth() / 2 - 70, mainCanvas.getHeight() / 2 + 10);
        gc.fillText("Press ESC for menu", mainCanvas.getWidth() / 2 - 120, mainCanvas.getHeight() / 2 + 50);
    }

    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}
