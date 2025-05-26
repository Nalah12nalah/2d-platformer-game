package game.pickups;

import city.cs.engine.*;
import game.sounds.SoundEffectManager;
import game.characters.Cat;
import org.jbox2d.common.Vec2;

/**
 * Represents a food pickup that the Cat can collect to restore resources.
 */
public class FoodPickup extends StaticBody implements SensorListener {
    private static final Shape shape = new CircleShape(0.5f);
    private static final BodyImage foodImage = new BodyImage("data/Pigeon/Food.png", 1.5f);

    private final Sensor sensor;

    /**
     * Creates a food pickup at a specific position in the world.
     * @param world the game world
     * @param position the location to spawn the food
     */
    public FoodPickup(World world, Vec2 position) {
        super(world);
        setPosition(position);
        addImage(foodImage);

        sensor = new Sensor(this, shape);
        sensor.addSensorListener(this);
    }

    /**
     * When the Cat touches the food, collect it and play a sound.
     */
    @Override
    public void beginContact(SensorEvent e) {
        if (e.getContactBody() instanceof Cat cat) {
            cat.pickUpFood();
            SoundEffectManager.play("data/Sounds/Pickup.wav", true);
            destroy();
        }
    }

    @Override
    public void endContact(SensorEvent e) {
        // No behavior on end contact
    }
}
