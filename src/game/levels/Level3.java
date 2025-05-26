package game.levels;

import city.cs.engine.BodyImage;
import city.cs.engine.BoxShape;
import city.cs.engine.StaticBody;
import game.main.GameWorld;
import game.characters.*;
import game.obstacles.*;
import game.props.*;
import game.winobjects.WinObject;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Level 3 of the game where the cat explores rooftops and obstacles.
 */
public class Level3 extends GameWorld {

    private List<Pigeon> pigeons;
    private List<Platform> platforms;
    private List<MovingPlatform> movingPlatforms;
    private List<BalilaCart> balilaCarts;
    private List<CardboardBox> cardboardBoxes;
    private List<BackgroundProp> backgroundProps;

    /**
     * Creates Level 3.
     * @param frame the game frame
     */
    public Level3(JFrame frame) {
        super(frame);
    }

    @Override
    protected void createLevel() {
        pigeons = new ArrayList<>();
        platforms = new ArrayList<>();
        movingPlatforms = new ArrayList<>();
        balilaCarts = new ArrayList<>();
        cardboardBoxes = new ArrayList<>();
        backgroundProps = new ArrayList<>();

        // Invisible barriers
        StaticBody barrier1 = new StaticBody(this, new BoxShape(0.5f, 10));
        StaticBody barrier2 = new StaticBody(this, new BoxShape(0.5f, 10));
        barrier1.setPosition(new Vec2(-20, MAIN_PLATFORM_Y + 10));
        barrier2.setPosition(new Vec2(355, MAIN_PLATFORM_Y + 22));
        Color invisible = new Color(0x0000000, true);
        barrier1.setFillColor(invisible);
        barrier2.setFillColor(invisible);
        barrier1.setLineColor(invisible);
        barrier2.setLineColor(invisible);

        // Cat setup
        cat = new Cat(this);
        cat.setPosition(START_POSITION);

        // Rooftop structures
        new Ladder(this, new Vec2(-14, MAIN_PLATFORM_Y - 10), 15);
        addPlatform(70, 10, MAIN_PLATFORM_Y, 0);

        new Ladder(this, new Vec2(10, MAIN_PLATFORM_Y + 2), 10);
        addPlatform(28, 0, MAIN_PLATFORM_Y + 10, 36);

        addPigeon(new Vec2(13, MAIN_PLATFORM_Y + 4));
        addPigeon(new Vec2(15, MAIN_PLATFORM_Y + 4));

        new LaundryString(this, new Vec2(38, MAIN_PLATFORM_Y + 16), new Vec2(40, MAIN_PLATFORM_Y + 12));

        new Ladder(this, new Vec2(70, MAIN_PLATFORM_Y + 12), 22);
        addPlatform(20, 0, MAIN_PLATFORM_Y + 32, 90);
        addPlatform(20, 20, MAIN_PLATFORM_Y + 32, 110);
        addPlatform(20, 0, MAIN_PLATFORM_Y + 32, 130);

        addRoof(new Vec2(56, MAIN_PLATFORM_Y + 28), 40);
        addBackgroundProp("data/Props/PaintProp.png", new Vec2(62, MAIN_PLATFORM_Y + 35), 5);
        addBackgroundProp("data/Props/PaintProp.png", new Vec2(59, MAIN_PLATFORM_Y + 35), 5);

        addBackgroundProp("data/Props/HouseSide.png", new Vec2(75, MAIN_PLATFORM_Y + 48), 35);
        addBackgroundProp("data/Props/HouseSide.png", new Vec2(109, MAIN_PLATFORM_Y + 48), 35);
        addBackgroundProp("data/Props/HouseSide.png", new Vec2(143, MAIN_PLATFORM_Y + 48), 35);

        new RollingPaintBucket(this, new Vec2(60, MAIN_PLATFORM_Y + 34), cat);

        addRoof(new Vec2(162, MAIN_PLATFORM_Y + 28), 124);
        addRoof(new Vec2(168, MAIN_PLATFORM_Y + 19), 124);

        addPlatform(10, 10, MAIN_PLATFORM_Y + 13, 180);
        addPlatform(10, 0, MAIN_PLATFORM_Y + 13, 200);

        new Ladder(this, new Vec2(210, MAIN_PLATFORM_Y - 5), 19);
        addPlatform(50, 10, MAIN_PLATFORM_Y - 5, 234);

        addBackgroundProp("data/Props/HouseSide.png", new Vec2(230, MAIN_PLATFORM_Y + 13), 35);
        addBackgroundProp("data/Props/HouseSide.png", new Vec2(285, MAIN_PLATFORM_Y + 13), 35);

        new LaundryString(this, new Vec2(253, MAIN_PLATFORM_Y + 1), new Vec2(255, MAIN_PLATFORM_Y - 3));
        new LaundryString(this, new Vec2(262, MAIN_PLATFORM_Y + 1), new Vec2(264, MAIN_PLATFORM_Y - 3));

        addPlatform(4, 0, MAIN_PLATFORM_Y - 5, 300);

        addVerticalMovingPlatform(14, 1, new Vec2(316, MAIN_PLATFORM_Y + 5), 10, 1.5f, new Color(126, 91, 50));

        addPlatform(10, 20, MAIN_PLATFORM_Y + 15, 340);
        addPlatform(10, 30, MAIN_PLATFORM_Y + 50, 365);
        // Win Object
        new WinObject(this, new Vec2(342, MAIN_PLATFORM_Y + 30), frame, "data/WinObjects/Level3WinObject.png", 30);
    }

    // Helper methods
    private void addPigeon(Vec2 position) {
        Pigeon pigeon = new Pigeon(this, position, cat);
        pigeons.add(pigeon);
    }

    private void addPlatform(int width, int height, float topY, float centerX) {
        Platform platform = new Platform(this, width, height, topY, centerX, "data/Platforms/Level3Top.png", "data/Platforms/Level3Base.png");
        platforms.add(platform);
    }

    private void addHorizontalMovingPlatform(float width, float height, Vec2 startPos, float range, float speed, Color color) {
        MovingPlatform platform = new MovingPlatform(this, width, height, startPos);
        platform.setLeftLimit(startPos.x - range);
        platform.setRightLimit(startPos.x + range);
        platform.startMovingHorizontally(speed);
        platform.setFillColor(color);
        platform.setLineColor(color);
        movingPlatforms.add(platform);
    }

    private void addVerticalMovingPlatform(float width, float height, Vec2 startPos, float range, float speed, Color color) {
        MovingPlatform platform = new MovingPlatform(this, width, height, startPos);
        platform.setLowerLimit(startPos.y - range);
        platform.setUpperLimit(startPos.y + range);
        platform.startMovingVertically(speed);
        platform.setFillColor(color);
        platform.setLineColor(color);
        movingPlatforms.add(platform);
    }

    private void addRoof(Vec2 position, float angle) {
        StaticBody roof = new StaticBody(this, new BoxShape(6, 1.5f));
        roof.setPosition(position);
        roof.setAngleDegrees(angle);
        roof.addImage(new BodyImage("data/Platforms/Roof.png", 2.25f));
    }

    private void addBackgroundProp(String imagePath, Vec2 position, float scale) {
        BackgroundProp prop = new BackgroundProp(this, imagePath, scale, position);
        backgroundProps.add(prop);
    }

    private void addCardboardBox(Vec2 position, String type) {
        CardboardBox box = new CardboardBox(this, position, cat, type);
        cardboardBoxes.add(box);
    }

    private void addBalilaCart(Vec2 position) {
        BalilaCart cart = new BalilaCart(this, position, cat);
        balilaCarts.add(cart);
    }

    @Override
    public int getLevelNumber() {
        return 3;
    }

    @Override
    public String getBackgroundImagePath() {
        return "data/Backgrounds/RoofTopBackground.png";
    }

    public List<Pigeon> getPigeons() {
        return pigeons;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public List<MovingPlatform> getMovingPlatforms() {
        return movingPlatforms;
    }

    public List<BalilaCart> getBalilaCarts() {
        return balilaCarts;
    }

    public List<CardboardBox> getCardboardBoxes() {
        return cardboardBoxes;
    }

    public List<BackgroundProp> getBackgroundProps() {
        return backgroundProps;
    }
}

