package aoop.asteroids.model.gameobjects;

import nl.rug.aoop.asteroids.model.AsteroidSize;
import nl.rug.aoop.asteroids.model.gameobjects.Asteroid;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the unique methods that asteroids have, separate from those it inherits from GameObject.
 */
class AsteroidTest {
	/**
	 * Test that the constructor successfully takes in an AsteroidSize, and sets the asteroid's radius to that defined
	 * by the given size.
	 */
	@Test
	void testConstructor() {
		Point.Double location = new Point.Double(400.0, 400.0);
		Point.Double velocity = new Point.Double(10.0, 10.0);
		Assertions.assertEquals(new Asteroid(location, velocity, AsteroidSize.LARGE).getRadius(), AsteroidSize.LARGE.getRadius());
		assertEquals(new Asteroid(location, velocity, AsteroidSize.MEDIUM).getRadius(), AsteroidSize.MEDIUM.getRadius());
		assertEquals(new Asteroid(location, velocity, AsteroidSize.SMALL).getRadius(), AsteroidSize.SMALL.getRadius());
	}

	/**
	 * Ensures that an asteroid will spawn successors properly when destroyed. Tests that each successor has the
	 * intended position and velocity.
	 *
	 * This test makes use of randomly generated asteroid objects, so it is repeated many times to ensure all cases are
	 * tested.
	 */
	@RepeatedTest(100)
	void testGetSuccessors() {
		Asteroid asteroid = this.generateRandomAsteroid();
		Collection<Asteroid> successors = asteroid.getSuccessors();
		for (Asteroid successor : successors) {
			assertEquals(asteroid.getLocation(), successor.getLocation());
			assertEquals(asteroid.getVelocity().getX(), successor.getVelocity().getX(), Asteroid.SUCCESSOR_VELOCITY_DIFFERENCE);
			assertEquals(asteroid.getVelocity().getY(), successor.getVelocity().getY(), Asteroid.SUCCESSOR_VELOCITY_DIFFERENCE);
		}
	}

	/**
	 * @return A randomly generated asteroid, of a random size, in a random location, with a random velocity.
	 */
	private Asteroid generateRandomAsteroid() {
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		int choice = rng.nextInt(3);
		AsteroidSize size;
		if (choice == 0) {
			size = AsteroidSize.LARGE;
		} else if (choice == 1) {
			size = AsteroidSize.MEDIUM;
		} else {
			size = AsteroidSize.SMALL;
		}
		return new Asteroid(
				new Point.Double(rng.nextDouble(AsteroidsFrame.WINDOW_SIZE.width), rng.nextDouble(AsteroidsFrame.WINDOW_SIZE.height)),
				new Point.Double(rng.nextDouble(-25, 25), rng.nextDouble(-25, 25)),
				size
		);
	}
}
