package nl.rug.aoop.asteroids.gameobserver;

/**
 * Classes which implement this interface indicate that they would like to be notified when a game is updated, and must
 * implement the onGameUpdated() method to do something when the game is updated.
 */
public interface GameUpdateListener {
	/**
	 * This method is called when the game that this listener is listening to announces that it should update.
	 *
	 * @param timeSinceLastTick The number of milliseconds that have passed since the last game tick occurred. This is
	 *                          used so that things like a display may continue showing an animated model while no
	 *                          actual physics update has been done by the game engine.
	 */
	void onGameUpdated(long timeSinceLastTick);
}
