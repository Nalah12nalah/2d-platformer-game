package game.props;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

/**
 * A simple background prop like a plant, a building, or a cart that does not affect gameplay.
 */
public class BackgroundProp extends StaticBody {

    /**
     * Creates a background prop.
     * @param world the game world
     * @param imagePath path to the image
     * @param scale scale of the image
     * @param position position of the prop
     */
    public BackgroundProp(World world, String imagePath, float scale, Vec2 position) {
        super(world);
        Shape shape = new BoxShape(0.1f, 0.1f);
        new GhostlyFixture(this, shape);

        setPosition(position);
        addImage(new BodyImage(imagePath, scale));
    }
}
