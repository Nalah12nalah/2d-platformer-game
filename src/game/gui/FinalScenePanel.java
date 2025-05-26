package game.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A panel that shows the final scene with a fade-in and fade-out effect,
 * then returns the player to the main menu.
 */
public class FinalScenePanel extends JPanel {

    private final JFrame frame;
    private float opacity = 0f; // controls transparency
    private Timer fadeInTimer;
    private Timer fadeOutTimer;
    private Image finalImage;

    /**
     * Creates the final scene panel and starts the fade-in animation.
     * @param frame the window frame this panel is added to
     */
    public FinalScenePanel(JFrame frame) {
        this.frame = frame;
        setLayout(null);
        setOpaque(false);

        finalImage = new ImageIcon("data/Backgrounds/FinalScene.png").getImage();

        startFadeIn();
    }

    /** Starts gradually increasing opacity to fade in the final scene. */
    private void startFadeIn() {
        fadeInTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1f) {
                    opacity = 1f;
                    fadeInTimer.stop();
                    stayForSeconds();
                }
                repaint();
            }
        });
        fadeInTimer.start();
    }

    /** Keeps the scene fully visible for a few seconds before fading out. */
    private void stayForSeconds() {
        Timer stayTimer = new Timer(7000, e -> startFadeOut());
        stayTimer.setRepeats(false);
        stayTimer.start();
    }

    /** Starts gradually decreasing opacity to fade out the final scene. */
    private void startFadeOut() {
        fadeOutTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0f) {
                    opacity = 0f;
                    fadeOutTimer.stop();
                    returnToMainMenu();
                }
                repaint();
            }
        });
        fadeOutTimer.start();
    }

    /** Switches back to the main menu after fade out is complete. */
    private void returnToMainMenu() {
        frame.setContentPane(new MainMenu(frame));
        frame.revalidate();
        frame.repaint();
    }

    /** Paints the final scene image with current opacity. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        g2d.drawImage(finalImage, 0, 0, getWidth(), getHeight(), this);

        g2d.dispose();
    }
}
