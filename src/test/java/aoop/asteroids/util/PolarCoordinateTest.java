package aoop.asteroids.util;

import nl.rug.aoop.asteroids.util.PolarCoordinate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.lang.Math.PI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Tests the functionality of the PolarCoordinate class.
 *
 * The only publicly available method besides the getters is the constructor, so this is all that will be tested here.
 */
class PolarCoordinateTest {
	/**
	 * The acceptable accuracy for asserting the equality of floating point numbers here. This is needed due to the tiny
	 * floating point errors that can occur when doing arithmetic on doubles.
	 */
	private static final double DELTA = 0.00001;

	/**
	 * Tests the constructor for a polar coordinate. This is what does all the work for the class, and all other methods
	 * are simply getters. Here is where the normalization should be tested.
	 *
	 * @param angle The angle to create the polar coordinate with. This may not be normalized.
	 * @param radius The radius to create the polar coordinate with. This may not be normalized.
	 * @param expectedAngle The normalized angle that the newly created polar coordinate should have.
	 * @param expectedRadius The normalized radius that the newly created polar coordinate should have.
	 */
	@ParameterizedTest
	@MethodSource("generateAngleRadiusPairs")
	void testConstructor(double angle, double radius, double expectedAngle, double expectedRadius) {
		PolarCoordinate pc = new PolarCoordinate(angle, radius);
		assertEquals(expectedAngle, pc.getAngle(), DELTA);
		assertEquals(expectedRadius, pc.getRadius(), DELTA);
	}

	/**
	 * @return A stream of arguments that can be used by the constructor test.
	 */
	static Stream<Arguments> generateAngleRadiusPairs() {
		return Stream.of(
				// First test zero angle and radius.
				arguments(0.0, 0.0, 0.0, 0.0),
				// An angle of 2 * PI is normalized back to 0 * PI.
				arguments(2.0 * PI, 1.0, 0.0, 1.0),
				// An angle that is already normalized should stay the same.
				arguments(PI, 1.0, PI, 1.0),
				// A negative angle still within 0 and -2 * PI should simply have 2 * PI added to it.
				arguments(-0.5 * PI, 1.0, 1.5 * PI, 1.0),
				// An angle greater than 2 * PI should be normalized down.
				arguments(5.0 * PI, 1.0, PI, 1.0),
				// Check a large negative angle as well.
				arguments(-7.5 * PI, 1.0, 0.5 * PI, 1.0),
				// A negative radius should become positive, and the angle should change by 1 * PI.
				arguments(0.5 * PI, -0.75, 1.5 * PI, 0.75)
		);
	}
}
