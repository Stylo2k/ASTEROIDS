package nl.rug.aoop.asteroids.view.viewmodels;

import nl.rug.aoop.asteroids.control.GameUpdater;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;

import java.awt.*;

/**
 * A view model for a game object is used to transform a game object into something that can be displayed in the view.
 * Child classes can be made to define custom drawing for different game objects.
 *
 * @param <T> The type of game object that a child class will display.
 */
public abstract class GameObjectViewModel<T extends GameObject> {
    /**
     * The object that this view model displays.
     */
    private final T gameObject;

    /**
     * Constructs a new view model with the given game object.
     *
     * @param gameObject The object that will be displayed when this view model is drawn.
     */
    GameObjectViewModel(T gameObject) {
        this.gameObject = gameObject;
    }

    /**
     * Gets the object that was given to this view model.
     *
     * @return The game object that should be displayed.
     */
    T getGameObject() {
        return gameObject;
    }

    /**
     * Draws the object that was given to this view model, as if it kept moving at the same velocity it had at the last
     * game tick. If every game tick an object's location changes by adding its velocity's x and y components to it,
     * then to draw the object at a time between ticks, we need to add only a fraction of the object's velocity that is
     * proportional to fraction of a game tick duration that has elapsed so far.
     * <p>
     * For example, let's assume that the game ticks every 100ms (not really, but it's easy to think about).
     * - Then, let's say that the FPS is set so that the display updates every 20ms.
     * - This means that when the game starts at 0ms, it ticks (updating physics for all the objects), and updates the
     * display. The next time the game will refresh, 20ms will have passed, and according to the game model (which has
     * not ticked yet since the game started) all objects are in their same place.
     * - However, we want to make it appear like the objects are moving. Since the game ticks every 100ms, 20ms is just
     * 20% of that. To give the appearance that an object is moving at the same speed as it would be according to game
     * physics, we can draw the object at a new position every 20ms, adding 20% of its velocity to its location, instead
     * of the 100% that would be added in a normal game tick.
     * - Therefore, every time the display refreshes, the object appears in a different position.
     * <p>
     * Please note: We DO NOT update the actual game objects' locations. This is purely a visual trick, and to update
     * the objects' locations would give an unfair advantage to faster PC's, and also cause rounding errors to
     * accumulate faster.
     *
     * @param graphics2D        The graphics object used to draw the object.
     * @param timeSinceLastTick The number of milliseconds since the last game tick.
     */
    public void drawObject(Graphics2D graphics2D, long timeSinceLastTick) {
        // What percent of a full game tick has elapsed? Only this percent of the object's velocity will be added.
        double gameTickRatio = timeSinceLastTick / GameUpdater.MILLISECONDS_PER_TICK;
        Point.Double simulatedLocation = new Point.Double(
                gameObject.getLocation().getX() + gameObject.getVelocity().getX() * gameTickRatio,
                gameObject.getLocation().getY() + gameObject.getVelocity().getY() * gameTickRatio
        );

        draw(graphics2D, simulatedLocation);
    }

    /**
     * Draws the game object that was given to this view model.
     *
     * @param graphics2D The graphics object which provides the necessary drawing methods.
     * @param location   The location at which to draw the object. Use this instead of the object's actual location, since
     *                   this location accounts for the time which has elapsed since the last game tick.
     */
    protected abstract void draw(Graphics2D graphics2D, Point.Double location);
}
