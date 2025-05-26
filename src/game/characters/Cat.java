package game.characters;

import city.cs.engine.*;
import javax.swing.Timer;

import game.projectiles.RockProjectile;
import game.utils.Destructible;
import game.projectiles.NubellaProjectile;
import game.sounds.SoundEffectManager;
import org.jbox2d.common.Vec2;

/**
 * Main player character (the Cat) that the player controls.
 * Handles movement, combat, animations, pickups, and special abilities.
 */
public class Cat extends DynamicBody implements StepListener {

    /**
     * Represents different states the Cat can be in.
     */
    private enum State {
        IDLE, WALKING, JUMPING, SCRATCHING, EATING, SHIELDING, CHARGING_NUBELLA
    }

    private State currentState;

    // Cat body parts
    private static final Shape headShape = new BoxShape(0.6f, 0.6f, new Vec2(1.0f, 1.2f));
    private static final Shape bodyShape = new BoxShape(1.2f, 0.5f, new Vec2(0, 0));
    private static final Shape tailShape = new BoxShape(0.3f, 0.7f, new Vec2(-1.4f, 0.8f));
    private static final Shape frontLeftLeg = new BoxShape(0.3f, 0.5f, new Vec2(0.9f, -0.7f));
    private static final Shape frontRightLeg = new BoxShape(0.3f, 0.5f, new Vec2(0.3f, -0.7f));
    private static final Shape backLeftLeg = new BoxShape(0.3f, 0.5f, new Vec2(-0.9f, -0.7f));
    private static final Shape backRightLeg = new BoxShape(0.3f, 0.5f, new Vec2(-0.3f, -0.7f));

    // Cat images for different states
    private static final BodyImage idleRight = new BodyImage("data/Characters/CatRight.png", 4f);
    private static final BodyImage idleLeft = new BodyImage("data/Characters/CatLeft.png", 4f);
    private static final BodyImage walk1Right = new BodyImage("data/Characters/CatWalkingRight1.png", 4f);
    private static final BodyImage walk2Right = new BodyImage("data/Characters/CatWalkingRight2.png", 4f);
    private static final BodyImage walk1Left = new BodyImage("data/Characters/CatWalkingLeft1.png", 4f);
    private static final BodyImage walk2Left = new BodyImage("data/Characters/CatWalkingLeft2.png", 4f);
    private static final BodyImage eatingRight = new BodyImage("data/Characters/CatEatingRight.png", 6f);
    private static final BodyImage eatingLeft = new BodyImage("data/Characters/CatEatingLeft.png", 6f);
    private static final BodyImage scratchRight1 = new BodyImage("data/Characters/CatScratchingRight1.png", 5f);
    private static final BodyImage scratchRight2 = new BodyImage("data/Characters/CatScratchingRight2.png", 5f);
    private static final BodyImage scratchLeft1 = new BodyImage("data/Characters/CatScratchingLeft1.png", 5f);
    private static final BodyImage scratchLeft2 = new BodyImage("data/Characters/CatScratchingLeft2.png", 5f);
    private static final BodyImage nubellaChargeRight = new BodyImage("data/Characters/CatChargingRight.png", 5f);
    private static final BodyImage nubellaChargeLeft = new BodyImage("data/Characters/CatChargingLeft.png", 5f);
    private static final BodyImage shieldingRight = new BodyImage("data/Characters/CatShieldingRight.png", 6f);
    private static final BodyImage shieldingLeft = new BodyImage("data/Characters/CatShieldingLeft.png", 6f);

    // Private flags and counters
    private boolean facingRight = true;
    private int walkFrame = 0;
    private int frameCounter = 0;

    private int foodCount = 0;
    private int health = 100;

    private Destructible scratchTarget;
    private int scratchCounter = 0;
    private int scratchFrame = 0;

    private int nubellaCount = 0;
    private long nubellaChargeStartTime = 0;
    private boolean showNubellaBar = false;
    private float nubellaChargeProgress = 0f; // 0.0 to 1.0

    private int rugCounter = 0;
    private boolean isUsingRug = false;

    private boolean teleporting = false;

    /**
     * Creates a new Cat character in the given world.
     * @param world the game world
     */
    public Cat(World world) {
        super(world);

        // Build the Cat's body from shapes
        new SolidFixture(this, headShape);
        new SolidFixture(this, bodyShape);
        new SolidFixture(this, tailShape);
        new SolidFixture(this, frontLeftLeg);
        new SolidFixture(this, frontRightLeg);
        new SolidFixture(this, backLeftLeg);
        new SolidFixture(this, backRightLeg);

        addImage(idleRight);
        setGravityScale(2);
        world.addStepListener(this);

        currentState = State.IDLE;
    }

    /**
     * Changes the Cat's current state and updates animation.
     */
    private void changeState(State newState) {
        if (currentState != newState) {
            currentState = newState;
            updateAnimation();
        }
    }

    /**
     * Updates the Cat's animation based on its state and direction.
     */
    private void updateAnimation() {
        removeAllImages();

        switch (currentState) {
            case IDLE -> addImage(facingRight ? idleRight : idleLeft);
            case JUMPING -> addImage(facingRight ? idleRight : idleLeft);
            case WALKING -> addImage(facingRight ? walk1Right : walk1Left);
            case SCRATCHING -> addImage(facingRight ? scratchRight1 : scratchLeft1);
            case EATING -> addImage(facingRight ? eatingRight : eatingLeft);
            case SHIELDING -> addImage(facingRight ? shieldingRight : shieldingLeft);
            case CHARGING_NUBELLA -> addImage(facingRight ? nubellaChargeRight : nubellaChargeLeft);
        }
    }

    // Movement
    /** Starts walking left or right at a given speed. */
    public void startWalking(int speed) {
        setLinearVelocity(new Vec2(speed, getLinearVelocity().y));
        facingRight = speed > 0;
        changeState(State.WALKING);
    }

    /** Stops walking and returns to idle. */
    public void stopWalking() {
        setLinearVelocity(new Vec2(0, getLinearVelocity().y));
        changeState(State.IDLE);
    }

    /** Makes the cat jump if it's on the ground. */
    public void jump(float speed) {
        Vec2 v = getLinearVelocity();
        if (Math.abs(v.y) < 0.01f) {
            setLinearVelocity(new Vec2(v.x, speed));
            changeState(State.JUMPING);
        }
    }
    // Rock
    /** Throws a rock projectile in the direction the cat is facing. */
    public void throwRock(World world) {
        float rockSpeed = facingRight ? 10 : -10;
        Vec2 rockStartPos = this.getPosition().add(new Vec2(facingRight ? 1.5f : -1.5f, 1));
        new RockProjectile(world, rockStartPos, rockSpeed);
        SoundEffectManager.play("data/Sounds/StoneThrow.WAV");
    }

    // Combat
    /** Starts scratching a destructible object. */
    public void startScratching(Destructible target) {
        if (target == null || currentState == State.SCRATCHING) return;

        this.scratchTarget = target;
        this.scratchCounter = 0;

        SoundEffectManager.play("data/Sounds/CatScratching.WAV", true);
        changeState(State.SCRATCHING);
    }

    /** Stops scratching. */
    public void stopScratching() {
        if (currentState == State.SCRATCHING) {
            SoundEffectManager.stop("data/Sounds/CatScratching.WAV");
            scratchTarget = null;
            scratchCounter = 0;
            changeState(State.IDLE);
        }
    }

    // Nubella
    /** Starts charging a Nubella projectile if the cat has one. */
    public void startChargingNubella() {
        if (nubellaCount > 0 && currentState != State.CHARGING_NUBELLA) {
            nubellaChargeStartTime = System.currentTimeMillis();
            SoundEffectManager.play("data/Sounds/NubellaCharging.WAV", false);
            changeState(State.CHARGING_NUBELLA);

            showNubellaBar = true;
            nubellaChargeProgress = 0f;
        }
    }

    /** Releases a charged Nubella projectile based on how long the player held. */
    public void releaseNubella() {
        if (currentState == State.CHARGING_NUBELLA) {
            SoundEffectManager.stop("data/Sounds/NubellaCharging.WAV");

            if (nubellaCount > 0) {
                nubellaCount--;
                long held = System.currentTimeMillis() - nubellaChargeStartTime;
                float power = Math.min(held / 100f, 20f);
                float speed = facingRight ? power : -power;
                Vec2 pos = getPosition().add(new Vec2(facingRight ? 2f : -2f, 3));
                new NubellaProjectile(getWorld(), pos, speed);

                SoundEffectManager.play("data/Sounds/NubellaRelease.WAV", false);
            }

            showNubellaBar = false;
            nubellaChargeProgress = 0f;
            changeState(State.IDLE);
        }
    }

    // Shielding
    /** Activates a temporary shield using the collected rug. */
    public void startShielding() {
        if (rugCounter > 0 && !isUsingRug && currentState != State.SHIELDING) {
            rugCounter--;
            isUsingRug = true;

            changeState(State.SHIELDING);
        }
    }

    /** Stops shielding if active. */
    public void stopShielding() {
        if (isUsingRug && currentState == State.SHIELDING) {
            isUsingRug = false;
            changeState(State.IDLE);
        }
    }

    // Eating
    /** Eats food to regain health if available. */
    public void eatFood() {
        if (foodCount > 0 && health < 100 && currentState != State.EATING) {
            foodCount--;
            health += 10;
            SoundEffectManager.play("data/Sounds/eating.wav", false);
            changeState(State.EATING);

            Timer eatTimer = new Timer(1000, e -> changeState(State.IDLE));
            eatTimer.setRepeats(false);
            eatTimer.start();
        }
    }

    /** Reduces health when taking damage. */
    public void takeDamage(int amount) {
        health -= amount;
    }

    // StepListener methods
    @Override
    public void preStep(StepEvent e) {
        // Prevent tipping over
        if (Math.abs(this.getAngle()) > Math.PI / 3) {
            this.setAngle(0);
        }

        frameCounter++;
        if (frameCounter >= 10) {
            frameCounter = 0;
            if (currentState == State.WALKING) {
                switchWalkingFrame();
            }
        }

        if (currentState == State.SCRATCHING && scratchTarget != null) {
            scratchCounter++;

            if (scratchCounter % 10 == 0) {
                // Switch scratching animation
                removeAllImages();
                addImage(facingRight ? (scratchFrame == 0 ? scratchRight1 : scratchRight2)
                        : (scratchFrame == 0 ? scratchLeft1 : scratchLeft2));
                scratchFrame = 1 - scratchFrame;
            }

            if (scratchCounter % 30 == 0) {
                scratchTarget.takeDamage();

                if (scratchTarget.getHealth() <= 0 || scratchTarget.getBody().getWorld() == null) {
                    stopScratching();
                }
            }
        }
    }

    @Override
    public void postStep(StepEvent e) {
        // Update Nubella charge bar
        if (currentState == State.CHARGING_NUBELLA && showNubellaBar) {
            long heldTime = System.currentTimeMillis() - nubellaChargeStartTime;
            nubellaChargeProgress = Math.min(heldTime / 2000f, 1.0f); // 2 seconds full bar
        }
    }

    /** Switches between walking frames to create animation. */
    private void switchWalkingFrame() {
        removeAllImages();
        if (facingRight) {
            addImage((walkFrame == 0) ? walk1Right : walk2Right);
        } else {
            addImage((walkFrame == 0) ? walk1Left : walk2Left);
        }
        walkFrame = 1 - walkFrame;
    }

    // State Getters
    /** Returns true if cat is currently scratching. */
    public boolean isScratching() {
        return currentState == State.SCRATCHING;
    }

    /** Returns true if cat is charging Nubella. */
    public boolean isChargingNubella() {
        return currentState == State.CHARGING_NUBELLA;
    }

    /** Returns true if the cat is teleporting. */
    public boolean isTeleporting() {
        return teleporting;
    }

    /** Returns true if cat is using a rug to shield. */
    public boolean isUsingRug() {
        return isUsingRug;
    }

    // Pickup methods
    /** Increases rug counter when picking up a rug. */
    public void pickUpRug() {
        rugCounter++;
    }

    /** Increases food counter when picking up food. */
    public void pickUpFood() {
        foodCount++;
    }

    /** Increases Nubella counter when picking up a Nubella. */
    public void pickUpNubella() {
        nubellaCount++;
    }

    // Setters
    /** Sets food counter value. */
    public void setFoodCount(int count) {
        this.foodCount = count;
    }

    /** Sets health counter value. */
    public void setHealth(int count) {
        this.health = count;
    }

    /** Sets teleporting status. */
    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }

    // Getters
    /** Returns the current health value. */
    public int getHealth() {
        return health;
    }

    /** Returns the current food count. */
    public int getFoodCount() {
        return foodCount;
    }

    /** Returns the current Nubella count. */
    public int getNubellaCount() {
        return nubellaCount;
    }

    /** Returns the charge progress for Nubella (0.0 - 1.0). */
    public float getNubellaChargeProgress() {
        if (currentState == State.CHARGING_NUBELLA) {
            long held = System.currentTimeMillis() - nubellaChargeStartTime;
            return Math.min(held / 2000f, 1f);
        }
        return 0;
    }

    /** Returns how many rugs the cat has collected. */
    public int getRugCounter() {
        return rugCounter;
    }
}
