package nl.rug.aoop.asteroids.view.mainmenu;

/**
 * interface for classes that will listen for any attempts to join
 * by the client
 * */
public interface JoinListener {

    /**
     * attempts to join to the server hosted on the port given
     *
     * @param port the port to connect to
     * */
    void attemptToJoin(int port);
}
