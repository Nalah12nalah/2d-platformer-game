package game.winobjects;

import city.cs.engine.*;
import game.characters.Cat;
import game.gui.FinalScenePanel;
import game.gui.LevelTransitionPanel;
import game.inputs.CatController;
import game.inputs.MouseHandler;
import game.levels.Level2;
import game.levels.Level3;
import game.main.GameView;
import game.main.GameWorld;
import game.sounds.MusicManager;
import org.jbox2d.common.Vec2;

import javax.swing.*;

/**
 * A static object that triggers level completion when the player reaches it.
 * Handles level transitions, music switching, and final scene display.
 */
public class WinObject extends StaticBody implements CollisionListener {
    private final JFrame frame;
    private final GameWorld currentWorld;

    /**
     * Constructs a WinObject at the specified position.
     * @param world the current game world
     * @param position the position to place the WinObject
     * @param frame the main game frame
     * @param imagePath path to the WinObject image
     * @param imageScale scale for the image size
     */
    public WinObject(GameWorld world, Vec2 position, JFrame frame, String imagePath, float imageScale) {
        super(world, new BoxShape(1, 2));
        this.frame = frame;
        this.currentWorld = world;

        setPosition(position);
        addImage(new BodyImage(imagePath, imageScale));
        addCollisionListener(this);
    }

    /**
     * Handles collision between the WinObject and the Cat.
     * Triggers level transition or final scene depending on the current level.
     */
    @Override
    public void collide(CollisionEvent e) {
        if (e.getOtherBody() instanceof Cat) {
            getWorld().stop();
            MusicManager.fadeOutAndStop();

            SwingUtilities.invokeLater(() -> {
                LevelTransitionPanel transition = new LevelTransitionPanel(frame, () -> {
                    GameWorld newWorld = null;
                    int nextLevel = currentWorld.getLevelNumber() + 1;

                    switch (nextLevel) {
                        case 2 -> {
                            newWorld = new Level2(frame);
                            MusicManager.fadeIn("data/Sounds/BackgroundLevel2.WAV", true);
                        }
                        case 3 -> {
                            newWorld = new Level3(frame);
                            MusicManager.fadeIn("data/Sounds/BackgroundLevel3.WAV", true);
                        }
                        default -> {
                            FinalScenePanel finalScene = new FinalScenePanel(frame);
                            frame.setContentPane(finalScene);
                            frame.revalidate();
                            frame.repaint();
                            return;
                        }
                    }

                    if (newWorld != null) {
                        newWorld.getCat().setHealth(currentWorld.getCat().getHealth());
                        newWorld.getCat().setFoodCount(currentWorld.getCat().getFoodCount());

                        GameView view = new GameView(newWorld, 1200, 800, newWorld.getCat());
                        newWorld.setGameView(view);
                        view.addKeyListener(new CatController(newWorld.getCat(), newWorld, view, frame));
                        view.addMouseListener(new MouseHandler(newWorld, view, newWorld.getCat()));

                        frame.setContentPane(view);
                        frame.revalidate();
                        frame.repaint();
                        view.requestFocusInWindow();

                        newWorld.start();
                        view.add(new LevelTransitionPanel(frame, () -> {}));
                    }
                });

                frame.getLayeredPane().add(transition, JLayeredPane.DRAG_LAYER);
                transition.repaint();
            });
        }
    }
}
