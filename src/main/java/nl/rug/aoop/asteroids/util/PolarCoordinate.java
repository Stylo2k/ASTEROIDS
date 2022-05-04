package nl.rug.aoop.asteroids.util;

import lombok.Getter;

import static java.lang.Math.PI;

/**
 * Represents a polar coordinate: an angle, in radians, along with a radius. The values are normalized so that any polar
 * coordinate has an angle between 0 (inclusive) and 2 * PI (exclusive), as well as a positive radius.
 */
public class PolarCoordinate {
    /**
     * The angle of this polar coordinate, in radians.
     */
    @Getter
    private double angle;

    /**
     * The radius of this polar coordinate.
     */
    @Getter
    private final double radius;

    /**
     * Constructs a new polar coordinate with the given values.
     * Normalizes angles to 0 <= angle <= 2 * PI.
     * Normalizes radii to 0 <= radius.
     *
     * @param angle  The angle of this coordinate.
     * @param radius The radius of this coordinate.
     */
    public PolarCoordinate(double angle, double radius) {
        this.angle = normalizeAngle(angle);
        // To normalize the radius, that is done here, since it might modify the angle, if a negative radius was given.
        if (radius < 0) { // If the radius is negative, make it positive and flip the angle (and normalize again).
            radius *= -1;
            this.angle = normalizeAngle(angle + PI);
        }
        this.radius = radius;
    }

    /**
     * Normalizes an angle so that it lies between 0 and 2 * PI.
     * I know it's probably not the fastest way to compute this, but it's easy to read.
     *
     * @param rawAngle The angle to normalize, in radians.
     * @return A normalized angle in radians. 0 <= angle <= 2 * PI.
     */
    private double normalizeAngle(double rawAngle) {
        double signedBoundedAngle = rawAngle % (2 * PI);
        if (signedBoundedAngle < 0) {
            return signedBoundedAngle + (2 * PI);
        }
        return signedBoundedAngle;
    }

}
