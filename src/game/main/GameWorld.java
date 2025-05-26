package game.main;

import city.cs.engine.*;
import javax.swing.*;

import game.characters.Pigeon;
import game.characters.ThiefCat;
import game.characters.Cat;
import game.utils.Destructible;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class that defines the shared behavior for all game levels.
 * Handles player spawning, game over logic, and thief cat spawns.
 */
public abstract class GameWorld extends World {
    protected Cat cat;
    protected static final float MAIN_PLATFORM_Y = -15;
    protected static final Vec2 START_POSITION = new Vec2(-10, MAIN_PLATFORM_Y + 2);
    protected boolean gameOver = false;
    protected int gameOverCounter = 0;
    protected GameView view;
    protected JFrame frame;
    protected List<Pigeon> pigeons = new ArrayList<>();
    private int thiefSpawnCounter = 0;
    private boolean thiefCatActive = false;

    /**
     * Creates a new GameWorld and sets up the level and step listeners.
     * @param frame the main application window
     */
    public GameWorld(JFrame frame) {
        super();
        this.frame = frame;
        createLevel();

        // Adds step listener to check game over and thief spawning
        this.addStepListener(new StepListener() {
            public void preStep(StepEvent stepEvent) {
                checkGameOver();
                handleThiefSpawn();
            }
            public void postStep(StepEvent stepEvent) {}
        });
    }

    /**
     * Sets the GameView that displays this world.
     */
    public void setGameView(GameView view) {
        this.view = view;
    }

    /**
     * Method that each level implements to create its own platforms, enemies, props, etc.
     */
    protected abstract void createLevel();

    /**
     * Gets the current level number.
     */
    public abstract int getLevelNumber();

    /**
     * Checks if the player has fallen too far and triggers game over.
     */
    protected void checkGameOver() {
        if (!gameOver && cat.getPosition().y < MAIN_PLATFORM_Y - 5) {
            gameOver = true;
            gameOverCounter = 0;
        }
        if (gameOver) {
            gameOverCounter++;
            if (gameOverCounter >= 120) {
                respawnCat();
            }
        }
    }

    /**
     * Respawns the cat at the starting position after game over.
     */
    protected void respawnCat() {
        cat.setPosition(START_POSITION);
        cat.setLinearVelocity(new Vec2(0, 0));
        gameOver = false;
    }

    /**
     * Returns whether the game is currently in a game over state.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Gets the main cat character.
     */
    public Cat getCat() {
        return cat;
    }

    /**
     * Gets the list of pigeons in the world.
     */
    public List<Pigeon> getPigeons() {
        return pigeons;
    }

    /**
     * Returns the file path of the background image.
     */
    public abstract String getBackgroundImagePath();

    // List of destructible objects in the world
    private final List<Destructible> destructibles = new ArrayList<>();

    /**
     * Adds a destructible object to the world.
     */
    public void addDestructible(Destructible d) {
        destructibles.add(d);
    }

    /**
     * Gets all destructible objects in the world.
     */
    public List<Destructible> getDestructibles() {
        return destructibles;
    }

    /**
     * Spawns a thief cat near the player after a certain amount of time,
     * only if food is available to steal.
     */
    protected void handleThiefSpawn() {
        if (getLevelNumber() < 2) return; // Thief cats only spawn from Level 2+

        if (!thiefCatActive) {
            thiefSpawnCounter++;

            if (thiefSpawnCounter >= 2400 && cat.getFoodCount() > 0) {
                ThiefCat thief = new ThiefCat(this, cat);
                thiefCatActive = true;
                thiefSpawnCounter = 0;

                // Reset thiefCatActive once the thief is removed
                thief.addCollisionListener(new CollisionListener() {
                    @Override
                    public void collide(CollisionEvent e) {
                        if (thief.getWorld() == null) {
                            thiefCatActive = false;
                        }
                    }
                });
            }
        }
    }
}
