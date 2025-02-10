package org.vajradevam.tetris;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class LeaderboardPanel extends VBox {
    private TetrisGame mainApp;

    public LeaderboardPanel(TetrisGame mainApp) {
        this.mainApp = mainApp;

        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(30));
        setSpacing(20);
        setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("LEADERBOARD");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");

        VBox scoresBox = new VBox(10);
        scoresBox.setAlignment(Pos.CENTER);
        scoresBox.setPadding(new Insets(20));
        scoresBox.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");

        List<LeaderboardManager.ScoreEntry> scores = LeaderboardManager.loadScores();

        if (scores.isEmpty()) {
            Label noScores = new Label("No scores yet!");
            noScores.setStyle("-fx-font-size: 18px; -fx-text-fill: #aaa;");
            scoresBox.getChildren().add(noScores);
        } else {
            // Header
            HBox header = new HBox(20);
            header.setAlignment(Pos.CENTER);

            Label rankHeader = new Label("RANK");
            rankHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00ff88; -fx-min-width: 60;");

            Label scoreHeader = new Label("SCORE");
            scoreHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00ff88; -fx-min-width: 100;");

            Label dateHeader = new Label("DATE");
            dateHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00ff88; -fx-min-width: 150;");

            header.getChildren().addAll(rankHeader, scoreHeader, dateHeader);
            scoresBox.getChildren().add(header);

            // Scores
            for (int i = 0; i < scores.size(); i++) {
                LeaderboardManager.ScoreEntry entry = scores.get(i);

                HBox scoreRow = new HBox(20);
                scoreRow.setAlignment(Pos.CENTER);

                Label rank = new Label("#" + (i + 1));
                rank.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-min-width: 60;");

                Label score = new Label(String.valueOf(entry.score));
                score.setStyle("-fx-font-size: 18px; -fx-text-fill: #00ff88; -fx-min-width: 100;");

                Label date = new Label(entry.date);
                date.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaa; -fx-min-width: 150;");

                scoreRow.getChildren().addAll(rank, score, date);
                scoresBox.getChildren().add(scoreRow);
            }
        }

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back to Menu");
        Button clearButton = new Button("Clear Scores");

        styleButton(backButton);
        styleButton(clearButton);

        backButton.setOnAction(e -> mainApp.returnToMenu());
        clearButton.setOnAction(e -> {
            LeaderboardManager.clearLeaderboard();
            mainApp.returnToMenu();
        });

        buttonBox.getChildren().addAll(backButton, clearButton);

        getChildren().addAll(title, scoresBox, buttonBox);
    }

    private void styleButton(Button button) {
        button.setPrefWidth(180);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; " +
                       "-fx-font-size: 16px; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #0f4c75; -fx-text-fill: white; " +
            "-fx-font-size: 16px; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #16213e; -fx-text-fill: white; " +
            "-fx-font-size: 16px; -fx-background-radius: 5;"));
    }
}
