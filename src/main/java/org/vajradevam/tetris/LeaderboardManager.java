package org.vajradevam.tetris;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "tetris_leaderboard.dat";
    private static final int MAX_ENTRIES = 10;

    public static class ScoreEntry implements Comparable<ScoreEntry> {
        public int score;
        public String date;

        public ScoreEntry(int score, String date) {
            this.score = score;
            this.date = date;
        }

        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score);
        }
    }

    public static void addScore(int score) {
        List<ScoreEntry> entries = loadScores();

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        entries.add(new ScoreEntry(score, date));

        Collections.sort(entries);

        if (entries.size() > MAX_ENTRIES) {
            entries = entries.subList(0, MAX_ENTRIES);
        }

        saveScores(entries);
    }

    public static List<ScoreEntry> loadScores() {
        List<ScoreEntry> entries = new ArrayList<>();

        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            return entries;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    int score = Integer.parseInt(parts[0]);
                    String date = parts[1];
                    entries.add(new ScoreEntry(score, date));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(entries);
        return entries;
    }

    private static void saveScores(List<ScoreEntry> entries) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (ScoreEntry entry : entries) {
                writer.println(entry.score + "|" + entry.date);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearLeaderboard() {
        File file = new File(LEADERBOARD_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}
