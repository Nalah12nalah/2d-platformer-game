package game.projectiles;

import city.cs.engine.*;
import game.characters.Pigeon;
import game.characters.ThiefCat;
import game.sounds.SoundEffectManager;
import org.jbox2d.common.Vec2;

/**
 * A projectile fired by the Cat that deals double damage to enemies.
 */
public class NubellaProjectile extends DynamicBody implements CollisionListener {

    private static final Shape nubellaShape = new CircleShape(0.6f);
    private static final BodyImage nubellaImage = new BodyImage("data/Weapons/NubellaProjectile.png", 1.2f);

    /**
     * Creates a Nubella projectile with a starting position and horizontal velocity.
     * @param world the game world
     * @param position the starting position of the projectile
     * @param velocityX the horizontal speed (positive for right, negative for left)
     */
    public NubellaProjectile(World world, Vec2 position, float velocityX) {
        super(world, nubellaShape);
        setPosition(position);
        addImage(nubellaImage);
        setLinearVelocity(new Vec2(velocityX, 2)); // Moves slightly upward
        setGravityScale(0.3f); // Falls slowly

        addCollisionListener(this);
    }

    /**
     * Handles collision with enemies or objects.
     */
    @Override
    public void collide(CollisionEvent e) {
        Body other = e.getOtherBody();

        if (other instanceof Pigeon pigeon) {
            SoundEffectManager.play("data/Sounds/PigeonHit.WAV", true);
            pigeon.nubellaHit(); // Deals double damage
            this.destroy();
        } else if (other instanceof ThiefCat thief) {
            SoundEffectManager.play("data/Sounds/ThiefCatHit.WAV", true);
            thief.nubellaHit(); // Deals double damage
            this.destroy();
        } else {
            // Destroy on any other collision
            this.destroy();
        }
    }
}
