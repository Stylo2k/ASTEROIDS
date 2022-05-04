package nl.rug.aoop.asteroids.server;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.connection.ConnectionEssentials;
import nl.rug.aoop.asteroids.model.connection.MultiPlayerGamePackage;
import nl.rug.aoop.asteroids.model.connection.SinglePlayerPackage;
import nl.rug.aoop.asteroids.model.connection.TrafficHandler;
import nl.rug.aoop.asteroids.model.gameobjects.Spaceship;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * handles and communicates with the clients separately
 * */
@Log
public class ClientHandler extends TrafficHandler implements Runnable {
    /**
     * instance of the game being played
     * */
    private final Game game;

    /**
     * indicates whether the class is running
     * */
    private boolean running;

    /**
     * id of the client that we made this class for
     * */
    private final String id;

    /**
     * socket to communicate with
     * */
    private final DatagramSocket s;

    /**
     * connection essentials needed to communicate
     * {@link ConnectionEssentials}
     * */
    private final ConnectionEssentials ce;

    /**
     * last know score of the spaceships
     * */
    private int lastScore;

    /**
     * the space of this client
     * */
    private Spaceship spaceship;

    /**
     * the user name of the client
     * */
    private String userName;

    /**
     * makes an instance of this client handler.
     * <p>
     *     communicates separately with each client and sends and receives information from
     *     them
     * </p>
     * */
    public ClientHandler(String id, Game game, ConnectionEssentials ce) throws IOException {
        this.id = id;
        this.game = game;
        this.lastScore = 0;
        running = false;
        this.ce = ce;
        this.s = new DatagramSocket();
        // send new init packet so client now communicates with a new separate socket and port
        sendInitPacket(s,ce);
    }

    /**
     * sends and receives information from the client
     * then updates own game
     * finally sends newly states to the client
     * */
    @Override
    public void run() {
        running = true;
        boolean destroyed = false;
        listenForQuitActions();
        assignClientASpaceShip();
        do {
            try {
                SinglePlayerPackage spp = receiveSinglePlayerGamePackage(s);
                this.userName = spp.getUserName();
                if (!destroyed) {
                    game.updateInfo(id, spp);
                } else {
                    game.updateMainSpaceShip();
                    game.updateHostsFrame();
                }
                if (!destroyed) {
                    destroyed = this.game.getClientById(id).isDestroyed();
                    lastScore = this.game.getClientById(id).getScore();
                    if(destroyed) {
                        spp.setDestroyed(true);
                        game.removeClient(id);
                    }
                }
                makeAndSendMultiPlayerPackage(destroyed);
            } catch (IOException | ClassNotFoundException e) {
                log.warning("Encountered an error while communicating with the client." +
                        "Clients ships color : " + spaceship.getSpaceShipsColor().toString());
                running = false;
            }
        } while (running);
    }

    /**
     * assigns this client a spaceship
     * */
    private void assignClientASpaceShip() {
        spaceship = new Spaceship();
        spaceship.setId(id);
        game.addClient(spaceship);
    }

    /**
     * makes and sends the package that hold information about other players
     * */
    private void makeAndSendMultiPlayerPackage(boolean destroyed) throws IOException {
        MultiPlayerGamePackage mp = new MultiPlayerGamePackage(
                destroyed,
                userName,
        this.game.getEnemiesNames(id),
        lastScore,
        this.game.getEnemiesScores(id),
        this.game.getEnemies(id),
        this.game.getAsteroids(),
        this.game.getEnemiesBullets(id));
        sendMultiPlayerGamePackage(mp, s, ce);
    }

    /**
     * listens for quit actions from the client
     * */
    private void listenForQuitActions() {
        ExecutorService thread = Executors.newSingleThreadExecutor();
        thread.submit(() -> {
            try {
                DatagramSocket quitSocket = new DatagramSocket();
                sendInitPacket(quitSocket, ce);
                receiveQuit(quitSocket);
                game.getClient(id).setDestroyed(true);
                this.game.getClientById(id).destroy();
                game.removeClient(id);
                running = false;
            } catch (IOException e) {
                log.warning("Could not receive message from client quitting the game.");
            }
        });
    }
}