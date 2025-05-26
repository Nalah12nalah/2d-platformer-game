package game.pickups;

import city.cs.engine.*;
import game.sounds.SoundEffectManager;
import game.characters.Cat;
import org.jbox2d.common.Vec2;

/**
 * Represents a rug pickup that the Cat can collect for shielding.
 */
public class RugPickup extends StaticBody implements SensorListener {
    private static final Shape shape = new CircleShape(0.5f);
    private static final BodyImage rugImage = new BodyImage("data/Props/rugIcon.png", 12f);

    private final Sensor sensor;

    /**
     * Creates a Rug pickup at a specific position in the world.
     * @param world the game world
     * @param position the location to spawn the rug
     */
    public RugPickup(World world, Vec2 position) {
        super(world);
        setPosition(position);
        addImage(rugImage);

        sensor = new Sensor(this, shape);
        sensor.addSensorListener(this);
    }

    /**
     * When the Cat touches the rug, collect it and play a sound.
     */
    @Override
    public void beginContact(SensorEvent e) {
        if (e.getContactBody() instanceof Cat cat) {
            cat.pickUpRug();
            SoundEffectManager.play("data/Sounds/Pickup.wav", true);
            destroy();
        }
    }

    @Override
    public void endContact(SensorEvent e) {
        // No behavior on end contact
    }
}
