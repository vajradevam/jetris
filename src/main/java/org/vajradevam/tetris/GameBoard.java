package org.vajradevam.tetris;

import javafx.scene.paint.Color;
import java.util.Random;

public class GameBoard {
    private static final int ROWS = 20;
    private static final int COLS = 10;

    private Color[][] board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private Tetromino heldPiece;
    private boolean canHold;
    private Random random;

    private int score;
    private int level;
    private int linesCleared;

    public GameBoard() {
        board = new Color[ROWS][COLS];
        random = new Random();
        score = 0;
        level = 1;
        linesCleared = 0;
        canHold = true;

        nextPiece = generateRandomPiece();
        spawnNewPiece();
    }

    private Tetromino generateRandomPiece() {
        Tetromino.Type[] types = Tetromino.Type.values();
        return new Tetromino(types[random.nextInt(types.length)]);
    }

    public void spawnNewPiece() {
        currentPiece = nextPiece;
        nextPiece = generateRandomPiece();
        canHold = true;

        if (!isValidPosition(currentPiece)) {
            // Game over
        }
    }

    public boolean moveDown() {
        currentPiece.moveDown();
        if (!isValidPosition(currentPiece)) {
            currentPiece.setY(currentPiece.getY() - 1);
            lockPiece();
            return false;
        }
        return true;
    }

    public void moveLeft() {
        currentPiece.moveLeft();
        if (!isValidPosition(currentPiece)) {
            currentPiece.moveRight();
        }
    }

    public void moveRight() {
        currentPiece.moveRight();
        if (!isValidPosition(currentPiece)) {
            currentPiece.moveLeft();
        }
    }

    public void rotate() {
        currentPiece.rotate();
        if (!isValidPosition(currentPiece)) {
            // Try wall kicks
            int[][] wallKicks = {{-1, 0}, {1, 0}, {0, -1}, {-2, 0}, {2, 0}};
            boolean found = false;

            for (int[] kick : wallKicks) {
                currentPiece.setX(currentPiece.getX() + kick[0]);
                currentPiece.setY(currentPiece.getY() + kick[1]);

                if (isValidPosition(currentPiece)) {
                    found = true;
                    break;
                }

                currentPiece.setX(currentPiece.getX() - kick[0]);
                currentPiece.setY(currentPiece.getY() - kick[1]);
            }

            if (!found) {
                currentPiece.rotateCounterClockwise();
            }
        }
    }

    public void hardDrop() {
        while (moveDown()) {
            score += 2;
        }
    }

    public void holdPiece() {
        if (!canHold) return;

        if (heldPiece == null) {
            heldPiece = currentPiece;
            spawnNewPiece();
        } else {
            Tetromino temp = currentPiece;
            currentPiece = heldPiece;
            currentPiece.setX(3);
            currentPiece.setY(0);
            heldPiece = temp;
        }

        canHold = false;
    }

    private boolean isValidPosition(Tetromino piece) {
        int[][] shape = piece.getShape();
        int px = piece.getX();
        int py = piece.getY();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardX = px + j;
                    int boardY = py + i;

                    if (boardX < 0 || boardX >= COLS || boardY >= ROWS) {
                        return false;
                    }

                    if (boardY >= 0 && board[boardY][boardX] != null) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void lockPiece() {
        int[][] shape = currentPiece.getShape();
        int px = currentPiece.getX();
        int py = currentPiece.getY();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardY = py + i;
                    int boardX = px + j;
                    if (boardY >= 0 && boardY < ROWS && boardX >= 0 && boardX < COLS) {
                        board[boardY][boardX] = currentPiece.getColor();
                    }
                }
            }
        }

        int lines = clearLines();
        updateScore(lines);
        spawnNewPiece();
    }

    private int clearLines() {
        int clearedCount = 0;

        for (int i = ROWS - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] == null) {
                    full = false;
                    break;
                }
            }

            if (full) {
                clearedCount++;
                for (int k = i; k > 0; k--) {
                    board[k] = board[k - 1].clone();
                }
                board[0] = new Color[COLS];
                i++;
            }
        }

        return clearedCount;
    }

    private void updateScore(int lines) {
        linesCleared += lines;

        switch (lines) {
            case 1: score += 100 * level; break;
            case 2: score += 300 * level; break;
            case 3: score += 500 * level; break;
            case 4: score += 800 * level; break;
        }

        level = 1 + linesCleared / 10;
    }

    public boolean isGameOver() {
        return !isValidPosition(currentPiece);
    }

    public int getGhostY() {
        Tetromino ghost = currentPiece.copy();
        while (isValidPosition(ghost)) {
            ghost.moveDown();
        }
        return ghost.getY() - 1;
    }

    // Getters
    public Color[][] getBoard() { return board; }
    public Tetromino getCurrentPiece() { return currentPiece; }
    public Tetromino getNextPiece() { return nextPiece; }
    public Tetromino getHeldPiece() { return heldPiece; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLinesCleared() { return linesCleared; }
    public static int getRows() { return ROWS; }
    public static int getCols() { return COLS; }
}
