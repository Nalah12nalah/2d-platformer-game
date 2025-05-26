package game.obstacles;

import city.cs.engine.*;
import city.cs.engine.Shape;
import org.jbox2d.common.Vec2;

import java.awt.*;

/**
 * A static platform made up of tiled blocks for the player to walk on.
 */
public class Platform extends StaticBody {

    /**
     * Creates a platform by stacking base and top tiles.
     * @param world the game world
     * @param widthInBlocks number of horizontal tiles
     * @param baseHeight number of vertical tiles for the base
     * @param topY y-position of the top row
     * @param centerX center x-position
     * @param topImagePath image path for the top layer
     * @param baseImagePath image path for the base tiles
     */
    public Platform(World world, int widthInBlocks, int baseHeight, float topY, float centerX,
                    String topImagePath, String baseImagePath) {
        super(world);

        float tileSize = 3f;
        float width = widthInBlocks * tileSize;
        float height = baseHeight * tileSize;
        float centerY = topY - height / 2f;

        this.setPosition(new Vec2(centerX, centerY));
        Shape shape = new BoxShape(width / 2f, height / 2f);
        new GhostlyFixture(this, shape); // Makes it collide but invisible

        this.setFillColor(new Color(0, 0, 0, 0));
        this.setLineColor(new Color(0, 0, 0, 0));

        // Create stacked base images
        for (int y = 0; y < baseHeight; y++) {
            for (int x = 0; x < widthInBlocks; x++) {
                float tileX = centerX - width / 2f + (x + 0.5f) * tileSize;
                float tileY = topY - (y + 1) * tileSize + tileSize / 2f;

                new AttachedImage(
                        this,
                        new BodyImage(baseImagePath, tileSize),
                        1f,
                        0f,
                        new Vec2(tileX - centerX, tileY - centerY)
                );
            }
        }

        // Create solid top blocks
        float topRowY = topY;
        for (int x = 0; x < widthInBlocks; x++) {
            float tileX = centerX - width / 2f + (x + 0.5f) * tileSize;
            StaticBody topTile = new StaticBody(world, new BoxShape(tileSize / 2f, tileSize / 2f));
            topTile.setPosition(new Vec2(tileX, topRowY));
            topTile.addImage(new BodyImage(topImagePath, tileSize));
        }
    }
}
