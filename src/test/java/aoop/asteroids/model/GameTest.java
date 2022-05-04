package aoop.asteroids.model;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.model.AsteroidSize;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.gameobjects.Asteroid;
import nl.rug.aoop.asteroids.model.gameobjects.Bullet;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the methods of the Game model. Particular attention is focused on this model's internal game engine state
 * monitoring. We should be confident that starting and stopping the game will reliably update all of the necessary
 * model properties.
 */
@Log
class GameTest {
	/**
	 * Tests the constructor of the Game model. This isn't really much, besides creating the spaceship object. The rest
	 * is handled by the testing for initializeGameData().
	 */
	@Test
	void testConstructor() {
		Game game = new Game();
		assertNotNull(game.getSpaceShips());
	}

	/**
	 * Tests the game model's method to initialize its data. This means essentially resetting all of the various models
	 * that belong to the main model.
	 */
	@Test
	void testInitializeGameData() {
		Game game = new Game(); // The constructor calls initializeGameData().
		assertTrue(game.getOwnBullets().isEmpty());
		assertTrue(game.getAsteroids().isEmpty());
		// We test the spaceship's reset functionality in a separate test, so don't worry too much about testing it here.
		assertFalse(game.getMainSpaceShip().isDestroyed());
		assertEquals(0, game.getMainSpaceShip().getScore());

		// Now mess up the model a bit, and re-initialize it, then test again.
		game.getAsteroids().add(new Asteroid(
				new Point.Double(400.0, 400.0),
				new Point.Double(5.0, 5.0),
				AsteroidSize.MEDIUM
		));
		game.getOwnBullets().add(new Bullet(400.0, 400.0, 25.0, -25.0));
		game.getMainSpaceShip().destroy();
		for (int i = 0; i < 10; i++) {
			game.getMainSpaceShip().increaseScore();
		}
		game.initializeGameData();
		// Test once more that everything has returned to normal.
		assertTrue(game.getOwnBullets().isEmpty());
		assertTrue(game.getAsteroids().isEmpty());
		assertFalse(game.getMainSpaceShip().isDestroyed());
		assertEquals(0, game.getMainSpaceShip().getScore());
	}

	/**
	 * Tests whether or not the game model can correctly determine if the game is over. This is actually pretty simple
	 * for a single player game; the game is over when the player's ship is destroyed.
	 */
	@Test
	void testIsGameOver() {
		Game game = new Game();
		assertFalse(game.isGameOver());
		game.getMainSpaceShip().destroy();
		assertTrue(game.isGameOver());
	}

	/**
	 * Tests starting a game. This involves setting a simple boolean running flag, as well as starting up the game
	 * updater thread. Since it is quite important that starting the thread works properly, we will use reflection to
	 * gain access to the private field in the Game model which holds the thread.
	 */
	@Test
	void testStart() {
		Game game = new Game();
		// First test a fresh, new game that isn't started.
		assertFalse(game.isRunning());
		Thread updaterThread = this.extractGameUpdaterThread(game);
		assertNull(updaterThread);

		// Start the game, and make sure it is running and the thread is working.
		game.start();
		assertTrue(game.isRunning());
		updaterThread = this.extractGameUpdaterThread(game);
		assertNotNull(updaterThread);
		assertTrue(updaterThread.isAlive());

		// Let's try to start the game again, to make sure it doesn't create a new thread.
		game.start();
		Thread secondUpdaterThread = this.extractGameUpdaterThread(game);
		assertSame(updaterThread, secondUpdaterThread);

		// Shut down the thread without using Game's quit() method; that might lead to circular testing.
		updaterThread.interrupt();
	}

	/**
	 * Tests quitting a game. This means shutting down a running game updater thread.
	 */
	@Test
	void testQuit() {
		Game game = new Game();
		// First test quitting a game that we know is not running.
		assertFalse(game.isRunning());
		Thread updaterThread = this.extractGameUpdaterThread(game);
		assertNull(updaterThread);
		game.quit();
		assertFalse(game.isRunning());
		updaterThread = this.extractGameUpdaterThread(game);
		assertNull(updaterThread);

		// Start up a game.
		game.start(); // Since the test for start() doesn't assume that quit() works, we can assume this works safely.
		// The game is now running, and its updater thread is alive.
		game.quit();
		assertFalse(game.isRunning());
		updaterThread = this.extractGameUpdaterThread(game);
		assertNull(updaterThread);
	}

	/**
	 * A helper method to gain access to the game updater thread within a Game object, using some reflection methods.
	 * Although strictly speaking it's not necessary to test private fields, doing so can make us much more certain that
	 * a class is acting just as it should internally. For checking the behavior of a thread, this a nice convenience to
	 * take advantage of.
	 *
	 * @param game The game to get the updater thread of.
	 *
	 * @return The thread that the game currently has, or null if the game doesn't currently have one.
	 */
	private Thread extractGameUpdaterThread(Game game) {
		try {
			Field updaterThreadField = game.getClass().getDeclaredField("gameUpdaterThread");
			updaterThreadField.setAccessible(true);
			return (Thread) updaterThreadField.get(game);
		} catch (Exception exception) {
			/*
			Many reflection methods throw checked exceptions, but since we know what we're doing, i.e. not misspelling
			field names, it is reasonable to assume that these exceptions will not happen. Therefore, they are caught
			here and a null is returned, instead of having the calling method have to handle them.
			 */
			log.warning("Encountered an error while testing the game thread");
			return null;
		}
	}
}
