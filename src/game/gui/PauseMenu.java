package game.gui;

import game.main.GameState;
import game.main.GameView;
import game.main.GameWorld;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * A panel that appears when the game is paused, allowing the player to
 * continue, save the game, or return to the main menu.
 */
public class PauseMenu extends JPanel {

    /**
     * Creates a Pause Menu with Continue, Save, and Main Menu options.
     * @param frame the main window frame
     * @param world the current game world
     * @param view the current game view
     */
    public PauseMenu(JFrame frame, GameWorld world, GameView view) {
        setOpaque(true);
        setLayout(null);
        setBackground(new Color(145, 136, 118, 115)); // semi-transparent brown
        setBounds(0, 0, 1200, 800);

        Font pixelFont;
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("data/Fonts/victor-pixel.ttf")).deriveFont(24f);
        } catch (Exception e) {
            System.out.println("Falling back to Monospaced font due to: " + e.getMessage());
            pixelFont = new Font("Monospaced", Font.BOLD, 20);
        }

        // Create Continue button
        JButton continueBtn = new JButton("Continue");
        continueBtn.setBounds(500, 300, 200, 50);
        continueBtn.setFont(pixelFont);

        // Create Save Game button
        JButton saveBtn = new JButton("Save Game");
        saveBtn.setBounds(500, 360, 200, 50);
        saveBtn.setFont(pixelFont);

        // Create Main Menu button
        JButton mainMenuBtn = new JButton("Main Menu");
        mainMenuBtn.setBounds(500, 420, 200, 50);
        mainMenuBtn.setFont(pixelFont);

        add(continueBtn);
        add(saveBtn);
        add(mainMenuBtn);

        // Resume the game when Continue is clicked
        continueBtn.addActionListener(e -> {
            world.start();
            view.remove(this);
            view.repaint();
            view.revalidate();
            view.requestFocusInWindow();
        });

        // Save the game when Save Game is clicked
        saveBtn.addActionListener(e -> {
            File file = new File("data/Save/save.txt");

            GameState.saveGame(
                    world.getLevelNumber(),
                    world.getCat().getFoodCount(),
                    world.getCat().getHealth(),
                    world.getCat().getPosition().x,
                    world.getCat().getPosition().y,
                    file
            );

            JOptionPane.showMessageDialog(this, "Game Saved Successfully!", "Save", JOptionPane.INFORMATION_MESSAGE);
        });

        // Return to Main Menu when Main Menu is clicked
        mainMenuBtn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.setContentPane(new MainMenu(frame));
            frame.revalidate();
            frame.repaint();
        });
    }
}
