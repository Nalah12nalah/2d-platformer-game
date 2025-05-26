package game.gui;

import city.cs.engine.DebugViewer;
import game.inputs.MouseHandler;
import game.sounds.MusicManager;
import game.inputs.CatController;
import game.levels.Level1;
import game.levels.Level2;
import game.levels.Level3;
import game.main.GameState;
import game.main.GameView;
import game.main.GameWorld;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * The Main Menu screen where the player can start a new game or load a saved game.
 */
public class MainMenu extends JPanel {

    private Image backgroundImage;
    private final JFrame frame;
    private Font pixelFont;

    /**
     * Creates the Main Menu and sets up buttons and background.
     * @param frame the main window frame
     */
    public MainMenu(JFrame frame) {
        this.frame = frame;
        setLayout(null);

        // Load custom pixel font
        try {
            File fontFile = new File("data/Fonts/victor-pixel.ttf");
            if (fontFile.exists()) {
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(40f);
            } else {
                throw new IOException("Font file not found: " + fontFile.getAbsolutePath());
            }
        } catch (FontFormatException | IOException e) {
            System.out.println("Falling back to Monospaced font due to: " + e.getMessage());
            pixelFont = new Font("Monospaced", Font.BOLD, 30);
        }

        // Load background image
        try {
            backgroundImage = new ImageIcon("data/Backgrounds/MainMenuBg.png").getImage();
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
        }

        // Create level buttons
        createLevelButton("", 440, 340, 1);
        createLevelButton("", 440, 440, 2);
        createLevelButton("", 440, 550, 3);
        createLoadGameButton("", 440, 670);

        MusicManager.fadeIn("data/Sounds/MainMenuMusic.wav", true);
    }

    /**
     * Creates a button for starting a new game at a specific level.
     */
    private void createLevelButton(String text, int x, int y, int level) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 390, 80);
        button.setFont(pixelFont);
        button.setBackground(new Color(30, 30, 30));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        add(button);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startGame(level);
            }
        });
    }

    /**
     * Creates a button for loading a previously saved game.
     */
    private void createLoadGameButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 390, 80);
        button.setFont(pixelFont);
        button.setBackground(new Color(30, 30, 30));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        add(button);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                File file = new File("data/Save/save.txt");
                if (file.exists()) {
                    GameState.GameData data = GameState.loadGame(file);
                    if (data != null) {
                        GameWorld world;
                        switch (data.level) {
                            case 1 -> {
                                world = new Level1(frame);
                                MusicManager.fadeIn("data/Sounds/BackgroundLevel1.WAV", true);
                            }
                            case 2 -> {
                                world = new Level2(frame);
                                MusicManager.fadeIn("data/Sounds/BackgroundLevel2.WAV", true);
                            }
                            case 3 -> {
                                world = new Level3(frame);
                                MusicManager.fadeIn("data/Sounds/BackgroundLevel3.WAV", true);
                            }
                            default -> {
                                JOptionPane.showMessageDialog(frame, "Invalid level in save file.");
                                return;
                            }
                        }

                        // Load player's previous data
                        world.getCat().setHealth(data.lives);
                        world.getCat().setFoodCount(data.score);
                        world.getCat().setPosition(new org.jbox2d.common.Vec2(data.catX, data.catY));

                        GameView view = new GameView(world, 1200, 800, world.getCat());
                        world.setGameView(view);
                        view.addKeyListener(new CatController(world.getCat(), world, view, frame));
                        view.addMouseListener(new MouseHandler(world, view, world.getCat()));

                        frame.setContentPane(view);
                        frame.revalidate();
                        frame.repaint();
                        frame.setVisible(true);
                        view.requestFocusInWindow();

                        world.start();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "No save file found.");
                }
            }
        });
    }

    /**
     * Starts a new game at the selected level.
     */
    private void startGame(int level) {
        frame.getContentPane().removeAll();
        frame.repaint();

        MusicManager.fadeOutAndStop();

        GameWorld world = switch (level) {
            case 1 -> {
                MusicManager.fadeIn("data/Sounds/BackgroundLevel1.wav", true);
                yield new Level1(frame);
            }
            case 2 -> {
                MusicManager.fadeIn("data/Sounds/BackgroundLevel2.wav", true);
                yield new Level2(frame);
            }
            case 3 -> {
                MusicManager.fadeIn("data/Sounds/BackgroundLevel3.wav", true);
                yield new Level3(frame);
            }
            default -> null;
        };

        if (world == null) {
            System.out.println("Invalid level: " + level);
            return;
        }

        GameView view = new GameView(world, 1200, 800, world.getCat());
        world.setGameView(view);
        view.addKeyListener(new CatController(world.getCat(), world, view, frame));
        view.addMouseListener(new MouseHandler(world, view, world.getCat()));

        frame.setContentPane(view);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
        view.requestFocusInWindow();

        world.start();
    }

    /**
     * Draws the main menu background.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
