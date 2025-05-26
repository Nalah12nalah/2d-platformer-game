package game.projectiles;

import city.cs.engine.*;
import game.characters.Pigeon;
import game.characters.ThiefCat;
import game.sounds.SoundEffectManager;
import org.jbox2d.common.Vec2;

/**
 * A simple rock projectile thrown by the Cat.
 * Damages enemies like Pigeons and ThiefCats.
 */
public class RockProjectile extends DynamicBody implements CollisionListener, StepListener {

    private static final Shape rockShape = new CircleShape(0.5f);
    private static final BodyImage rockImage = new BodyImage("data/Weapons/Rock.png", 1.5f);

    /**
     * Creates a RockProjectile at a given position and speed.
     * @param world the game world
     * @param position the starting position
     * @param speed the horizontal speed (positive for right, negative for left)
     */
    public RockProjectile(World world, Vec2 position, float speed) {
        super(world, rockShape);
        addImage(rockImage);
        setPosition(position.add(new Vec2(0, 2))); // Slightly above the cat

        setLinearVelocity(new Vec2(speed, 3)); // Moves right/left and slightly upwards

        addCollisionListener(this);
        world.addStepListener(this);
    }

    /**
     * Handles what happens when the rock collides with something.
     */
    @Override
    public void collide(CollisionEvent e) {
        Body other = e.getOtherBody();

        if (other instanceof Pigeon pigeon) {
            SoundEffectManager.play("data/Sounds/PigeonHit.WAV", true);
            pigeon.rockHit(); // Reduces pigeon health
            this.destroy();
        } else if (other instanceof ThiefCat thief) {
            SoundEffectManager.play("data/Sounds/ThiefCatHit.WAV", true);
            thief.rockHit(); // Reduces thief cat health
            this.destroy();
        } else {
            // Destroy rock on any other collision
            this.destroy();
        }
    }

    /**
     * Not used in this class, required by StepListener.
     */
    @Override
    public void preStep(StepEvent stepEvent) {}

    /**
     * Not used in this class, required by StepListener.
     */
    @Override
    public void postStep(StepEvent stepEvent) {}
}
