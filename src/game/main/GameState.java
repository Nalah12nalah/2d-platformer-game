package game.main;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Class that handles saving and loading the game state.
 */
public class GameState {

    /**
     * Saves the current game state to a file.
     */
    public static void saveGame(int level, int score, int lives, float catX, float catY, File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("# Save Timestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            writer.println(level);
            writer.println(score);
            writer.println(lives);
            writer.println(catX);
            writer.println(catY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the saved game state from a file.
     * @return GameData object containing the loaded data, or null if failed.
     */
    public static GameData loadGame(File file) {
        try (Scanner scanner = new Scanner(file)) {
            scanner.nextLine(); // Skip timestamp line
            int level = Integer.parseInt(scanner.nextLine());
            int score = Integer.parseInt(scanner.nextLine());
            int lives = Integer.parseInt(scanner.nextLine());
            float catX = Float.parseFloat(scanner.nextLine());
            float catY = Float.parseFloat(scanner.nextLine());
            return new GameData(level, score, lives, catX, catY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A simple class to store saved game data.
     */
    public static class GameData {
        public final int level;
        public final int score;
        public final int lives;
        public final float catX;
        public final float catY;

        public GameData(int level, int score, int lives, float catX, float catY) {
            this.level = level;
            this.score = score;
            this.lives = lives;
            this.catX = catX;
            this.catY = catY;
        }
    }
}
