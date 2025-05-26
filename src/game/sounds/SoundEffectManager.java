package game.sounds;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles playing sound effects, allowing or preventing overlap.
 */
public class SoundEffectManager {
    private static final Map<String, Clip> activeClips = new ConcurrentHashMap<>();

    /**
     * Plays a sound effect.
     * @param filepath path to the audio file
     * @param allowOverlap whether the same sound can overlap itself
     */
    public static void play(String filepath, boolean allowOverlap) {
        new Thread(() -> {
            try {
                if (!allowOverlap && activeClips.containsKey(filepath)) {
                    Clip existingClip = activeClips.get(filepath);
                    if (existingClip.isRunning()) {
                        return; // Don't replay if already playing
                    } else {
                        activeClips.remove(filepath);
                    }
                }

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filepath));
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();

                if (!allowOverlap) {
                    activeClips.put(filepath, clip);
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            activeClips.remove(filepath);
                        }
                    });
                }

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.out.println("Failed to play sound effect: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Plays a sound effect allowing overlap (shortcut version).
     * @param filepath path to the audio file
     */
    public static void play(String filepath) {
        play(filepath, true);
    }

    /**
     * Stops a specific sound effect if it's currently playing.
     * @param filepath path to the audio file
     */
    public static void stop(String filepath) {
        Clip clip = activeClips.get(filepath);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
            activeClips.remove(filepath);
        }
    }
}
