package nl.rug.aoop.asteroids.control;

import nl.rug.aoop.asteroids.model.gameobjects.Spaceship;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This class is responsible for handling keyboard input for a single player that is bound to a ship.
 */
public record PlayerKeyListener(Spaceship ship) implements KeyListener {
    /**
     * The key that, when pressed, causes the ship to accelerate.
     */
    private static final int ACCELERATION_KEY = KeyEvent.VK_W;

    /**
     * The key that turns the ship left, or counter-clockwise.
     */
    private static final int LEFT_KEY = KeyEvent.VK_A;

    /**
     * The key that turns the ship right, or clockwise.
     */
    private static final int RIGHT_KEY = KeyEvent.VK_D;

    /**
     * The key that causes the ship to fire its weapon.
     */
    private static final int FIRE_WEAPON_KEY = KeyEvent.VK_SPACE;

    /**
     * Constructs a new player key listener to control the given ship.
     *
     * @param ship The ship that this key listener will control.
     */
    public PlayerKeyListener {
    }

    /**
     * This method is invoked when a key is pressed and sets the corresponding fields in the spaceship to true.
     *
     * @param event Key event that triggered the method.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case ACCELERATION_KEY -> ship.setAccelerateKeyPressed(true);
            case LEFT_KEY -> ship.setTurnLeftKeyPressed(true);
            case RIGHT_KEY -> ship.setTurnRightKeyPressed(true);
            case FIRE_WEAPON_KEY -> ship.setFiring(true);
        }
    }

    /**
     * This method is invoked when a key is released and sets the corresponding fields in the spaceship to false.
     *
     * @param event Key event that triggered the method.
     */
    @Override
    public void keyReleased(KeyEvent event) {
        switch (event.getKeyCode()) {
            case ACCELERATION_KEY -> ship.setAccelerateKeyPressed(false);
            case LEFT_KEY -> ship.setTurnLeftKeyPressed(false);
            case RIGHT_KEY -> ship.setTurnRightKeyPressed(false);
            case FIRE_WEAPON_KEY -> ship.setFiring(false);
        }
    }

    /**
     * This method doesn't do anything, but we must provide an empty implementation to satisfy the contract of the
     * KeyListener interface.
     *
     * @param event Key event that triggered the method.
     */
    @Override
    public void keyTyped(KeyEvent event) {
    }
}
