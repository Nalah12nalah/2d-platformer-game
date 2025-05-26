package game.obstacles;

import city.cs.engine.*;
import game.characters.Cat;
import game.main.GameWorld;
import game.pickups.NubellaPickup;
import game.sounds.SoundEffectManager;
import game.utils.Destructible;
import org.jbox2d.common.Vec2;

/**
 * A destructible cardboard box that drops a Nubella when broken.
 */
public class CardboardBox extends StaticBody implements Destructible {
    private static final Shape boxShape = new BoxShape(1.5f, 1.0f);
    private int hitCount = 0;
    private static final int MAX_HITS = 8;
    private final Cat cat;
    private boolean isDestroyed = false;

    /**
     * Creates a new cardboard box at a given position.
     * @param world the game world
     * @param position where to place the box
     * @param cat the cat (player) instance
     * @param type type of box image (open, closed, right-facing)
     */
    public CardboardBox(World world, Vec2 position, Cat cat, String type) {
        super(world, boxShape);
        this.cat = cat;
        setPosition(position);

        // Select the correct image based on box type
        String imagePath;
        if ("open".equalsIgnoreCase(type)) {
            imagePath = "data/Obstacles/CardboardBoxOpenUpwards.png";
        } else if ("closed".equalsIgnoreCase(type)) {
            imagePath = "data/Obstacles/CardboardBoxClosed.png";
        } else {
            imagePath = "data/Obstacles/CardboardBoxOpenRight.png";
        }

        addImage(new BodyImage(imagePath, 4f));

        if (world instanceof GameWorld gw) {
            gw.addDestructible(this);
        }
    }

    /**
     * Deals damage to the box. If broken, drops a Nubella pickup.
     */
    @Override
    public void takeDamage() {
        hitCount++;
        if (hitCount >= MAX_HITS) {
            new NubellaPickup(getWorld(), getPosition());
            destroy();
            SoundEffectManager.play("data/Sounds/BoxBreak.wav");
        }
    }

    /**
     * Returns the remaining health of the box.
     */
    @Override
    public int getHealth() {
        return MAX_HITS - hitCount;
    }

    /**
     * Returns the body of this box.
     */
    @Override
    public Body getBody() {
        return this;
    }

    /**
     * Destroys the box and removes it from the world's destructible list.
     */
    @Override
    public void destroy() {
        if (getWorld() instanceof GameWorld gw) {
            gw.getDestructibles().remove(this);
            isDestroyed = true;
        }
        super.destroy();
    }

    /**
     * Returns whether the box has been destroyed.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }
}
