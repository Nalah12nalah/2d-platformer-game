package game.main;

import game.gui.MainMenu;

import javax.swing.*;

/**
 * Main class that sets up the game window and starts the main menu.
 */
public class Game {
    private JFrame frame;

    /**
     * Creates the game window and shows the main menu.
     */
    public Game() {
        frame = new JFrame("Cat Adventure");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Adds the main menu to the frame
        frame.add(new MainMenu(frame));
        frame.setVisible(true);
    }

    /**
     * Launches the game.
     */
    public static void main(String[] args) {
        // Handle any unexpected errors
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Uncaught Exception in thread " + t);
            e.printStackTrace();
        });

        new Game(); // Start the game
    }
}
