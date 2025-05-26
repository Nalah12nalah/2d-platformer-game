package game.main;

import city.cs.engine.UserView;
import game.characters.Cat;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * The main game view that draws the background, HUD elements, and game world.
 */
public class GameView extends UserView {
    private final Cat cat;
    private final GameWorld world;
    private Image background;
    private boolean gameOver;
    private boolean showGameOverMessage;
    private long gameOverTime;
    private boolean gameWon = false;
    private Rectangle pauseButtonBounds = new Rectangle(10, 10, 160, 40);
    private final Image nubellaIcon = new ImageIcon("data/Weapons/nubella.png").getImage();
    private final Image rugIcon = new ImageIcon("data/Props/rugIcon.png").getImage();

    /**
     * Creates the game view and loads background.
     */
    public GameView(GameWorld world, int width, int height, Cat cat) {
        super(world, width, height);
        this.world = world;
        this.cat = cat;
        background = new ImageIcon(world.getBackgroundImagePath()).getImage();
        this.gameOver = false;
        this.showGameOverMessage = false;

        setLayout(null);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }

    /**
     * Triggers the "You Won" message at the end of the game.
     */
    public void triggerWinMessage() {
        gameWon = true;
    }

    // Converts world X coordinates to screen X pixels
    public int worldToScreenX(float worldX) {
        return (int) (getWidth() / 2 + worldX * getZoom());
    }

    // Converts world Y coordinates to screen Y pixels
    public int worldToScreenY(float worldY) {
        return (int) (getHeight() / 2 - worldY * getZoom());
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        // Centers the camera on the cat
        setView(new Vec2(cat.getPosition().x, cat.getPosition().y + 10), 20f);
        super.paintBackground(g);

        // Draw scrolling background
        float x = cat.getPosition().x;
        int offset = (int) (x * 0.5) % background.getWidth(null) - 5;

        Composite original = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));

        g.drawImage(background, -offset - 100, -200, null);
        g.drawImage(background, -offset + background.getWidth(null) - 100, -200, null);

        g.setComposite(original);
    }


    @Override
    protected void paintForeground(Graphics2D g) {
        super.paintForeground(g);

        Font pixelFont;
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("data/Fonts/victor-pixel.ttf")).deriveFont(30f);
            g.setFont(pixelFont);
        } catch (Exception e) {
            pixelFont = new Font("Monospaced", Font.BOLD, 20);
            g.setFont(pixelFont);
        }

        int screenWidth = getWidth();
        int xRight = screenWidth - 180;
        int boxWidth = 200;
        int boxHeight = 50;
        int spacing = 15;
        int iconSize = 30;

        g.setColor(new Color(115, 101, 101, 252));

        // Health Counter
        g.fillRoundRect(xRight - 30, 10, boxWidth, boxHeight, 15, 15);
        g.setColor(Color.WHITE);
        g.drawString("Health: " + cat.getHealth(), xRight - 20, 40);

        // Food Counter
        g.setColor(new Color(115, 101, 101, 252));
        g.fillRoundRect(xRight - 30, 70, boxWidth, boxHeight, 15, 15);
        g.setColor(Color.WHITE);
        g.drawImage(new ImageIcon("data/Pigeon/Food.png").getImage(), xRight - 20, 75, iconSize, iconSize, null);
        g.drawString("x" + cat.getFoodCount(), xRight + iconSize + 20, 105);

        // Nubella Counter
        g.setColor(new Color(115, 101, 101, 252));
        g.fillRoundRect(xRight - 30, 130, boxWidth, boxHeight, 15, 15);
        g.setColor(Color.WHITE);
        g.drawImage(nubellaIcon, xRight - 20, 135, iconSize, iconSize, null);
        g.drawString("x" + cat.getNubellaCount(), xRight + iconSize + 20, 165);

        // Rug Counter
        g.setColor(new Color(115, 101, 101, 252));
        g.fillRoundRect(xRight - 30, 190, boxWidth, boxHeight, 15, 15);
        g.setColor(Color.WHITE);
        g.drawImage(rugIcon, xRight - 20, 195, iconSize + 20, iconSize + 20, null);
        g.drawString("x" + cat.getRugCounter(), xRight + iconSize + 20, 225);

        // Pause Button
        g.setColor(new Color(115, 101, 101, 252));
        g.fillRoundRect(pauseButtonBounds.x, pauseButtonBounds.y, pauseButtonBounds.width, pauseButtonBounds.height, 15, 15);
        g.setColor(Color.WHITE);
        g.drawString("Pause", pauseButtonBounds.x + 25, pauseButtonBounds.y + 30);

        // Game Over Logic
        if ((cat.getPosition().y < -25 || cat.getHealth() <= 0) && !gameOver) {
            startGameOverSequence();
        }

        if (showGameOverMessage && (System.currentTimeMillis() - gameOverTime) < 2000) {
            drawGameOverMessage(g, screenWidth);
        }

        // Winning Screen
        if (gameWon) {
            drawWinMessage(g);
        }
    }



    /**
     * Starts the game over sequence when cat dies or falls.
     */
    private void startGameOverSequence() {
        gameOver = true;
        showGameOverMessage = true;
        gameOverTime = System.currentTimeMillis();

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                world.respawnCat();
                gameOver = false;
                showGameOverMessage = false;
            } catch (InterruptedException ignored) {}
        }).start();
    }

    /**
     * Draws "Game Over!" message at the center.
     */
    private void drawGameOverMessage(Graphics2D g, int screenWidth) {
        g.setColor(new Color(0, 0, 0, 134));
        g.fillRoundRect(screenWidth / 2 - 80, getHeight() / 2 - 25, 250, 50, 15, 15);
        g.setColor(Color.WHITE);
        g.drawString("Game Over!", screenWidth / 2 - 50, getHeight() / 2);
    }

    /**
     * Draws the "YOU WON!" message if the player wins.
     */
    private void drawWinMessage(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 171));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(60f));
        g.drawString("YOU WON!", getWidth() / 2 - 100, getHeight() / 2);
    }

    /**
     * Gets the rectangle bounds of the pause button.
     */
    public Rectangle getPauseButtonBounds() {
        return pauseButtonBounds;
    }
}
