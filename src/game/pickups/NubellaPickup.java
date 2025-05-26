package game.pickups;

import city.cs.engine.*;
import game.sounds.SoundEffectManager;
import game.characters.Cat;
import org.jbox2d.common.Vec2;

/**
 * Represents a Nubella pickup that the Cat can collect as a special weapon.
 */
public class NubellaPickup extends StaticBody implements SensorListener {
    private static final Shape shape = new CircleShape(0.5f);
    private static final BodyImage nubellaImage = new BodyImage("data/Weapons/Nubella.png", 2.5f);

    private final Sensor sensor;

    /**
     * Creates a Nubella pickup at a specific position in the world.
     * @param world the game world
     * @param position the location to spawn the nubella
     */
    public NubellaPickup(World world, Vec2 position) {
        super(world);
        setPosition(position);
        addImage(nubellaImage);

        sensor = new Sensor(this, shape);
        sensor.addSensorListener(this);
    }

    /**
     * When the Cat touches the nubella, collect it and play a sound.
     */
    @Override
    public void beginContact(SensorEvent e) {
        if (e.getContactBody() instanceof Cat cat) {
            cat.pickUpNubella();
            SoundEffectManager.play("data/Sounds/PickUp.wav", true);
            destroy();
        }
    }

    @Override
    public void endContact(SensorEvent e) {
        // No behavior on end contact
    }
}
