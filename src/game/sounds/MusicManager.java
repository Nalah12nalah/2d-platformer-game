package game.sounds;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Manages background music playback, including fading in and out.
 */
public class MusicManager {
    private static Clip currentClip;
    private static FloatControl volumeControl;
    private static Thread fadeThread;

    /**
     * Plays a music file immediately.
     * @param filepath path to the audio file
     * @param loop whether to loop the music
     */
    public static void play(String filepath, boolean loop) {
        stop();
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filepath));
            currentClip = AudioSystem.getClip();
            currentClip.open(audioIn);
            volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);

            if (loop) {
                currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                currentClip.start();
            }
        } catch (Exception e) {
            System.out.println("Error loading music: " + e.getMessage());
        }
    }

    /**
     * Fades out the currently playing music and stops it.
     */
    public static void fadeOutAndStop() {
        stopFade();
        if (currentClip != null && currentClip.isRunning()) {
            fadeThread = new Thread(() -> {
                try {
                    for (float i = volumeControl.getValue(); i >= -40; i -= 2) {
                        volumeControl.setValue(i);
                        Thread.sleep(50);
                    }
                    stop();
                } catch (Exception ignored) {}
            });
            fadeThread.start();
        }
    }

    /**
     * Fades in a new music track.
     * @param filepath path to the audio file
     * @param loop whether to loop the music
     */
    public static void fadeIn(String filepath, boolean loop) {
        stop();
        stopFade();
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filepath));
            currentClip = AudioSystem.getClip();
            currentClip.open(audioIn);
            volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-40f);

            if (loop) {
                currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                currentClip.start();
            }

            fadeThread = new Thread(() -> {
                try {
                    for (float i = -40f; i <= 0f; i += 2f) {
                        volumeControl.setValue(i);
                        Thread.sleep(50);
                    }
                } catch (Exception ignored) {}
            });
            fadeThread.start();
        } catch (Exception e) {
            System.out.println("Error fading in: " + e.getMessage());
        }
    }

    /**
     * Immediately stops any music playing.
     */
    public static void stop() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
            volumeControl = null;
        }
    }

    /**
     * Interrupts any ongoing fade thread.
     */
    private static void stopFade() {
        if (fadeThread != null && fadeThread.isAlive()) {
            fadeThread.interrupt();
        }
    }
}
