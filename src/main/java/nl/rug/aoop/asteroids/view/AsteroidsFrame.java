package nl.rug.aoop.asteroids.view;

import lombok.Getter;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.client.Client;
import nl.rug.aoop.asteroids.control.PlayerKeyListener;
import nl.rug.aoop.asteroids.control.actions.*;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.PanelType;
import nl.rug.aoop.asteroids.server.Server;
import nl.rug.aoop.asteroids.util.SoundEffectPlayer;
import nl.rug.aoop.asteroids.util.database.DatabaseManager;
import nl.rug.aoop.asteroids.view.mainmenu.HighScorePanel;
import nl.rug.aoop.asteroids.view.mainmenu.HostGamePanel;
import nl.rug.aoop.asteroids.view.mainmenu.JoinGamePanel;
import nl.rug.aoop.asteroids.view.mainmenu.MainMenuPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main window that's used for displaying the game.
 */
@Log
public class AsteroidsFrame extends JFrame {
    /**
     * The title which appears in the upper border of the window.
     */
    private static final String WINDOW_TITLE = "Asteroids";

    /**
     * The size that the window should be.
     */
    public static final int WIDTH = 1365;
    public static final int HEIGHT = 800;

    /**
     * dimension of the frame
     * */
    public static final Dimension WINDOW_SIZE = new Dimension(WIDTH, HEIGHT);

    /**
     * path to the icon of the frame
     * */
    public static final String ICON_PATH = "data/graphics/icon.jpg";

    /**
     * The game model.
     */
    @Getter
    private final Game game;

    /**
     * the options menu panel
     * */
    private JPanel optionPanel;

    /**
     * an instance of the main menu panel
     * */
    @Getter
    private MainMenuPanel mainMenuPanel;

    /**
     * an instance of the client side
     * */
    private Client client;

    /**
     * execute service to start submit client and server to
     * */
    @Getter
    private final ExecutorService service;

    /**
     * an instance of the server sides
     * */
    private Server server;

    /**
     * Constructs the game's main window.
     */
    public AsteroidsFrame() {
        game = new Game(this);
        service = Executors.newCachedThreadPool();
        SwingUtilities.invokeLater(this::initSwingUI);
    }

    /**
     * A helper method to do the tedious task of initializing the Swing UI components.
     */
    private void initSwingUI() {
        // Basic frame properties.
        setTitle(WINDOW_TITLE);
        setSize(WINDOW_SIZE);
        setIcon();
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addMenuBar();
        mainMenuPanel = new MainMenuPanel(this);
        optionPanel = mainMenuPanel;
        add(optionPanel, BorderLayout.CENTER);
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
        setFocusable(true);
        new SoundEffectPlayer("mainMenuPanel").playMainMenuMusic(mainMenuPanel);
    }

    /**
     * sets the icon of the frame to {@link #ICON_PATH}
     * */
    private void setIcon() {
        try {
            setIconImage(ImageIO.read(new File(ICON_PATH)));
        } catch (IOException e) {
            log.warning("Unable to load the icon. Check data/icon.jpg");
        }
    }

    /**
     * adds {@link MenuBar} to the frame with
     * {@link QuitToMainMenuAction} and {@link NewGameAction}
     * */
    private void addMenuBar() {
        // Add a menu bar with some simple actions.
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        menuBar.add(menu);
        menu.add(new NewGameAction(this));
        menu.add(new JoinGameAction(this));
        menu.add(new HostGameAction(this));
        menu.add(new HighScoreAction(this));
        menu.add(new QuitToMainMenuAction(this));
        menu.add(new ExitAction(this));
        setJMenuBar(menuBar);
    }

    /**
     * changes the {@link #optionPanel} depending on the button pressed
     * @param option the option of the panel
     * */
    public void changePanel(PanelType option) {
        resetComponents();
        if (option == PanelType.NEW_SOLO_GAME) {
            newSoloGamePanel ();
        }else if (option == PanelType.JOIN_GAME) {
            newJoinGamePanel();
        } else if (option == PanelType.HOST_GAME) {
            newHostGamePanel();
        } else if (option == PanelType.JOINED_GAME) {
            newJoinedGame();
        } else if (option == PanelType.HOSTED_GAME) {
            newHostedGame();
        } else if(option == PanelType.HIGH_SCORES) {
            highScorePanel();
        } else if (option == PanelType.START || option == PanelType.RETURN) {
            reloadStartPanel();
        } else if (option == PanelType.QUIT) {
            resetClientAndServer();
            System.exit(0);
        } else {
            log.warning("Unknown panel type \n You have been kicked out of the space program...");
            System.exit(1);
        }
        mainMenuPanel.setInMainMenu(optionPanel instanceof MainMenuPanel);
        add(optionPanel);
        optionPanel.revalidate();
        optionPanel.repaint();
    }

    /**
     * reloads the start panel {@link MainMenuPanel}
     * */
    private void reloadStartPanel() {
        resetClientAndServer();
        optionPanel = mainMenuPanel;
    }

    /**
     * resets client and server by shutting them down
     * */
    private void resetClientAndServer() {
        if (client != null) {
            client.shutDown();
        }
        if (server != null) server.shutDown();
    }

    /**
     * rests the componenets of the {@link #optionPanel}
     * and {@link #game}
     * */
    private void resetComponents() {
        remove(optionPanel);
        resetGame();
    }

    /**
     * changes {@link #optionPanel} to a new hosted game and lets others join
     * via {@link Server}
     * */
    private void newHostedGame() {
        game.setType(Game.HOST);
        addKeyListener(new PlayerKeyListener(game.getMainSpaceShip()));
        game.getMainSpaceShip().setUserName(mainMenuPanel.getField().getText().toUpperCase());
        game.start();
        optionPanel = new AsteroidsPanel(game);
    }

    /**
     * changes {@link #optionPanel} to a new joined game connected to the host
     * via {@link Client}
     * */
    private void newJoinedGame() {
        game.setType(Game.CLIENT);
        addKeyListener(new PlayerKeyListener(game.getMainSpaceShip()));
        game.getMainSpaceShip().setUserName(mainMenuPanel.getField().getText().toUpperCase());
        game.start();
        optionPanel = new AsteroidsPanel(game);
    }

    /**
     * changes {@link #optionPanel} to a new {@link HostGamePanel}
     * */
    private void newHostGamePanel() {
        if(server != null) server.shutDown();
        server = new Server(this);
        service.submit(server);
        while (!server.isRunning()) {
            JLabel label = new JLabel("Starting server...");
            optionPanel = new JPanel();
            optionPanel.add(label);
            add(optionPanel);
            optionPanel.revalidate();
            optionPanel.repaint();
        }
        optionPanel = new HostGamePanel(server);
    }

    /**
     * changes {@link #optionPanel} to a new {@link JoinGamePanel}
     * */
    private void newJoinGamePanel() {
        try {
            if(client != null) client.shutDown();
            client = new Client(this, game);
        } catch (SocketException e) {
            log.warning("Could not start the game.." +
                    "Please restart the game");
        }
        JoinGamePanel joinGamePanel = new JoinGamePanel(client, this);
        (joinGamePanel).addListener(client);
        optionPanel = joinGamePanel;
    }

    /**
     * changes {@link #optionPanel} to a new solo game
     * */
    private void newSoloGamePanel () {
        resetGame();
        game.setType(Game.SOLO);
        resetClientAndServer();
        game.getMainSpaceShip().setUserName(mainMenuPanel.getField().getText().toUpperCase());
        game.start();
        optionPanel = new AsteroidsPanel(game);
        addKeyListener(new PlayerKeyListener(game.getMainSpaceShip()));
        mainMenuPanel.setInMainMenu(false);
    }

    /**
     * changes {@link #optionPanel} to a high score panel
     */
    public void highScorePanel() {
        DatabaseManager databaseManager = new DatabaseManager("HighScores");
        optionPanel = new HighScorePanel(this, databaseManager.getSortedScores());
        databaseManager.closeDatabase();
    }

    /**
     * resets game by quitting and reloading
     * */
    private void resetGame() {
        game.quit();
        game.initializeGameData();
    }
}
