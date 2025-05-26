package game.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * A panel that handles level transition with a fade-in and fade-out effect,
 * displaying a "LEVEL COMPLETE!" message in the middle.
 */
public class LevelTransitionPanel extends JPanel {

    private float alpha = 0f; // controls fade transparency
    private boolean fadingIn = true;
    private final Timer timer;
    private final Runnable onFadeComplete;
    private Font pixelFont;

    /**
     * Creates a LevelTransitionPanel and starts the fade animation.
     * @param frame the game window
     * @param onFadeComplete code to run when fade-in completes
     */
    public LevelTransitionPanel(JFrame frame, Runnable onFadeComplete) {
        this.onFadeComplete = onFadeComplete;
        setOpaque(false);
        setBounds(0, 0, 1200, 800);

        // Load custom pixel font
        try {
            File fontFile = new File("data/Fonts/victor-pixel.ttf");
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(50f);
        } catch (FontFormatException | IOException e) {
            System.out.println("Could not load pixel font. Falling back.");
            pixelFont = new Font("Monospaced", Font.BOLD, 48);
        }

        // Timer to handle fade in and fade out
        timer = new Timer(120, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fadingIn) {
                    alpha += 0.1f;
                    if (alpha >= 1.5f) {
                        alpha = 1.5f;
                        fadingIn = false;
                        onFadeComplete.run();
                    }
                } else {
                    alpha -= 0.05f;
                    if (alpha <= 0f) {
                        alpha = 0f;
                        timer.stop();
                        Container parent = getParent();
                        if (parent != null) {
                            parent.remove(LevelTransitionPanel.this);
                            parent.repaint();
                        }
                    }
                }
                repaint();
            }
        });
        timer.start();
    }

    /**
     * Paints the black fade screen and shows "LEVEL COMPLETE!" during fade-in.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Show "LEVEL COMPLETE!" only during fade in
        if (alpha >= 0.8f && fadingIn) {
            g2d.setFont(pixelFont);
            g2d.setColor(Color.WHITE);
            String message = "LEVEL COMPLETE!";
            FontMetrics fm = g2d.getFontMetrics();
            int msgWidth = fm.stringWidth(message);
            int x = (getWidth() - msgWidth) / 2;
            int y = getHeight() / 2;
            g2d.drawString(message, x, y);
        }

        g2d.dispose();
    }
}
