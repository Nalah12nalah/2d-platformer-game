package game.props;

import city.cs.engine.*;
import game.characters.Cat;
import game.projectiles.NubellaProjectile;
import game.projectiles.RockProjectile;
import game.sounds.SoundEffectManager;
import org.jbox2d.common.Vec2;

import javax.swing.*;

/**
 * A bucket that rolls toward the Cat when approached and damages on collision.
 */
public class RollingPaintBucket extends DynamicBody implements CollisionListener {
    private static final Shape bucketShape = new CircleShape(0.75f);
    private static final BodyImage bucketImage = new BodyImage("data/Props/Bucket.png", 2f);

    private final Cat cat;
    private boolean hasStartedRolling = false;
    private boolean destroyed = false;
    private Timer proximityTimer;
    private Timer autoDestroyTimer;

    /**
     * Creates a paint bucket that rolls toward the Cat if nearby.
     * @param world the game world
     * @param position starting position
     * @param cat the player character
     */
    public RollingPaintBucket(World world, Vec2 position, Cat cat) {
        super(world, bucketShape);
        this.cat = cat;
        setPosition(position);
        addImage(bucketImage);
        addCollisionListener(this);

        setGravityScale(0);
        setLinearVelocity(new Vec2(0, 0));

        proximityTimer = new Timer(200, e -> checkCatProximity());
        proximityTimer.start();
    }

    /**
     * Checks if the Cat is close enough to trigger rolling.
     */
    private void checkCatProximity() {
        if (hasStartedRolling || destroyed || cat == null) return;

        float xDistance = Math.abs(this.getPosition().x - cat.getPosition().x);
        if (xDistance < 8f) {
            startRolling();
        }
    }

    /**
     * Starts the rolling movement and sound.
     */
    private void startRolling() {
        hasStartedRolling = true;
        setGravityScale(2f);
        applyImpulse(new Vec2(-1, 0));

        SoundEffectManager.play("data/Sounds/Rolling.wav", false);

        if (proximityTimer != null) proximityTimer.stop();

        // Auto destroy after 10 seconds
        autoDestroyTimer = new Timer(10000, e -> {
            if (!destroyed) {
                SoundEffectManager.stop("data/Sounds/Rolling.wav");
                destroy();
            }
        });
        autoDestroyTimer.setRepeats(false);
        autoDestroyTimer.start();
    }

    /**
     * Handles collision with Cat or projectiles.
     */
    @Override
    public void collide(CollisionEvent e) {
        if (destroyed) return;

        if (e.getOtherBody() instanceof Cat cat && !cat.isUsingRug()) {
            cat.takeDamage(15);
            SoundEffectManager.stop("data/Sounds/Rolling.wav");
            SoundEffectManager.play("data/Sounds/Hiss.wav", false);
            destroy();
        } else if (e.getOtherBody() instanceof RockProjectile || e.getOtherBody() instanceof NubellaProjectile) {
            SoundEffectManager.stop("data/Sounds/Rolling.wav");
            destroy();
        }
    }

    /**
     * Cleans up timers and destroys the bucket safely.
     */
    @Override
    public void destroy() {
        if (destroyed) return;

        destroyed = true;
        if (proximityTimer != null) proximityTimer.stop();
        if (autoDestroyTimer != null) autoDestroyTimer.stop();
        super.destroy();
    }
}
