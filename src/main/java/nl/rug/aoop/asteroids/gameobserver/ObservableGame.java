package nl.rug.aoop.asteroids.gameobserver;

import java.util.HashSet;
import java.util.Set;

/**
 * An observable game is an object that game update listeners can register to, so that when the game updates, they will
 * be able to react to it.
 * <p>
 * Since Java has deprecated the official Observable and Observer objects, this class serves as a custom implementation
 * of the Observable class that is suited to the uses of this game.
 */
public abstract class ObservableGame {
    /**
     * The list of listeners that will be notified when the game updates.
     */
    private final Set<GameUpdateListener> listeners;

    /**
     * Constructs a new observable game with initially no listeners.
     */
    protected ObservableGame() {
        listeners = new HashSet<>();
    }

    /**
     * Adds the given listener to the list of listeners that will get notified when the game updates.
     *
     * @param listener The listener to add.
     */
    public void addListener(GameUpdateListener listener) {
        listeners.add(listener);
    }

    /**
     * Tells all the registered listeners that their representation of the game should be updated.
     *
     * @param timeSinceLastTick The number of milliseconds that have passed since the last game tick occurred. This is
     *                          used so that things like a display may continue showing an animated model while no
     *                          actual physics update has been done by the game engine.
     */
    public void notifyListeners(long timeSinceLastTick) {
        listeners.forEach(listener -> listener.onGameUpdated(timeSinceLastTick));
    }
}
