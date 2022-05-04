package aoop.asteroids.model;

import nl.rug.aoop.asteroids.model.AsteroidSize;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the AsteroidSize enum class. There's not much to test here, besides ensuring that each different size of
 * asteroid devolves into the right sized successor.
 */
class AsteroidSizeTest {
	/**
	 * Tests that each size of asteroid, when destroyed, produces the correct size of successor asteroids.
	 */
	@Test
	void testGetSuccessorSize() {
		assertEquals(AsteroidSize.MEDIUM, AsteroidSize.LARGE.getSuccessorSize());
		assertEquals(AsteroidSize.SMALL, AsteroidSize.MEDIUM.getSuccessorSize());
		assertNull(AsteroidSize.SMALL.getSuccessorSize());
	}
}
