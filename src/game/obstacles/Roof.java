package game.obstacles;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

/**
 * A solid roof object made of tiles, which can be rotated at an angle.
 */
public class Roof extends StaticBody {

    /**
     * Creates a roof platform made of rotated tile images.
     * @param world the game world
     * @param widthInBlocks number of tiles horizontally
     * @param topY y-position of the roof
     * @param centerX x-center position
     * @param roofTileImagePath path to the roof tile image
     * @param angleDegrees rotation angle in degrees
     */
    public Roof(World world, int widthInBlocks, float topY, float centerX,
                String roofTileImagePath, float angleDegrees) {
        super(world);

        float tileSize = 3f;
        float width = widthInBlocks * tileSize;
        float height = tileSize;
        float centerY = topY - height / 2f;

        setPosition(new Vec2(centerX, centerY));
        setAngleDegrees(angleDegrees);

        // Create solid fixture
        Shape shape = new BoxShape(width / 2f, height / 2f);
        new SolidFixture(this, shape);

        float angleRadians = (float) Math.toRadians(angleDegrees);

        // Attach rotated images
        for (int x = 0; x < widthInBlocks; x++) {
            float tileX = centerX - width / 2f + (x + 0.5f) * tileSize;
            float tileY = topY - tileSize / 2f;

            float localX = tileX - centerX;
            float localY = tileY - centerY;

            // Rotate positions
            float rotatedX = (float)(localX * Math.cos(angleRadians) - localY * Math.sin(angleRadians));
            float rotatedY = (float)(localX * Math.sin(angleRadians) + localY * Math.cos(angleRadians));

            AttachedImage img = new AttachedImage(
                    this,
                    new BodyImage(roofTileImagePath, tileSize),
                    1f,
                    0f,
                    new Vec2(rotatedX, rotatedY)
            );
            img.setRotation(angleRadians);
        }
    }
}
