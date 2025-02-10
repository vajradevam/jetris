package org.vajradevam.tetris;

import javafx.scene.paint.Color;

public class Tetromino {
    public enum Type {
        I, O, T, S, Z, J, L
    }

    private Type type;
    private int[][] shape;
    private Color color;
    private int x, y;

    public Tetromino(Type type) {
        this.type = type;
        this.x = 3;
        this.y = 0;
        initShape();
    }

    private void initShape() {
        switch (type) {
            case I:
                shape = new int[][]{
                    {0, 0, 0, 0},
                    {1, 1, 1, 1},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0}
                };
                color = Color.CYAN;
                break;
            case O:
                shape = new int[][]{
                    {1, 1},
                    {1, 1}
                };
                color = Color.YELLOW;
                break;
            case T:
                shape = new int[][]{
                    {0, 1, 0},
                    {1, 1, 1},
                    {0, 0, 0}
                };
                color = Color.PURPLE;
                break;
            case S:
                shape = new int[][]{
                    {0, 1, 1},
                    {1, 1, 0},
                    {0, 0, 0}
                };
                color = Color.GREEN;
                break;
            case Z:
                shape = new int[][]{
                    {1, 1, 0},
                    {0, 1, 1},
                    {0, 0, 0}
                };
                color = Color.RED;
                break;
            case J:
                shape = new int[][]{
                    {1, 0, 0},
                    {1, 1, 1},
                    {0, 0, 0}
                };
                color = Color.BLUE;
                break;
            case L:
                shape = new int[][]{
                    {0, 0, 1},
                    {1, 1, 1},
                    {0, 0, 0}
                };
                color = Color.ORANGE;
                break;
        }
    }

    public void rotate() {
        int n = shape.length;
        int[][] rotated = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                rotated[j][n - 1 - i] = shape[i][j];
            }
        }

        shape = rotated;
    }

    public void rotateCounterClockwise() {
        int n = shape.length;
        int[][] rotated = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                rotated[n - 1 - j][i] = shape[i][j];
            }
        }

        shape = rotated;
    }

    public Tetromino copy() {
        Tetromino copy = new Tetromino(this.type);
        copy.x = this.x;
        copy.y = this.y;
        copy.shape = new int[this.shape.length][this.shape[0].length];
        for (int i = 0; i < this.shape.length; i++) {
            System.arraycopy(this.shape[i], 0, copy.shape[i], 0, this.shape[i].length);
        }
        return copy;
    }

    // Getters and setters
    public int[][] getShape() { return shape; }
    public Color getColor() { return color; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public Type getType() { return type; }

    public void moveDown() { y++; }
    public void moveLeft() { x--; }
    public void moveRight() { x++; }
}
