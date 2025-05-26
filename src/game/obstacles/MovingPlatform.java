package game.obstacles;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

/**
 * A moving platform that moves horizontally or vertically between set limits.
 */
public class MovingPlatform extends StaticBody implements StepListener {
    private float speed = 1.5f;
    private boolean movingUp = true;
    private boolean movingRight = true;
    private float upperLimit;
    private float lowerLimit;
    private float leftLimit;
    private float rightLimit;
    private boolean vertical; // true for vertical movement, false for horizontal

    /**
     * Creates a moving platform at a specified position.
     * @param world the game world
     * @param width platform width
     * @param height platform height
     * @param position starting position
     */
    public MovingPlatform(World world, float width, float height, Vec2 position) {
        super(world, new BoxShape(width / 2, height / 2));
        setPosition(new Vec2(position.x, position.y - (height / 2)));
        world.addStepListener(this);
    }

    /**
     * Starts vertical movement.
     * @param speed platform speed
     */
    public void startMovingVertically(float speed) {
        this.speed = speed;
        this.vertical = true;
    }

    /**
     * Starts horizontal movement.
     * @param speed platform speed
     */
    public void startMovingHorizontally(float speed) {
        this.speed = speed;
        this.vertical = false;
    }

    public void setUpperLimit(float limit) {
        this.upperLimit = limit;
    }

    public void setLowerLimit(float limit) {
        this.lowerLimit = limit;
    }

    public void setLeftLimit(float limit) {
        this.leftLimit = limit;
    }

    public void setRightLimit(float limit) {
        this.rightLimit = limit;
    }

    /**
     * Moves the platform slightly every frame according to its direction.
     */
    @Override
    public void preStep(StepEvent e) {
        Vec2 position = getPosition();

        if (vertical) { // Vertical movement
            if (movingUp) {
                setPosition(new Vec2(position.x, position.y + speed * 0.05f));
                if (position.y >= upperLimit) movingUp = false;
            } else {
                setPosition(new Vec2(position.x, position.y - speed * 0.05f));
                if (position.y <= lowerLimit) movingUp = true;
            }
        } else { // Horizontal movement
            if (movingRight) {
                setPosition(new Vec2(position.x + speed * 0.05f, position.y));
                if (position.x >= rightLimit) movingRight = false;
            } else {
                setPosition(new Vec2(position.x - speed * 0.05f, position.y));
                if (position.x <= leftLimit) movingRight = true;
            }
        }
    }

    @Override
    public void postStep(StepEvent e) {
        // Nothing needed here
    }
}
