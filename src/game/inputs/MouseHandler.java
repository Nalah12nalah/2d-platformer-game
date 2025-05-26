package game.inputs;

import game.gui.PauseMenu;
import game.characters.Cat;
import game.main.GameView;
import game.main.GameWorld;
import game.obstacles.CardboardBox;
import game.utils.Destructible;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Handles mouse inputs like scratching cardboard, charging nubella, and pausing the game.
 */
public class MouseHandler implements MouseListener {

    private final GameWorld world;
    private final GameView view;
    private final Cat cat;
    public static boolean isMouseDown = false;
    private long mousePressTime;
    private final long CHARGE_DELAY = 200; // milliseconds before charging starts

    /**
     * Creates a MouseHandler for the Cat character and game world.
     */
    public MouseHandler(GameWorld world, GameView view, Cat cat) {
        this.world = world;
        this.view = view;
        this.cat = cat;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Handle clicking the pause button
        Point clickPoint = e.getPoint();
        SwingUtilities.convertPointToScreen(clickPoint, view);
        SwingUtilities.convertPointFromScreen(clickPoint, view);

        if (view.getPauseButtonBounds().contains(clickPoint)) {
            world.stop();
            PauseMenu pauseMenu = new PauseMenu(
                    (JFrame) SwingUtilities.getWindowAncestor(view),
                    world,
                    view
            );
            view.add(pauseMenu, Integer.valueOf(1));
            view.repaint();
            view.revalidate();
            return;
        }

        // Handle scratching nearby destructibles
        if (!cat.isScratching() && !cat.isChargingNubella()) {
            Vec2 catPos = cat.getPosition();
            for (Destructible d : world.getDestructibles()) {
                if (d == null || d.getBody() == null || d.getBody().getWorld() == null) continue;

                float distance = catPos.sub(d.getBody().getPosition()).length();
                if (distance < 5f && d instanceof CardboardBox box && !box.isDestroyed()) {
                    cat.startScratching(d);
                    break;
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isMouseDown = true;
        mousePressTime = System.currentTimeMillis();

        // Start charging nubella if held long enough
        Timer chargeDelayTimer = new Timer((int) CHARGE_DELAY, ev -> {
            if (isMouseDown && cat.getNubellaCount() > 0) {
                cat.startChargingNubella();
            }
        });
        chargeDelayTimer.setRepeats(false);
        chargeDelayTimer.start();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMouseDown = false;

        // Release charged nubella if held long enough
        long heldTime = System.currentTimeMillis() - mousePressTime;
        if (heldTime >= CHARGE_DELAY) {
            cat.releaseNubella();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not used
    }
}
