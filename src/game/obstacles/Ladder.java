package game.obstacles;

import city.cs.engine.*;
import game.characters.Cat;
import org.jbox2d.common.Vec2;

/**
 * A ladder made of stacked sections that teleports the cat up or down.
 */
public class Ladder implements SensorListener {
    private final StaticBody topSensorBody;
    private final StaticBody bottomSensorBody;
    private final Vec2 topTeleportPosition;
    private final Vec2 bottomTeleportPosition;

    /**
     * Creates a new ladder.
     * @param world the world
     * @param basePosition bottom starting position
     * @param totalHeight total height of the ladder
     */
    public Ladder(World world, Vec2 basePosition, float totalHeight) {
        float sectionHeight = 4.0f;
        int numberOfSections = Math.round(totalHeight / sectionHeight);
        float actualHeight = numberOfSections * sectionHeight;

        // Create stacked visual sections
        for (int i = 0; i < numberOfSections; i++) {
            float yOffset = i * sectionHeight;
            StaticBody section = new StaticBody(world);
            section.setPosition(new Vec2(basePosition.x, basePosition.y + yOffset));

            if (i == 0) {
                section.addImage(new BodyImage("data/Props/LadderBottom.png", sectionHeight));
            } else if (i == numberOfSections - 1) {
                section.addImage(new BodyImage("data/Props/LadderTop.png", sectionHeight));
            } else {
                section.addImage(new BodyImage("data/Props/LadderMiddle.png", sectionHeight));
            }
        }

        // Sensors at the top and bottom
        bottomSensorBody = new StaticBody(world);
        bottomSensorBody.setPosition(basePosition);
        topSensorBody = new StaticBody(world);
        topSensorBody.setPosition(new Vec2(basePosition.x, basePosition.y + actualHeight));

        Shape sensorShape = new BoxShape(0.4f, 0.2f);
        new Sensor(bottomSensorBody, sensorShape).addSensorListener(this);
        new Sensor(topSensorBody, sensorShape).addSensorListener(this);

        // Teleportation points
        topTeleportPosition = topSensorBody.getPosition().add(new Vec2(2, 2f));
        bottomTeleportPosition = bottomSensorBody.getPosition().add(new Vec2(2, 2f));
    }

    /**
     * Handles teleporting when the cat touches the sensors.
     */
    @Override
    public void beginContact(SensorEvent e) {
        if (e.getContactBody() instanceof Cat cat && !cat.isTeleporting()) {
            Vec2 catPos = cat.getPosition();
            float distToBottom = catPos.sub(bottomSensorBody.getPosition()).length();
            float distToTop = catPos.sub(topSensorBody.getPosition()).length();

            if (distToBottom < 1.5f) {
                cat.setPosition(topTeleportPosition);
                cat.setTeleporting(true);
            } else if (distToTop < 1.5f) {
                cat.setPosition(bottomTeleportPosition);
                cat.setTeleporting(true);
            }
        }
    }

    /**
     * Resets teleporting flag when the cat leaves the sensor.
     */
    @Override
    public void endContact(SensorEvent e) {
        if (e.getContactBody() instanceof Cat cat) {
            cat.setTeleporting(false);
        }
    }
}
