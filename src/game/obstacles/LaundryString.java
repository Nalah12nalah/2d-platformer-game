package game.obstacles;

import city.cs.engine.*;
import game.pickups.RugPickup;
import game.characters.Cat;
import org.jbox2d.common.Vec2;

import javax.swing.*;

/**
 * A laundry string that plays an animation and drops a rug when touched by the cat.
 */
public class LaundryString extends StaticBody implements SensorListener {
    private final BodyImage idleImage = new BodyImage("data/Props/ClothesIdle.png", 12f);
    private final BodyImage windyImage1 = new BodyImage("data/Props/ClothesWindy.png", 12f);
    private final BodyImage windyImage2 = new BodyImage("data/Props/ClothesIdle.png", 12f);
    private final BodyImage noRugImage = new BodyImage("data/Props/ClothesNoRug.png", 12f);

    private final Sensor sensor;
    private final World world;
    private final Vec2 rugDropPosition;
    private boolean rugHasFallen = false;

    private AttachedImage currentImage;
    private Timer animationTimer;
    private boolean toggleWindImage = false;

    /**
     * Creates a laundry string with a hidden rug to drop.
     * @param world the world
     * @param position position of the string
     * @param rugDropPosition where the rug will fall
     */
    public LaundryString(World world, Vec2 position, Vec2 rugDropPosition) {
        super(world);
        this.world = world;
        this.rugDropPosition = rugDropPosition;

        setPosition(position);
        Shape clothesShape = new BoxShape(1.5f, 0.5f);
        new GhostlyFixture(this, clothesShape);

        currentImage = addImage(idleImage);

        sensor = new Sensor(this, new BoxShape(10f, 1f, new Vec2(0, -1f)));
        sensor.addSensorListener(this);
    }

    /**
     * Starts the animation and prepares rug drop when the cat touches.
     */
    @Override
    public void beginContact(SensorEvent e) {
        if (rugHasFallen) return;

        if (e.getContactBody() instanceof Cat) {
            startWindAnimation();

            // Drop the rug after 3.5 seconds
            Timer rugDropTimer = new Timer(3500, ev -> dropRug());
            rugDropTimer.setRepeats(false);
            rugDropTimer.start();
        }
    }

    @Override
    public void endContact(SensorEvent e) {
        // No action needed when the cat leaves
    }

    /**
     * Starts a windy animation that toggles images.
     */
    private void startWindAnimation() {
        if (animationTimer != null) return;

        animationTimer = new Timer(400, e -> {
            removeAttachedImage(currentImage);
            currentImage = addImage(toggleWindImage ? windyImage1 : windyImage2);
            toggleWindImage = !toggleWindImage;
        });
        animationTimer.start();
    }

    /**
     * Stops the windy animation.
     */
    private void stopWindAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
    }

    /**
     * Drops the rug and switches to the "no rug" version of the clothes.
     */
    private void dropRug() {
        if (rugHasFallen) return;

        new RugPickup(world, rugDropPosition);

        stopWindAnimation();
        removeAttachedImage(currentImage);
        currentImage = addImage(noRugImage);

        sensor.removeSensorListener(this);
        rugHasFallen = true;
    }
}
