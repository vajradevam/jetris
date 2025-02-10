package org.vajradevam.tetris;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TetrisGame extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Tetris Game");

        showMainMenu();
    }

    private void showMainMenu() {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(50));
        menu.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("TETRIS");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");

        Button singlePlayer = new Button("Single Player");
        Button twoPlayer = new Button("Two Player");
        Button leaderboard = new Button("Leaderboard");
        Button exit = new Button("Exit");

        styleButton(singlePlayer);
        styleButton(twoPlayer);
        styleButton(leaderboard);
        styleButton(exit);

        singlePlayer.setOnAction(e -> startSinglePlayer());
        twoPlayer.setOnAction(e -> startTwoPlayer());
        leaderboard.setOnAction(e -> showLeaderboard());
        exit.setOnAction(e -> primaryStage.close());

        menu.getChildren().addAll(title, singlePlayer, twoPlayer, leaderboard, exit);

        Scene scene = new Scene(menu, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void styleButton(Button button) {
        button.setPrefWidth(250);
        button.setPrefHeight(50);
        button.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; " +
                       "-fx-font-size: 18px; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #0f4c75; -fx-text-fill: white; " +
            "-fx-font-size: 18px; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #16213e; -fx-text-fill: white; " +
            "-fx-font-size: 18px; -fx-background-radius: 5;"));
    }

    private void startSinglePlayer() {
        GamePanel gamePanel = new GamePanel(this, false);
        Scene scene = new Scene(gamePanel, 800, 700);
        primaryStage.setScene(scene);
        gamePanel.requestFocus();
        gamePanel.startGame();
    }

    private void startTwoPlayer() {
        TwoPlayerPanel twoPlayerPanel = new TwoPlayerPanel(this);
        Scene scene = new Scene(twoPlayerPanel, 1200, 700);
        primaryStage.setScene(scene);
        twoPlayerPanel.requestFocus();
        twoPlayerPanel.startGame();
    }

    private void showLeaderboard() {
        LeaderboardPanel leaderboardPanel = new LeaderboardPanel(this);
        Scene scene = new Scene(leaderboardPanel, 600, 500);
        primaryStage.setScene(scene);
    }

    public void returnToMenu() {
        showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
