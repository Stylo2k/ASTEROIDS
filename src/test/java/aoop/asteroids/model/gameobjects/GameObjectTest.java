package aoop.asteroids.model.gameobjects;

import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests (some) of the functionality of the GameObject class. There are several methods which child classes do not
 * change, so testing them here saves the tedium of having to write duplicate tests for every child class.
 */
class GameObjectTest {
	/**
	 * Tests the functionality of the destroy() method. To avoid circular testing with the isDestroyed() method, we will
	 * use reflection here to check the value of the game object's private field.
	 */
	@Test
	void testDestroy() throws IllegalAccessException, NoSuchFieldException {
		// Create a new object that isn't destroyed yet.
		GameObject obj = this.getRandomGameObjectImplementation();
		Field destroyedField = GameObject.class.getDeclaredField("destroyed");
		destroyedField.setAccessible(true);
		assertFalse((boolean) destroyedField.get(obj));
		// Now destroy it, and make sure it got destroyed.
		obj.destroy();
		assertTrue((boolean) destroyedField.get(obj));
		// Make sure that calling destroy() on a destroyed object keeps it destroyed.
		obj.destroy();
		assertTrue((boolean) destroyedField.get(obj));
	}

	/**
	 * Tests whether or not a game object can determine if it was destroyed. Since we've already tested the destroy()
	 * method without using isDestroyed(), we know it works, and can use it here without circular testing.
	 */
	@Test
	void testIsDestroyed() {
		GameObject obj = this.getRandomGameObjectImplementation();
		assertFalse(obj.isDestroyed());
		obj.destroy();
		assertTrue(obj.isDestroyed());
		// Once again, just to make sure, try destroying a destroyed object.
		obj.destroy();
		assertTrue(obj.isDestroyed());
	}

	/**
	 * Tests the default implementation for stepping through one game tick for a game object. In this case, the object
	 * should move at a constant speed across the field, wrapping across the screen when nearing the borders.
	 *
	 * This runs several times (denoted by the @RepeatedTest(X) annotation), since we're using randomly generated game
	 * objects.
	 */
	@RepeatedTest(100)
	void testNextStep() {
		GameObject obj = this.getRandomGameObjectImplementation();
		// We use many steps to ensure that the object can wrap around the screen properly.
		for (int i = 0; i < 1000; i++) {
			Point.Double locationBeforeStep = (Point.Double) obj.getLocation().clone();
			obj.nextStep();
			Point.Double locationAfterStep = (Point.Double) obj.getLocation().clone();
			assertEquals(
					locationAfterStep.getX(),
					(AsteroidsFrame.WINDOW_SIZE.width + locationBeforeStep.getX() + obj.getVelocity().getX()) % AsteroidsFrame.WINDOW_SIZE.width,
					0.000001
			);
			assertEquals(
					locationAfterStep.getY(),
					(AsteroidsFrame.WINDOW_SIZE.height + locationBeforeStep.getY() + obj.getVelocity().getY()) % AsteroidsFrame.WINDOW_SIZE.height,
					0.000001
			);
		}
	}

	/**
	 * Tests the default implementation for checking collisions of game objects. Checks first that any object whose
	 * invulnerability has not worn out cannot collide, and then goes through very many steps, moving the objects across
	 * the screen and checking that when they are certain to collide, that the collides() method can determine this.
	 */
	@RepeatedTest(100)
	void testCollides() {
		GameObject firstObject = this.getRandomGameObjectImplementation();
		int stepsUntilFirstCanCollide = firstObject.getDefaultStepsUntilCollisionPossible();
		GameObject secondObject = this.getRandomGameObjectImplementation();
		int stepsUntilSecondCanCollide = secondObject.getDefaultStepsUntilCollisionPossible();

		// Go through very many iterations of the objects moving across the screen.
		for (int i = 0; i < 10000; i++) {
			// First, if at least one object is invulnerable, check to make certain that the objects cannot collide.
			if (stepsUntilFirstCanCollide > 0 || stepsUntilSecondCanCollide > 0) {
				assertFalse(firstObject.collides(secondObject), "First object collided with second object, when both were not yet invulnerable.");
				assertFalse(secondObject.collides(firstObject), "Second object collided with first object, when both were not yet invulnerable.");
			} else {
				// It is possible for the two objects to collide, so check that the method can properly determine when they do collide.
				double distance = firstObject.getLocation().distance(secondObject.getLocation());
				double radiiSum = firstObject.getRadius() + secondObject.getRadius();
				if (distance < radiiSum) {
					assertTrue(firstObject.collides(secondObject), "First object did not collide with second object, when distance between them was less than the sum of their radii. Distance: " + distance + ", Sum of radii: " + radiiSum);
					assertTrue(secondObject.collides(firstObject), "Second object did not collide with first object, when distance between them was less than the sum of their radii. Distance: " + distance + ", Sum of radii: " + radiiSum);
				} else {
					assertFalse(firstObject.collides(secondObject), "First object collided with second object, when distance between them was greater than or equal to the sum of their radii. Distance: " + distance + ", Sum of radii: " + radiiSum);
					assertFalse(secondObject.collides(firstObject), "Second object collided with first object, when distance between them was greater than or equal to the sum of their radii. Distance: " + distance + ", Sum of radii: " + radiiSum);
				}
			}

			firstObject.nextStep();
			stepsUntilFirstCanCollide--;
			secondObject.nextStep();
			stepsUntilSecondCanCollide--;
		}
	}

	/**
	 * Ensures that getSpeed() returns the proper value for the speed of an object, all the time.
	 */
	@Test
	void testGetSpeed() {
		// First test an object with zero speed. All the other properties don't matter.
		Point.Double location = new Point.Double(1, 1); // An arbitrary location to make creating objects less tedious.
		GameObject stoppedObject = this.getGameObjectImplementation(location, new Point.Double(0, 0), 5, 20);
		assertEquals(0.0, stoppedObject.getSpeed());

		// Now test game objects where movement is only one one axis.
		GameObject positiveX = this.getGameObjectImplementation(location, new Point.Double(10, 0), 5, 20);
		assertEquals(10.0, positiveX.getSpeed());
		GameObject positiveY = this.getGameObjectImplementation(location, new Point.Double(0, 10), 5, 20);
		assertEquals(10.0, positiveY.getSpeed());
		GameObject negativeX = this.getGameObjectImplementation(location, new Point.Double(-10, 0), 5, 20);
		assertEquals(10.0, negativeX.getSpeed());
		GameObject negativeY = this.getGameObjectImplementation(location, new Point.Double(0, -10), 5, 20);
		assertEquals(10.0, negativeY.getSpeed());

		// And finally, test lots of possibilities in between.
		for (int i = 0; i < 1000; i++) {
			GameObject obj = this.getRandomGameObjectImplementation();
			double x = obj.getVelocity().getX();
			double y = obj.getVelocity().getY();
			double expectedSpeed = Math.sqrt(x * x + y * y);
			assertEquals(expectedSpeed, obj.getSpeed(), 0.00001);
		}
	}

	/**
	 * @return An implementation of the GameObject class, with random location, velocity, radius, and steps until
	 * collision.
	 */
	private GameObject getRandomGameObjectImplementation() {
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		Point.Double location = new Point.Double(rng.nextDouble(AsteroidsFrame.WINDOW_SIZE.width), rng.nextDouble(AsteroidsFrame.WINDOW_SIZE.height));
		Point.Double velocity = new Point.Double(rng.nextDouble(-25, 25), rng.nextDouble(-25, 25));
		double radius = rng.nextDouble(50);
		int stepsUntilCollision = rng.nextInt(30);
		return this.getGameObjectImplementation(location, velocity, radius, stepsUntilCollision);
	}

	/**
	 * Returns a GameObject with the given properties. This is useful for testing methods that child classes don't
	 * change, so we can test the default implementation provided by the abstract class. Javadoc on the anonymous class
	 * methods has been omitted for brevity. If you'd like to read it, please see {@link GameObject}.
	 *
	 * @param location The location of the game object.
	 * @param velocity The velocity of the object.
	 * @param radius The radius of the object.
	 * @param stepsUntilCollision The number of steps until the object may collide.
	 *
	 * @return The newly created GameObject.
	 */
	private GameObject getGameObjectImplementation(Point.Double location, Point.Double velocity, double radius, int stepsUntilCollision) {
		class GameObjectImpl extends GameObject {
			private GameObjectImpl(Point.Double location, Point.Double velocity, double radius) {
				super(location, velocity, radius);
			}

			@Override
			public int getDefaultStepsUntilCollisionPossible() {
				return stepsUntilCollision;
			}
		}

		return new GameObjectImpl(location, velocity, radius);
	}
}
