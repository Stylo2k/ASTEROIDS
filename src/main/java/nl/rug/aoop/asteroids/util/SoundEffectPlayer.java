package nl.rug.aoop.asteroids.util;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.model.AsteroidSize;
import nl.rug.aoop.asteroids.view.mainmenu.MainMenuPanel;

import javax.sound.sampled.*;
import java.io.IOException;
import java.nio.file.Path;

/**
 * plays the sounds needed for a good gaming experience
 * **/
@Log
public class SoundEffectPlayer implements Runnable {
    /**
     * all types of sound effect
     * */
    public static final String THRUST = "thrust";
    public static final String FIRE = "fire";
    public static final String BANG = "bang";
    public static final String EXPLODE = "explode";

    /**
     * the type of sound effect to play
     * */
    private final String type;

    /**
     * plays a sound depending on the type
     * @param type the type of sound
     * */
    public SoundEffectPlayer(String type) {
        this.type = type;
    }

    /**
     * static method that plays the main menu sound as long
     * as a player is in the main menu
     *  ~ notice this is only made once
     * */
    public void playMainMenuMusic(MainMenuPanel mainMenuPanel) {
        MainMenuSoundPlayer runner = new MainMenuSoundPlayer(mainMenuPanel);
        Thread thread = new Thread(runner);
        thread.start();
    }

    /**
     * plays a sound depending on the asteroids size
     * @param type the type of the bang
     * @param size the size of the asteroid
     * */
    public SoundEffectPlayer(String type, AsteroidSize size) {
        String plus;
        if (size == AsteroidSize.LARGE) {
            plus = "Large";
        } else if (size == AsteroidSize.MEDIUM) {
            plus = "Medium";
        } else {
            plus = "Small";
        }
        this.type = type + plus;
    }

    /**
     * plays a certain sound given by its {@link #type}
     * <p>
     *     if the type is an asteroid then we also have a certain
     *     size to it and depending on the size we get different "bangs"
     * </p>
     *     <b>SEE data/sounds</b>
     * */
    @Override
    public void run() {
        Path path = Path.of("data/sounds/"+ type + ".wav");
        AudioInputStream ais;
        try {
            ais = AudioSystem.getAudioInputStream(path.toFile());
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            log.warning("Encountered an error while trying to play sound effect." +
                    "Please check your game files (data/sounds)");

        }
    }
}
