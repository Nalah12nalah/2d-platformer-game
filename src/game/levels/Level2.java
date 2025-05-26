package game.levels;


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
 * Level 2 of the game where the cat explores the busy souq area.
 */
public class Level2 extends GameWorld {

    private List<Pigeon> pigeons;
    private List<Platform> platforms;
    private List<MovingPlatform> movingPlatforms;
    private List<BalilaCart> balilaCarts;
    private List<CardboardBox> cardboardBoxes;
    private List<BackgroundProp> backgroundProps;

    /**
     * Creates Level 2.
     * @param frame the game frame
     */
    public Level2(JFrame frame) {
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
        barrier2.setPosition(new Vec2(315, MAIN_PLATFORM_Y + 70));
        Color invisible = new Color(0x0000000, true);
        barrier1.setFillColor(invisible);
        barrier2.setFillColor(invisible);
        barrier1.setLineColor(invisible);
        barrier2.setLineColor(invisible);

        // Cat setup
        cat = new Cat(this);
        cat.setPosition(START_POSITION);

        // Souq platforms and obstacles
        addPlatform(30, 10, MAIN_PLATFORM_Y, 15);
        addCardboardBox(new Vec2(-15, MAIN_PLATFORM_Y + 3), "left");
        addBackgroundProp("data/Props/MenCart1.png", new Vec2(-5, MAIN_PLATFORM_Y + 6), 10);
        addBalilaCart(new Vec2(8, MAIN_PLATFORM_Y + 6));
        addBackgroundProp("data/Props/WomenCart1.png", new Vec2(18, MAIN_PLATFORM_Y + 6), 10);

        addCardboardBox(new Vec2(38, MAIN_PLATFORM_Y + 3), "closed");
        addCardboardBox(new Vec2(40, MAIN_PLATFORM_Y + 3), "closed");
        addCardboardBox(new Vec2(39.5f, MAIN_PLATFORM_Y + 6.5f), "open");

        addPigeon(new Vec2(46, MAIN_PLATFORM_Y + 3));
        addPigeon(new Vec2(44, MAIN_PLATFORM_Y + 3));

        addHorizontalMovingPlatform(20, 15, new Vec2(75, MAIN_PLATFORM_Y), 5, 1f, new Color(64, 35, 14));

        addPigeon(new Vec2(72, MAIN_PLATFORM_Y + 3));
        addPigeon(new Vec2(78, MAIN_PLATFORM_Y + 3));

        addPlatform(30, 10, MAIN_PLATFORM_Y, 135);
        addCardboardBox(new Vec2(142, MAIN_PLATFORM_Y + 3), "closed");
        addCardboardBox(new Vec2(142, MAIN_PLATFORM_Y + 6.5f), "closed");
        addCardboardBox(new Vec2(136, MAIN_PLATFORM_Y + 3), "open");

        addPigeon(new Vec2(130, MAIN_PLATFORM_Y + 3));
        addPigeon(new Vec2(143, MAIN_PLATFORM_Y + 3));
        addPigeon(new Vec2(128, MAIN_PLATFORM_Y + 20));
        addPigeon(new Vec2(145, MAIN_PLATFORM_Y + 20));

        addPlatform(3, 0, MAIN_PLATFORM_Y + 4, 110);
        addPlatform(3, 0, MAIN_PLATFORM_Y + 12, 118);
        addPlatform(3, 0, MAIN_PLATFORM_Y + 18, 126);
        addPlatform(6, 0, MAIN_PLATFORM_Y + 26, 136);

        addBalilaCart(new Vec2(136, MAIN_PLATFORM_Y + 33));

        addPlatform(3, 0, MAIN_PLATFORM_Y + 18, 146);
        addPlatform(3, 0, MAIN_PLATFORM_Y + 12, 154);
        addPlatform(3, 0, MAIN_PLATFORM_Y + 4, 162);

        addVerticalMovingPlatform(10, 2, new Vec2(184, MAIN_PLATFORM_Y + 5), 7, 2f, new Color(64, 35, 14));
        addHorizontalMovingPlatform(10, 2, new Vec2(204, MAIN_PLATFORM_Y + 15), 7, 1f, new Color(64, 35, 14));

        addPlatform(8, 18, MAIN_PLATFORM_Y + 15, 225);
        addPlatform(3, 0, MAIN_PLATFORM_Y + 23, 235);
        addPlatform(3, 0, MAIN_PLATFORM_Y + 31, 220);
        addPlatform(3, 0, MAIN_PLATFORM_Y + 39, 235);
        addPlatform(3, 0, MAIN_PLATFORM_Y + 47, 220);

        addPlatform(10, 1, MAIN_PLATFORM_Y + 55, 245);

        // Win Object
        new WinObject(this, new Vec2(245, MAIN_PLATFORM_Y + 65), frame, "data/WinObjects/Level2WinObject.png", 20f);
    }

    private void addPigeon(Vec2 position) {
        Pigeon pigeon = new Pigeon(this, position, cat);
        pigeons.add(pigeon);
    }

    private void addPlatform(int width, int height, float bottomY, float centerX) {
        Platform platform = new Platform(this, width, height, bottomY, centerX, "data/Platforms/Level2Top.png", "data/Platforms/Level2Base.png");
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

    private void addCardboardBox(Vec2 position, String type) {
        CardboardBox box = new CardboardBox(this, position, cat, type);
        cardboardBoxes.add(box);
    }

    private void addBalilaCart(Vec2 position) {
        BalilaCart cart = new BalilaCart(this, position, cat);
        balilaCarts.add(cart);
    }

    private void addBackgroundProp(String imagePath, Vec2 position, float scale) {
        BackgroundProp prop = new BackgroundProp(this, imagePath, scale, position);
        backgroundProps.add(prop);
    }

    @Override
    public int getLevelNumber() {
        return 2;
    }

    @Override
    public String getBackgroundImagePath() {
        return "data/Backgrounds/SouqBackground.jpg";
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
