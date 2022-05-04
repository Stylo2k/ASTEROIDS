package aoop.asteroids.model.gameobjects;

import nl.rug.aoop.asteroids.model.gameobjects.Spaceship;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the functionality of the spaceship, whose changes during a game tick are quite complex, involving energy
 * limitations, weapon cooldown, and velocity dampening.
 *
 * TODO: Add additional testing for the various things that happen when nextStep() is called.
 */
class SpaceshipTest {
	/**
	 * Tests resetting a spaceship to default values. Since this is essentially a setter for many different fields,
	 * there is not really a need to check lots of extraneous cases, just that everything is as expected after calling
	 * reset().
	 */
	@Test
	void testReset() {
		Spaceship ship = new Spaceship();
		ship.reset();
		assertEquals(0, ship.getDirection());
		assertEquals(0, ship.getScore());
		assertEquals(100.0, ship.getEnergyPercentage());
		assertFalse(ship.isAccelerating());
		assertEquals(AsteroidsFrame.WINDOW_SIZE.width / 2.0, ship.getLocation().getX());
		assertEquals(AsteroidsFrame.WINDOW_SIZE.height / 2.0, ship.getLocation().getY());
	}

	/**
	 * Tests the ship's turning during its nextStep method.
	 */
	@Test
	void testNextStepTurning() {
		final double netEnergyLossWhenTurning = Spaceship.TURNING_ENERGY_COST - Spaceship.ENERGY_GENERATION;
		// Test turning.
		Spaceship ship = new Spaceship();
		assertEquals(0.0, ship.getDirection());
		assertEquals(100.0, ship.getEnergyPercentage());

		double previousEnergyPercentage = ship.getEnergyPercentage();
		ship.setTurnRightKeyPressed(true);
		ship.nextStep(); // Turn to the right once.
		assertEquals(0.04 * Math.PI, ship.getDirection(), 0.00001); // Check the new direction.
		assertEquals(this.getExpectedEnergyPercentage(previousEnergyPercentage, netEnergyLossWhenTurning), ship.getEnergyPercentage(), 0.00001);
		previousEnergyPercentage = ship.getEnergyPercentage();

		ship.setTurnLeftKeyPressed(true);
		ship.setTurnRightKeyPressed(false);
		ship.nextStep(); // Turn left back to the starting direction.
		assertEquals(0.0, ship.getDirection(), 0.00001);
		assertEquals(this.getExpectedEnergyPercentage(previousEnergyPercentage, netEnergyLossWhenTurning), ship.getEnergyPercentage(), 0.00001);
		previousEnergyPercentage = ship.getEnergyPercentage();

		ship.nextStep(); // Turn to the left once.
		assertEquals(-0.04 * Math.PI, ship.getDirection(), 0.00001);
		assertEquals(this.getExpectedEnergyPercentage(previousEnergyPercentage, netEnergyLossWhenTurning), ship.getEnergyPercentage(), 0.00001);
		previousEnergyPercentage = ship.getEnergyPercentage();

		// Now try turning both at once. Energy should be used, and ship should keep current direction.
		ship.setTurnRightKeyPressed(true);
		ship.nextStep();
		assertEquals(-0.04 * Math.PI, ship.getDirection(), 0.00001);
		assertEquals(this.getExpectedEnergyPercentage(previousEnergyPercentage, netEnergyLossWhenTurning), ship.getEnergyPercentage(), 0.00001);
	}

	/**
	 * Gets the expected energy percentage readout for a ship after it incurs some loss of energy.
	 *
	 * @param previousPercentage The energy percentage of a ship before doing the thing for which it expects to lose
	 *                           energy.
	 * @param expectedLoss The amount of energy that the ship expects to lose. This should be a positive number.
	 *
	 * @return The energy percentage that the ship should display after losing the given amount of energy.
	 */
	// the error here is from provided code
	private double getExpectedEnergyPercentage(double previousPercentage, double expectedLoss) {
		double expectedLossPercentage = expectedLoss * 100 / Spaceship.ENERGY_CAPACITY;
		return previousPercentage - expectedLossPercentage;
	}
}
