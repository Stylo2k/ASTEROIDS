package nl.rug.aoop.asteroids.model;

import lombok.Getter;

/**
 * This enumeration defines the different possible sizes of asteroids.
 */
public enum AsteroidSize {
    SMALL(10),
    MEDIUM(20),
    LARGE(40);

    /**
     * The radius that each different asteroid size is.
     */
    @Getter
    private final double radius;

    /**
     * Constructor that requires that any new enumeration values provide a valid radius.
     *
     * @param radius The radius of the asteroid.
     */
    AsteroidSize(double radius) {
        this.radius = radius;
    }

    /**
     * @return The size of asteroids that are produced when this one is destroyed. May return null if this asteroid is
     * too small to produce successors.
     */
    public AsteroidSize getSuccessorSize() {
        return switch(this) {
            case LARGE -> MEDIUM;
            case MEDIUM -> SMALL;
            default -> null;
        };
    }
}
