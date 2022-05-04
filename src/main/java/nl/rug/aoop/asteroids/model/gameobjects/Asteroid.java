package nl.rug.aoop.asteroids.model.gameobjects;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.model.AsteroidSize;
import nl.rug.aoop.asteroids.util.AsteroidRotator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An asteroid is the object which can be destroyed by bullets, but also will destroy a player's ship if the two
 * collide. Each asteroid has a certain size, which determines its radius, as well as if any smaller asteroids spawn
 * when that one is destroyed.
 */
public class Asteroid extends GameObject {
    /**
     * By how much (both positive and negative) can the successor asteroids that spawn when an asteroid is destroyed
     * vary in velocity compared to their parent.
     */
    public static final double SUCCESSOR_VELOCITY_DIFFERENCE = 5.0;

    /**
     * Number of ticks this object is immune to collision
     */
    public static final int IMMUNITY_TICKS = 3;

    /**
     * The size of this asteroid.
     */
    @Getter
    private final AsteroidSize size;

    /**
     * the direction angle of the asteroid
     * */
    @Getter @Setter
    public double directionAngle;

    /**
     * Constructs a new asteroid at the specified location, with specified velocities in both X and Y direction and the
     * specified radius.
     *
     * @param location the location in which to spawn an asteroid.
     * @param velocity The velocity of the asteroid.
     * @param size     The size of the asteroid.
     */
    public Asteroid(Point.Double location, Point.Double velocity, AsteroidSize size) {
        super(location, velocity, (int) size.getRadius());
        this.size = size;
        directionAngle = AsteroidRotator.generateRandomAngle();
    }

    /**
     * Constructs a new asteroid at the specified location, with specified velocities in both X and Y direction and the
     * specified radius.
     *
     * @param location the location in which to spawn an asteroid.
     * @param velocity The velocity of the asteroid.
     * @param size     The size of the asteroid.
     */
    public Asteroid(Point.Double location, Point.Double velocity, double size, double directionAngle) {
        super(location, velocity, (int) size);
        if (size == AsteroidSize.SMALL.getRadius()) {
            this.size = AsteroidSize.SMALL;
        } else if (size == AsteroidSize.MEDIUM.getRadius()) {
            this.size = AsteroidSize.MEDIUM;
        } else {
            this.size = AsteroidSize.LARGE;
        }
        this.directionAngle = directionAngle;
    }

    /**
     * Generates some asteroids that spawn as a result of the destruction of this asteroid. Some sizes of asteroids may
     * not produce any successors because they're too small.
     *
     * @return A collection of the successors.
     */
    public Collection<Asteroid> getSuccessors() {
        // Initialize the array to a fixed capacity to improve performance.
        Collection<Asteroid> successors = new ArrayList<>(2);
        AsteroidSize successorSize = size.getSuccessorSize();
        if (successorSize != null) {
            successors.add(generateSuccessor());
            successors.add(generateSuccessor());
        }
        return successors;
    }

    /**
     * Generates a new asteroid that should be spawned when this one is destroyed.
     * <p>
     * The asteroid is created at the same location as the current one, and is one size smaller. The new asteroid's
     * velocity is set to the current asteroid's velocity, with some random speed adjustments.
     *
     * @return A newly created asteroid, if the size of this asteroid allows for successors. Otherwise null.
     */
    private Asteroid generateSuccessor() {
        if (size.getSuccessorSize() == null) {
            return null;
        }
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return new Asteroid(
                getLocation(),
                new Point.Double(
                        getVelocity().getX() + rng.nextDouble(-SUCCESSOR_VELOCITY_DIFFERENCE, SUCCESSOR_VELOCITY_DIFFERENCE),
                        getVelocity().getY() + rng.nextDouble(-SUCCESSOR_VELOCITY_DIFFERENCE, SUCCESSOR_VELOCITY_DIFFERENCE)
                ),
                size.getSuccessorSize()
        );
    }

    /**
     * @return The number of steps, or game ticks, for which this object is immune from collisions.
     */
    @Override
    public int getDefaultStepsUntilCollisionPossible() {
        return IMMUNITY_TICKS;
    }
}
