package nl.rug.aoop.asteroids.util;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.view.mainmenu.MainMenuPanel;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.nio.file.Path;

/**
 * plays main-menu music when player is in one of the main menu panels
 * */
@Log
public class MainMenuSoundPlayer extends Thread{
    /**
     * instance of the main menu panel
     * */
    private final MainMenuPanel mainMenuPanel;

    /**
     * path to the music
     * */
    private static final Path MUSIC_PATH = Path.of("data/sounds/mainMenu.wav");

    /**
     * indicates if the class is running
     * */
    private boolean running;

    /**
     * is responsible for playing the background track when the user is in the {@link MainMenuPanel}
     *
     * @param mainMenuPanel the main panel to play sound in background for
     * */
    public MainMenuSoundPlayer(MainMenuPanel mainMenuPanel) {
        this.mainMenuPanel = mainMenuPanel;
        running= false;
    }

    /**
     * plays the main menu music and loops endlessly.
     * <p>
     *     it stops however when a player exits one of the main menu panels
     * </p>
     * */
    @Override
    public void run() {
        running = true;
        AudioInputStream ais;
        try {
            ais = AudioSystem.getAudioInputStream(MUSIC_PATH.toFile());
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            while (running) {
                if (!mainMenuPanel.isInMainMenu() && clip.isOpen()) {
                    clip.close();
                } else if (mainMenuPanel.isInMainMenu() && !clip.isOpen()) {
                    ais = AudioSystem.getAudioInputStream(MUSIC_PATH.toFile());
                    clip.open(ais);
                    clip.start();
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
        } catch (Exception e) {
            log.warning("Encountered an error while trying to play background music");
        }
    }
}
