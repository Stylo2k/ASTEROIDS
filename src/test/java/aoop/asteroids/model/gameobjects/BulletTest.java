package aoop.asteroids.model.gameobjects;

import nl.rug.aoop.asteroids.model.gameobjects.Bullet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the unique characteristics of the Bullet game object. The only different behavior that the bullet defines, is
 * that after some set number of steps (set by a constant), the bullet will destroy itself.
 */
class BulletTest {
	/**
	 * Test that after the bullet's set lifetime, it is destroyed. To do that, we assert that while the bullet is has
	 * gone through a number of steps less than its lifetime, it should not be destroyed. Once we pass that lifetime,
	 * then we assert that it should now be destroyed.
	 */
	@Test
	void testNextStep() {
		Bullet bullet = new Bullet(400.0, 400.0, 30.0, -30.0);
		for (int i = 0; i < Bullet.DEFAULT_BULLET_STEP_LIFETIME; i++) {
			assertFalse(bullet.isDestroyed());
			bullet.nextStep();
		}
		assertTrue(bullet.isDestroyed());
	}
}
