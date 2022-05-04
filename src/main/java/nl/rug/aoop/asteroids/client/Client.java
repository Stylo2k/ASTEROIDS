package nl.rug.aoop.asteroids.client;

import lombok.Getter;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.PanelType;
import nl.rug.aoop.asteroids.model.connection.ConnectionEssentials;
import nl.rug.aoop.asteroids.model.connection.SinglePlayerPackage;
import nl.rug.aoop.asteroids.model.connection.TrafficHandler;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import nl.rug.aoop.asteroids.view.errors.ErrorDialog;
import nl.rug.aoop.asteroids.view.mainmenu.JoinListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * represents a client / player that joins a hosts game
 * */
@Log
public class Client extends TrafficHandler implements JoinListener, Runnable {
    /**
     * time we give the client to connect to the server
     * */
    private static final int SLEEP_TIME = 50;

    /**
     * socket to connect to server
     * */
    private final DatagramSocket s;

    /**
     * the frame to manipulate
     * */
    private final AsteroidsFrame frame;

    /**
     * port to which this client will connect
     * */
    private int port;

    /**
     * instance of the game
     * */
    private final Game game;

    /**
     * essentials to connect to server
     * */
    private ConnectionEssentials ce;

    /**
     * essentials to connect to server
     * */
    private ConnectionEssentials quitCE;

    /**
     * indicates whether the client is running
     * */
    private boolean running;

    /**
     * indicates whether the client has joined a game
     * */
    @Getter
    private boolean joined;

    /**
     * represents a player
     * @param game the instance of the game to which the client joins
     * */
    public Client(AsteroidsFrame frame, Game game) throws SocketException {
        this.game = game;
        this.s = new DatagramSocket();
        this.running = false;
        joined = false;
        this.frame = frame;
    }

    /**
     * communicates with server. By sending information of the game and receiving other
     * makes use of the {@link SinglePlayerPackage} to send the info over
     * */
    @Override
    public void run() {
        running = true;
        boolean destroyed = false;
        while (running) {
            try {
                sendOwnInformation();
                var mp = receiveMultiPlayerGamePackage(s);
                if (!destroyed) {
                    destroyed = mp.isOwnShipDestroyed();
                    game.getMainSpaceShip().updateScore(mp.getOwnScore());
                    game.getMainSpaceShip().setUserName(mp.getOwnName());
                    if (destroyed) {
                        destroyOwnShip();
                        spectateOrQuit();
                    }
                }
                game.updateGameElements(mp);
            } catch (IOException | ClassNotFoundException e) {
                log.warning("Could not receive information from the client" +
                        "Please restart the server");
                running = false;
            }
        }
    }

    /**
     * asks client through dialog whether to quit or spectate
     * */
    private void spectateOrQuit() {
        String option = ErrorDialog.gameOver();
        if (option.equals(ErrorDialog.QUIT_TO_MAIN_MENU)) {
            running = false;
            frame.changePanel(PanelType.START);
        }
    }

    /**
     * destroys own ship
     * */
    private void destroyOwnShip() {
        game.removeSpaceShip(0);
        game.getMainSpaceShip().destroy();
    }

    /**
     * sends own information to the server
     * */
    private void sendOwnInformation() throws IOException {
        SinglePlayerPackage singlePlayerPackage = new SinglePlayerPackage(
                game.getMainSpaceShip().getLocation(),
                game.getMainSpaceShip().getDirection(),
                game.getMainSpaceShip().getStepsUntilCollisionPossible(),
                game.getMainSpaceShip().isAccelerating(),
                game.getOwnBulletsLocations(),
                game.getOwnBulletsStepsLeft(),
                game.getMainSpaceShip().getUserName()
        );
        singlePlayerPackage.setDestroyed(game.getMainSpaceShip().isDestroyed());
        sendSinglePlayerGamePackage(singlePlayerPackage,s, ce);
    }

    /**
     * shuts down the thread responsible for connecting this client to the server
     * */
    public void shutDown() {
        if (running) {
            try {
                sendQuit(s, quitCE);
            } catch (IOException e) {
                log.warning("Encountered an error while sending the shutting down the server");
            }
        }
        running = false;
    }

    /**
     * connects the client to server using
     * <li>{@link ConnectionEssentials}</li>
     * <li>{@link TrafficHandler}</li>
     * */
    private void connectToServer() throws IOException {
        ce = new ConnectionEssentials(InetAddress.getByName("localhost"), port);
        sendInitPacket(s, ce);
        DatagramPacket packet = receive(s);
        listenForQuitActions(ce);

        joined = true;
        this.port = packet.getPort();
        ce = new ConnectionEssentials(packet.getAddress(), port);
        DatagramPacket packet1 = receive(s);
        quitCE = new ConnectionEssentials(packet1.getAddress(), packet1.getPort());
    }

    /**
     * attempts the client to join a game at the given port
     * @param port the port to join to
     * */
    @Override
    public void attemptToJoin(int port) {
        this.port = port;
        ExecutorService thread = Executors.newSingleThreadExecutor();
        thread.submit(() -> {
            try {
                connectToServer();
            } catch (IOException e) {
                log.warning("Encountered an error while trying to connect to the host" +
                        "Please notify them to host a new game");
            }
        });
        waitForConnection();
        if (!joined) {
            thread.shutdownNow();
        }
    }

    /**
     * makes this thread sleep. which gives time for the client to connect.
     * Since the method `connectToServer` is a blocking method. If the {@link #SLEEP_TIME}
     * elapses and a connection has not been established yet. Then the give port is wrong.
     * */
    private void waitForConnection() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            log.warning("Thread was interrupted" +
                    "Game might not function properly");
        }
    }

    /**
     * listens for quit action done by the host
     * */
    private void listenForQuitActions(ConnectionEssentials ce) {
        ExecutorService thread = Executors.newSingleThreadExecutor();
        thread.submit(() -> {
            try {
                DatagramSocket quitSocket = new DatagramSocket();
                sendInitPacket(quitSocket, ce);
                receiveQuit(quitSocket);
                running = false;
                ErrorDialog.kickedFromServer();
                frame.changePanel(PanelType.START);
            } catch (IOException e) {
                log.warning("Could not listen for a quit action from the host");
            }
        });
    }
}
