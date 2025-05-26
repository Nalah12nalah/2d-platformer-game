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
 * Level 1 of the game where the cat starts its Hijazi adventure.
 */
public class Level1 extends GameWorld {

    private List<Pigeon> pigeons;
    private List<Platform> platforms;
    private List<MovingPlatform> movingPlatforms;
    private List<BackgroundProp> backgroundProps;

    /**
     * Creates Level 1.
     * @param frame the game frame
     */
    public Level1(JFrame frame) {
        super(frame);
    }

    @Override
    protected void createLevel() {
        pigeons = new ArrayList<>();
        platforms = new ArrayList<>();
        movingPlatforms = new ArrayList<>();
        backgroundProps = new ArrayList<>();

        // Invisible barriers
        StaticBody barrier1 = new StaticBody(this, new BoxShape(0.5f, 10));
        StaticBody barrier2 = new StaticBody(this, new BoxShape(0.5f, 10));
        barrier1.setPosition(new Vec2(-22, MAIN_PLATFORM_Y + 10));
        barrier2.setPosition(new Vec2(315, MAIN_PLATFORM_Y + 52));
        Color invisible = new Color(0x0000000, true);
        barrier1.setFillColor(invisible);
        barrier2.setFillColor(invisible);
        barrier1.setLineColor(invisible);
        barrier2.setLineColor(invisible);

        // Cat setup
        cat = new Cat(this);
        cat.setPosition(new Vec2(START_POSITION.x - 10, START_POSITION.y + 15));

        // Starting platform and background
        addPlatform(40, 10, MAIN_PLATFORM_Y, -15);
        addBackgroundProp("data/Props/MorningHouse1.png", new Vec2(-40, MAIN_PLATFORM_Y + 20), 40);
        addBackgroundProp("data/Props/Plant.png", new Vec2(-21, MAIN_PLATFORM_Y + 5), 10);

        // First pigeon
        addPigeon(new Vec2(25, MAIN_PLATFORM_Y));

        // Moving platform section
        addHorizontalMovingPlatform(20, 0.7f, new Vec2(65, MAIN_PLATFORM_Y), 60, 70, 1.5f, new Color(115, 101, 101, 252));
        addPigeon(new Vec2(65, MAIN_PLATFORM_Y));

        // Mid-section platforms
        addPlatform(10, 10, MAIN_PLATFORM_Y, 95);
        addPlatform(3, 2, MAIN_PLATFORM_Y + 6, 110);
        addPlatform(3, 2, MAIN_PLATFORM_Y + 12, 120);
        addPigeon(new Vec2(120, MAIN_PLATFORM_Y + 14));
        addPlatform(3, 2, MAIN_PLATFORM_Y + 18, 130);

        addHorizontalMovingPlatform(8, 0.5f, new Vec2(145, MAIN_PLATFORM_Y + 18), 140, 150, 1.5f, new Color(115, 101, 101, 252));

        addBackgroundProp("data/Props/MorningHouse1.png", new Vec2(210, MAIN_PLATFORM_Y + 38), 40);
        addPlatform(27, 10, MAIN_PLATFORM_Y + 18, 199);
        addPigeon(new Vec2(175, MAIN_PLATFORM_Y + 20));
        addPlatform(3, 2, MAIN_PLATFORM_Y + 24, 185);

        // Climb section
        addVerticalMovingPlatform(13, 0.5f, new Vec2(245, MAIN_PLATFORM_Y + 24), MAIN_PLATFORM_Y + 20, MAIN_PLATFORM_Y + 28, 1.5f, new Color(115, 101, 101, 252));
        addPlatform(8, 10, MAIN_PLATFORM_Y + 30, 265);
        addVerticalMovingPlatform(13, 0.5f, new Vec2(285, MAIN_PLATFORM_Y + 36), MAIN_PLATFORM_Y + 33, MAIN_PLATFORM_Y + 39, 1.5f, new Color(115, 101, 101, 252));
        addPlatform(8, 10, MAIN_PLATFORM_Y + 42, 305);

        // Win Object
        new WinObject(this, new Vec2(305, MAIN_PLATFORM_Y + 45), frame, "data/WinObjects/Level1WinObject.png", 4f);
    }

    private void addPigeon(Vec2 position) {
        Pigeon pigeon = new Pigeon(this, position, cat);
        pigeons.add(pigeon);
    }

    private void addPlatform(int width, int height, float bottomY, float centerX) {
        Platform platform = new Platform(this, width, height, bottomY, centerX, "data/Platforms/Level1Top.png", "data/Platforms/Level1Base.png");
        platforms.add(platform);
    }

    private void addHorizontalMovingPlatform(float width, float height, Vec2 startPos, float leftLimit, float rightLimit, float speed, Color color) {
        MovingPlatform platform = new MovingPlatform(this, width, height, startPos);
        platform.startMovingHorizontally(speed);
        platform.setFillColor(color);
        platform.setLineColor(color);
        platform.setLeftLimit(leftLimit);
        platform.setRightLimit(rightLimit);
        movingPlatforms.add(platform);
    }

    private void addVerticalMovingPlatform(float width, float height, Vec2 startPos, float lowerLimit, float upperLimit, float speed, Color color) {
        MovingPlatform platform = new MovingPlatform(this, width, height, startPos);
        platform.startMovingVertically(speed);
        platform.setFillColor(color);
        platform.setLineColor(color);
        platform.setLowerLimit(lowerLimit);
        platform.setUpperLimit(upperLimit);
        movingPlatforms.add(platform);
    }

    private void addBackgroundProp(String imagePath, Vec2 position, float scale) {
        BackgroundProp prop = new BackgroundProp(this, imagePath, scale, position);
        backgroundProps.add(prop);
    }

    @Override
    public int getLevelNumber() {
        return 1;
    }

    @Override
    public String getBackgroundImagePath() {
        return "data/Backgrounds/Background.jpeg";
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

    public List<BackgroundProp> getBackgroundProps() {
        return backgroundProps;
    }
}
