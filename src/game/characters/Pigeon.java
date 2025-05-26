package game.characters;

import city.cs.engine.*;
import game.projectiles.RockProjectile;
import game.pickups.FoodPickup;
import game.sounds.SoundEffectManager;
import org.jbox2d.common.Vec2;

import javax.swing.*;

/**
 * Enemy pigeon character that moves back and forth and attacks the player if close enough.
 */
public class Pigeon extends Walker implements CollisionListener {

    // Shape of the pigeon
    private static final Shape pigeonShape = new PolygonShape(
            -1.5f, 0.8f, 1.5f, 0.8f, -1.5f, -0.6f
    );

    // Pigeon images
    private BodyImage pigeonRight = new BodyImage("data/Enemies/PigeonRight.png", 2f);
    private BodyImage pigeonLeft = new BodyImage("data/Enemies/PigeonLeft.png", 2f);
    private BodyImage pigeonAttackRight = new BodyImage("data/Enemies/PigeonAttackingRight.png", 3.5f);
    private BodyImage pigeonAttackLeft = new BodyImage("data/Enemies/PigeonAttackingLeft.png", 3.5f);

    // Flags and timers
    private boolean movingRight = true;
    private Timer moveTimer;
    private Timer attackCheckTimer;
    private boolean attackCooldown = false;
    private Cat cat; // reference to player
    private static final float ATTACK_RANGE = 6f;
    private boolean attacking = false;
    private boolean destroyed = false;
    private int MAX_HITS = 2;

    /**
     * Creates a Pigeon enemy at a specific position.
     * @param world the world the pigeon belongs to
     * @param position where to spawn the pigeon
     * @param cat reference to the player's cat
     */
    public Pigeon(World world, Vec2 position, Cat cat) {
        super(world, pigeonShape);
        this.cat = cat;
        setPosition(position);
        addImage(pigeonRight);
        addCollisionListener(this);

        // Timer to switch movement direction every few seconds
        moveTimer = new Timer(2000, e -> {
            if (!attacking) {
                switchDirection();
            }
        });
        moveTimer.start();
        startMoving();

        // Timer to keep checking if Cat is close enough to attack
        attackCheckTimer = new Timer(500, e -> checkForAttack());
        attackCheckTimer.start();
    }

    /** Starts moving in the current direction. */
    private void startMoving() {
        setLinearVelocity(new Vec2(2, 0));
    }

    /** Switches movement direction left or right. */
    private void switchDirection() {
        movingRight = !movingRight;
        removeAllImages();
        addImage(movingRight ? pigeonRight : pigeonLeft);
        setLinearVelocity(new Vec2(movingRight ? 2 : -2, 0));
    }

    /** Checks if Cat is close enough to attack, and acts accordingly. */
    private void checkForAttack() {
        if (cat == null || destroyed) return;

        if (attackCooldown) {
            // If on cooldown, keep moving normally
            if (attacking) {
                exitAttackMode();
            }
            return;
        }

        float distance = this.getPosition().sub(cat.getPosition()).length();
        if (distance < ATTACK_RANGE) {
            enterAttackMode();
        } else {
            exitAttackMode();
        }
    }

    /** Starts attack mode: pigeon chases cat. */
    private void enterAttackMode() {
        if (!attacking) attacking = true;

        Vec2 direction = cat.getPosition().sub(this.getPosition());
        direction.normalize();
        setLinearVelocity(direction.mul(3));

        removeAllImages();
        if (cat.getPosition().x > this.getPosition().x) {
            addImage(pigeonAttackRight);
            movingRight = true;
        } else {
            addImage(pigeonAttackLeft);
            movingRight = false;
        }
    }

    /** Exits attack mode and resumes normal patrol. */
    private void exitAttackMode() {
        if (attacking) {
            attacking = false;
            removeAllImages();
            addImage(movingRight ? pigeonRight : pigeonLeft);
            setLinearVelocity(new Vec2(movingRight ? 2 : -2, 0));
        }
    }

    /** Starts the attack cooldown so pigeon can't attack immediately again. */
    private void startAttackCooldown() {
        attackCooldown = true;

        Timer cooldownTimer = new Timer(5000, e -> attackCooldown = false);
        cooldownTimer.setRepeats(false);
        cooldownTimer.start();
    }

    /** Handles being hit by a Nubella projectile (more powerful). */
    public void nubellaHit() {
        MAX_HITS -= 2;
        if (MAX_HITS <= 0 && !destroyed) {
            World world = this.getWorld();
            new FoodPickup(world, this.getPosition()); // drop food
            destroy();
        }
    }

    /** Handles being hit by a regular rock projectile. */
    public void rockHit() {
        MAX_HITS--;
        if (MAX_HITS <= 0 && !destroyed) {
            World world = this.getWorld();
            new FoodPickup(world, this.getPosition()); // drop food
            destroy();
        }
    }

    @Override
    public void collide(CollisionEvent e) {
        if (destroyed) return;

        if (e.getOtherBody() instanceof RockProjectile) {
            rockHit();
        } else if (e.getOtherBody() instanceof Cat cat) {
            if (!attackCooldown) {
                SoundEffectManager.play("data/Sounds/Hiss.WAV");
                cat.takeDamage(10);
                startAttackCooldown();
                exitAttackMode();
            }
        }
    }

    /** Destroys the pigeon, stopping timers and marking it as dead. */
    @Override
    public void destroy() {
        if (moveTimer != null) moveTimer.stop();
        if (attackCheckTimer != null) attackCheckTimer.stop();
        super.destroy();
        this.destroyed = true;
    }

    /** Checks if the pigeon is still alive. */
    public boolean isAlive() {
        return !this.destroyed;
    }
}
