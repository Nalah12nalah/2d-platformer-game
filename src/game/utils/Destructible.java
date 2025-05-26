package game.utils;

import city.cs.engine.Body;
import org.jbox2d.common.Vec2;

/**
 * Interface for destructible game objects that can take damage and be destroyed.
 */
public interface Destructible {

    /**
     * Inflicts damage on the object.
     */
    void takeDamage();

    /**
     * Returns the current health of the object.
     * @return remaining health points
     */
    int getHealth();

    /**
     * Returns the underlying physics body of the object.
     * @return the Body representing this object
     */
    Body getBody();

    /**
     * Returns the position of the object in the world.
     * @return position vector
     */
    Vec2 getPosition();
}
