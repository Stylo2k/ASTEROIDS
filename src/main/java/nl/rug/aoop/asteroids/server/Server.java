package nl.rug.aoop.asteroids.server;

import lombok.Getter;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.PanelType;
import nl.rug.aoop.asteroids.model.connection.ConnectionEssentials;
import nl.rug.aoop.asteroids.model.connection.TrafficHandler;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * server created by the host for the client to connect to
 * */
@Log
public class Server extends TrafficHandler implements Runnable {
    /**
     * static field of value 0 so the server gets random available
     *
     * */
    private static final int STATIC_PORT = 0;

    /**
     * the port to which we let others connect
     * */
    @Getter
    public int port;

    /**
     * indicates whether the server is running
     * */
    @Getter
    private boolean running;

    /**
     * service to which we submit {@link ClientHandler} so it handles
     * each client separately
     * */
    private final ExecutorService service;

    /**
     * indicates whether the game has started
     * */
    private boolean gameStarted;

    /**
     * the original frame from which we get info and send info to
     * */
    @Getter
    private final AsteroidsFrame frame;

    /**
     * instance of game to extract info from and update
     * */
    @Getter
    private final Game game;

    /**
     * id specified for the main spaceship
     * */
    public static String mainId;

    /**
     * the quit connection essentials for listening on a different port
     * */
    private final List<ConnectionEssentials> quitCEs;

    /**
     * the socket used by the server
     * */
    private DatagramSocket s;

    /**
     * represents a server that runs when the user wishes to host a game
     * @param frame the frame that should contain the info
     * */
    public Server(AsteroidsFrame frame) {
        this.frame = frame;
        this.running = false;
        this.game = frame.getGame();
        game.setClientsMap(new ConcurrentHashMap<>());
        gameStarted = false;
        this.service = Executors.newCachedThreadPool();
        mainId = UUID.randomUUID().toString();
        game.addMainSpaceShip(mainId);
        this.quitCEs = new ArrayList<>();
    }

    /**
     * handles new incoming requests
     * */
    @Override
    public void run() {
        try {
            s = new DatagramSocket(STATIC_PORT);
            port = s.getLocalPort();
            running = true;
            while (running) {
                handleRequests(s);
            }
        } catch (Exception e) {
            log.warning("Encountered an error while making server");
            running = false;
        }
    }

    /**
     * shuts the server down by setting the {@link #running} to false
     * */
    public void shutDown() {
        if (running) {
            quitCEs.forEach(ce -> {
                try {
                    sendQuit(s, ce);
                } catch (IOException e) {
                    log.warning("Encountered an error while shutting down the server." +
                            "Client will not get the shutdown notification");
                }
            });
        }
        running = false;
    }

    /**
     * handles incoming requests by spawning a {@link ClientHandler}
     * for each new client.
     * */
    private void handleRequests(DatagramSocket s) {
        try {
            ConnectionEssentials ce = connect(s);
            if (!gameStarted) frame.changePanel(PanelType.HOSTED_GAME);
            gameStarted = true;
            String id = UUID.randomUUID().toString();
            service.submit(new ClientHandler(id, game, ce));
            log.info("Client Joined: players joined " + (game.getClientsMap().size() + 1));

            DatagramPacket packet1 = receive(s);
            quitCEs.add(new ConnectionEssentials(packet1.getAddress(), packet1.getPort()));

        } catch (IOException e) {
            log.warning("Could not connect with client." +
                    "Please host a new game");
        }

    }

    /**
     * establishes connection with client
     * @return the connection essentials to be used to communicate with client
     * */
    private ConnectionEssentials connect(DatagramSocket s) throws IOException {
        DatagramPacket packet = receive(s);
        return new ConnectionEssentials(packet.getAddress(), packet.getPort());
    }
}
