package game.inputs;

import game.main.GameView;
import game.main.GameWorld;
import game.gui.PauseMenu;
import game.characters.Cat;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles keyboard inputs to control the Cat character
 * (moving, jumping, throwing, eating, shielding, pausing).
 */
public class CatController implements KeyListener {

    private final Cat cat;
    private final GameWorld world;
    private final GameView view;
    private final JFrame frame;

    /**
     * Creates a CatController to manage player keyboard input.
     */
    public CatController(Cat cat, GameWorld world, GameView view, JFrame frame) {
        this.cat = cat;
        this.world = world;
        this.view = view;
        this.frame = frame;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Move right
        if (code == KeyEvent.VK_RIGHT) {
            cat.startWalking(20);
        }
        // Move left
        else if (code == KeyEvent.VK_LEFT) {
            cat.startWalking(-10);
        }
        // Jump
        else if (code == KeyEvent.VK_UP) {
            cat.jump(20);
        }
        // Throw rock
        else if (code == KeyEvent.VK_SPACE) {
            cat.throwRock(cat.getWorld());
        }
        // Eat food
        if (code == KeyEvent.VK_E) {
            cat.eatFood();
        }
        // Pause game
        if (code == KeyEvent.VK_P) {
            world.stop();
            PauseMenu pauseMenu = new PauseMenu(frame, world, view);
            view.add(pauseMenu);
            view.repaint();
            view.revalidate();
        }
        // Start shielding
        else if (e.getKeyCode() == KeyEvent.VK_S) {
            cat.startShielding();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        // Stop moving when keys released
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_LEFT) {
            cat.stopWalking();
        }
        // Stop shielding
        else if (e.getKeyCode() == KeyEvent.VK_S) {
            cat.stopShielding();
        }
    }
}
