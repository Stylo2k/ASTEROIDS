package nl.rug.aoop.asteroids.model.gameobjects;

import lombok.Setter;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import lombok.Getter;

import java.awt.*;

/**
 * This class represents any object that is present in a game, such as a bullet, asteroid, or a player's ship. As an
 * abstract class, it provides some basic attributes that all objects in the game should have, like position and
 * velocity.
 */
public abstract class GameObject {
    /**
     * An x and y value pair indicating the object's current location.
     */
    @Getter @Setter
    private Point.Double location;

    /**
     * An x and y value pair indicating the object's current velocity, in pixels per game tick.
     */
    @Getter @Setter
    private Point.Double velocity;

    /**
     * Radius of the object.
     */
    @Getter
    private final double radius;

    /**
     * A flag that is set when this object collides with another. This tells the game engine that this object should be
     * removed from the game.
     */
    @Getter
    protected boolean destroyed;

    /**
     * The number of game ticks that must pass before this object is allowed to collide with other game objects. This
     * can also be thought of as a grace period, or temporary immunity.
     */
    @Getter @Setter
    private int stepsUntilCollisionPossible;

    /**
     * Constructs a new game object with the specified location, velocity and radius.
     *
     * @param locationX The object's location on the x-axis.
     * @param locationY The object's location on the y-axis.
     * @param velocityX Velocity in X direction.
     * @param velocityY Velocity in Y direction.
     * @param radius    Radius of the object.
     */
    protected GameObject(double locationX, double locationY, double velocityX, double velocityY, double radius) {
        location = new Point.Double(locationX, locationY);
        velocity = new Point.Double(velocityX, velocityY);
        this.radius = radius;
        stepsUntilCollisionPossible = getDefaultStepsUntilCollisionPossible();
    }

    /**
     * A convenience constructor that accepts points instead of individual coordinates.
     *
     * @param location A point representing the x- and y-coordinates of the object's location.
     * @param velocity A point representing the object's speed on both the x and y axes.
     * @param radius   The radius of the object.
     */
    protected GameObject(Point.Double location, Point.Double velocity, double radius) {
        this(location.getX(), location.getY(), velocity.getX(), velocity.getY(), radius);
    }

    /**
     * Child classes should implement this method to define what happens to an object when the game advances by one game
     * tick in the main loop. The amount of time that passes with each step should be the same, so that movement is
     * uniform even when performance may suffer.
     */
    public void nextStep() {
        location.x = (AsteroidsFrame.WINDOW_SIZE.width + location.x + velocity.x) % AsteroidsFrame.WINDOW_SIZE.width;
        location.y = (AsteroidsFrame.WINDOW_SIZE.height + location.y + velocity.y) % AsteroidsFrame.WINDOW_SIZE.height;
        if (stepsUntilCollisionPossible > 0) {
            stepsUntilCollisionPossible--;
        }
    }

    /**
     * Flags this object as destroyed, so that the game may deal with it.
     */
    public final void destroy() {
        destroyed = true;
    }

    /**
     * @return The speed of the object, as a scalar value combining the x- and y-velocities.
     */
    public double getSpeed() {
        // A cheap trick: distance() is doing Math.sqrt(px * px + py * py) internally.
        return getVelocity().distance(0, 0);
    }

    /**
     * Given some other game object, this method checks whether the current object and the given object collide with
     * each other. It does this by measuring the distance between the objects and checking whether it is larger than the
     * sum of the radii. Furthermore, both objects should be allowed to collide.
     *
     * @param other The other object that it may collide with.
     * @return True if object collides with given object, false otherwise.
     */
    public boolean collides(GameObject other) {
        return getLocation().distance(other.getLocation()) < getRadius() + other.getRadius()
                && canCollide() && other.canCollide();
    }

    /**
     * @return Whether or not this object is immune from collisions.
     */
    // todo : remove this or find alternative
    private boolean canCollide() {
        return stepsUntilCollisionPossible <= 0;
    }

    /**
     * @return The number of steps, or game ticks, for which this object is immune from collisions.
     */
    public abstract int getDefaultStepsUntilCollisionPossible();
}
