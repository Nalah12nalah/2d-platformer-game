package game.characters;

import city.cs.engine.*;
import game.main.GameWorld;
import game.projectiles.RockProjectile;
import game.sounds.SoundEffectManager;
import org.jbox2d.common.Vec2;
import java.util.Random;

/**
 * Thief Cat enemy that sneaks towards the player, hisses, steals food, jumps over obstacles if stuck,
 * and retreats after stealing or being hit.
 */
public class ThiefCat extends DynamicBody implements StepListener, CollisionListener {

    private static final Shape catShape = new BoxShape(1.2f, 0.6f);

    private final Cat cat;
    private final GameWorld world;

    // ThiefCat images
    private final BodyImage walkRight1 = new BodyImage("data/ThiefCat/ThiefWalkRight1.png", 4f);
    private final BodyImage walkRight2 = new BodyImage("data/ThiefCat/ThiefWalkRight2.png", 4f);
    private final BodyImage walkLeft1 = new BodyImage("data/ThiefCat/ThiefWalkLeft1.png", 4f);
    private final BodyImage walkLeft2 = new BodyImage("data/ThiefCat/ThiefWalkLeft2.png", 4f);
    private final BodyImage hissRight = new BodyImage("data/ThiefCat/HissRight.png", 4f);
    private final BodyImage hissLeft = new BodyImage("data/ThiefCat/HissLeft.png", 4f);
    private final BodyImage stealRight = new BodyImage("data/ThiefCat/StealRight.png", 4f);
    private final BodyImage stealLeft = new BodyImage("data/ThiefCat/StealLeft.png", 4f);

    private boolean facingRight;
    private boolean isHissing = false;
    private boolean isStealing = false;
    private boolean isRetreating = false;
    private int frameCounter = 0;
    private int walkFrame = 0;
    private int hissTimer = 0;
    private int hissDuration = 0;
    private int hitPoints = 4;
    private int stuckTimer = 0;

    /**
     * Creates a ThiefCat enemy near the player.
     *
     * @param world the game world
     * @param cat   the player character to target
     */
    public ThiefCat(GameWorld world, Cat cat) {
        super(world, catShape);
        this.world = world;
        this.cat = cat;

        setGravityScale(2);

        float spawnX = cat.getPosition().x + (new Random().nextBoolean() ? 10 : -10);
        setPosition(new Vec2(spawnX, cat.getPosition().y + 4));
        facingRight = cat.getPosition().x > spawnX;

        addImage(facingRight ? walkRight1 : walkLeft1);
        setLinearVelocity(new Vec2(facingRight ? 2.5f : -2.5f, 0));

        world.addStepListener(this);
        addCollisionListener(this);
    }

    /**
     * Makes the ThiefCat retreat and remove itself from the world.
     */
    private void retreatAndRemove() {
        isRetreating = true;
        setLinearVelocity(new Vec2(facingRight ? -6f : 6f, 0));

        world.addStepListener(new StepListener() {
            @Override
            public void preStep(StepEvent e) {
                frameCounter++;
                if (frameCounter >= 10) {
                    frameCounter = 0;
                    removeAllImages();
                    addImage(facingRight ? (walkFrame == 0 ? walkRight1 : walkRight2)
                            : (walkFrame == 0 ? walkLeft1 : walkLeft2));
                    walkFrame = 1 - walkFrame;
                }
                if (Math.abs(getPosition().x - cat.getPosition().x) > 20) {
                    destroy();
                    world.removeStepListener(this);
                }
            }

            @Override
            public void postStep(StepEvent e) {
            }
        });
    }

    /**
     * Called when hit by a Nubella projectile (stronger hit).
     */
    public void nubellaHit() {
        hitPoints -= 2;
        if (hitPoints <= 0 && !isRetreating) {
            retreatAndRemove();
        }
    }

    /**
     * Called when hit by a rock projectile (normal hit).
     */
    public void rockHit() {
        hitPoints--;
        if (hitPoints <= 0 && !isRetreating) {
            retreatAndRemove();
        }
    }

    @Override
    public void preStep(StepEvent e) {
        // Auto-destroy if falls below platform
        if (getPosition().y < -20) {
            destroy();
            return;
        }

        // Destroy if far away from cat
        if (cat != null && getPosition().sub(cat.getPosition()).length() > 20) {
            destroy();
            return;
        }

        // Prevent flipping
        if (Math.abs(this.getAngle()) > Math.PI / 3) {
            this.setAngle(0);
        }

        if (isStealing || isRetreating) return;

        Vec2 catPos = cat.getPosition();
        Vec2 myPos = this.getPosition();

        // Handle hissing
        if (!isHissing) {
            hissTimer++;
            if (hissTimer >= 480) {
                isHissing = true;
                hissDuration = 0;
                removeAllImages();
                addImage(facingRight ? hissRight : hissLeft);
                SoundEffectManager.play("data/Sounds/Hiss.wav", false);
                return;
            }
        } else {
            hissDuration++;
            if (hissDuration >= 180) {
                isHissing = false;
                hissTimer = 0;
                removeAllImages();
                addImage(facingRight ? walkRight1 : walkLeft1);
            }
            return;
        }

        // Move towards player
        float speed = (catPos.x > myPos.x) ? 2.5f : -2.5f;
        facingRight = speed > 0;
        setLinearVelocity(new Vec2(speed, getLinearVelocity().y));

        // Check if stuck
        if (Math.abs(getLinearVelocity().x) < 0.3f) {
            stuckTimer++;
            if (stuckTimer >= 100) { // After 5s stuck
                destroy();
                return;
            }
            if (stuckTimer % 50 == 0) { // Every 2.5s, jump
                applyImpulse(new Vec2(facingRight ? 20f : -20f, 25f));
            }
        } else {
            stuckTimer = 0;
        }

        // Walking animation
        frameCounter++;
        if (frameCounter >= 10) {
            frameCounter = 0;
            removeAllImages();
            addImage(facingRight ? (walkFrame == 0 ? walkRight1 : walkRight2)
                    : (walkFrame == 0 ? walkLeft1 : walkLeft2));
            walkFrame = 1 - walkFrame;
        }
    }


    @Override
    public void postStep(StepEvent stepEvent) {

    }

    @Override
    public void collide(CollisionEvent e) {
        if (isRetreating) return;

        if (e.getOtherBody() instanceof RockProjectile) {
            hitPoints--;
            if (hitPoints <= 0) {
                retreatAndRemove();
            }
        }

        if (e.getOtherBody() == cat && !isStealing) {
            isStealing = true;
            removeAllImages();
            addImage(facingRight ? stealRight : stealLeft);
            SoundEffectManager.play("data/Sounds/NomNom.wav", false);

            cat.setFoodCount(Math.max(0, cat.getFoodCount() - 1));

            // After stealing, resume moving
            world.addStepListener(new StepListener() {
                int timer = 0;

                @Override
                public void preStep(StepEvent e) {
                    setLinearVelocity(new Vec2(facingRight ? -6f : 6f, 0));
                    timer++;

                    // Add the walking image after stealing
                    if (timer == 1) {
                        removeAllImages();
                        addImage(facingRight ? walkRight1 : walkLeft1);
                        setLinearVelocity(new Vec2(facingRight ? 2.5f : -2.5f, 0));
                    }


                    if (timer >= 150) { // Disappear after ~3 seconds
                        destroy();
                        world.removeStepListener(this);
                    }
                }

                @Override
                public void postStep(StepEvent e) {
                }
            });
        }

    }

}