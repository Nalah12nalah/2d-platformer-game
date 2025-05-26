package game.props;

import city.cs.engine.*;
import game.characters.Cat;
import game.main.GameWorld;
import game.sounds.SoundEffectManager;
import org.jbox2d.common.Vec2;

/**
 * A cart that plays a Balila seller sound when the Cat gets close.
 */
public class BalilaCart extends StaticBody implements StepListener {
    private static final float TRIGGER_DISTANCE = 6f;
    private final Cat cat;
    private boolean playingSound = false;

    private final BodyImage idleImage = new BodyImage("data/Props/MenCart2.png", 12f);

    /**
     * Creates a Balila cart in the world.
     * @param world the game world
     * @param position the position of the cart
     * @param cat the player character
     */
    public BalilaCart(GameWorld world, Vec2 position, Cat cat) {
        super(world);
        Shape cartShape = new BoxShape(2.5f, 1.5f);
        new GhostlyFixture(this, cartShape);

        addImage(idleImage);
        setPosition(position);
        this.cat = cat;
        world.addStepListener(this);
    }

    /**
     * Plays a sound effect when the Cat gets close enough.
     */
    @Override
    public void preStep(StepEvent e) {
        float distance = this.getPosition().sub(cat.getPosition()).length();

        if (distance < TRIGGER_DISTANCE && !playingSound) {
            SoundEffectManager.play("data/sounds/BalilaSeller.wav", false);
            playingSound = true;
        }
    }

    @Override
    public void postStep(StepEvent e) {
        // Nothing needed here
    }
}
