package nl.rug.aoop.asteroids.model;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.control.GameUpdater;
import nl.rug.aoop.asteroids.gameobserver.ObservableGame;
import nl.rug.aoop.asteroids.server.ClientHandler;
import nl.rug.aoop.asteroids.model.connection.MultiPlayerGamePackage;
import nl.rug.aoop.asteroids.model.connection.SinglePlayerPackage;
import nl.rug.aoop.asteroids.model.gameobjects.Asteroid;
import nl.rug.aoop.asteroids.model.gameobjects.Bullet;
import nl.rug.aoop.asteroids.model.gameobjects.Spaceship;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import nl.rug.aoop.asteroids.view.errors.ErrorDialog;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is the main model for the Asteroids game. It contains all game objects, and has methods to start and stop
 * the game.
 * <p>
 * This is strictly a model class, containing only the state of the game. Updates to the game are done in
 * {@link GameUpdater}, which runs in its own thread, and manages the main game loop and physics updates.
 */
public class Game extends ObservableGame {
    /**
     * these fields describe the game type
     * */
    public static final String SOLO = "solo";
    public static final String CLIENT = "client";
    public static final String HOST = "host";

    /**
     * the original frame to manipulate
     * */
    @Getter
    private AsteroidsFrame frame;

    /**
     * The spaceship object that the player is in control of.
     */
    @Getter
    private CopyOnWriteArrayList<Spaceship> spaceShips;

    /**
     * The list of all bullets currently active in the game.
     */
    @Getter
    private CopyOnWriteArrayList<Bullet> ownBullets;

    /**
     * contains the enemies bullets
     * */
    @Getter
    private CopyOnWriteArrayList<Bullet> enemiesBullets;

    /**
     * contains the enemies scores
     * */
    @Getter
    private CopyOnWriteArrayList<Bullet> enemiesScores;

    /**
     * The list of all asteroids in the game.
     */
    @Getter
    private CopyOnWriteArrayList<Asteroid> asteroids;

    /**
     * Indicates whether or not the game is running. Setting this to false causes the game to exit its loop and quit.
     */
    private volatile boolean running = false;

    /**
     * The game updater thread, which is responsible for updating the game's state as time goes on.
     */
    private Thread gameUpdaterThread;

    /**
     * Number of milliseconds to wait for the game updater to exit its game loop.
     */
    private static final int EXIT_TIMEOUT_MILLIS = 100;

    /**
     * colors to color the spaceships with
     * */
    private final Color[] colors = new Color[]{Color.DARK_GRAY, Color.RED, Color.BLUE, Color.ORANGE, Color.GREEN,
    Color.CYAN, Color.WHITE, Color.MAGENTA, Color.PINK};

    /**
     * the type of the game being player
     * <p>
     * {@link #SOLO},{@link #CLIENT} or {@link #HOST}
     * */
    @Getter @Setter
    private String type;


    /**
     * map containing all online players that have joined the host's game
     * ~ note this map is a {@link ConcurrentHashMap}. This is because multiple
     * {@link ClientHandler} will be changing values in here
     * */
    @Getter @Setter
    public ConcurrentHashMap<String, SinglePlayerPackage> clientsMap;

    /**
     * the id of the main spaceship / player
     * */
    private String mainId;

    /**
     * separate thread for the to get the hosts wish when dying
     * */
    private ExecutorService optionThread;

    /**
     * Constructs a new game, with a new spaceship and all other model data in its default starting state.
     */
    public Game() {
        spaceShips = new CopyOnWriteArrayList<>();
        spaceShips.add(new Spaceship());
        spaceShips.get(0).reset();
        initializeGameData();
    }

    /**
     * Constructs a new game, with a new spaceship and all other model data in its default starting state.
     * @param frame the frame to manipulate
     */
    public Game(AsteroidsFrame frame) {
        spaceShips = new CopyOnWriteArrayList<>();
        spaceShips.add(new Spaceship());
        spaceShips.get(0).reset();
        initializeGameData();
        this.frame = frame;
    }

    /**
     * Initializes all the model objects used by the game. Can also be used to reset the game's state back to a
     * default starting state before beginning a new game.
     */
    public void initializeGameData() {
        optionThread = null;
        ownBullets = new CopyOnWriteArrayList<>();
        enemiesBullets = new CopyOnWriteArrayList<>();
        asteroids = new CopyOnWriteArrayList<>();
        spaceShips = new CopyOnWriteArrayList<>();
        enemiesScores = new CopyOnWriteArrayList<>();
        spaceShips.add(new Spaceship());
        spaceShips.get(0).reset();
        getMainSpaceShip().setSpaceShipsColor(colors[0]);
        type = SOLO;
    }

    /**
     * @return Whether or not the game is running.
     */
    public synchronized boolean isRunning() {
        return running;
    }

    /**
     * @return True if the player's ship has been destroyed, or false otherwise.
     */
    public boolean isGameOver() {
        return (spaceShips.size() == 0 || spaceShips.get(0).isDestroyed());
    }

    /**
     * Using this game's current model, spools up a new game updater thread to begin a game loop and start processing
     * user input and physics updates. Only if the game isn't currently running, that is.
     */
    public void start() {
        if (!running) {
            running = true;
            gameUpdaterThread = new Thread(new GameUpdater(this));
            gameUpdaterThread.start();
        }
    }

    /**
     * Tries to quit the game, if it is running.
     */
    public void quit() {
        if (running) {
            try {
                // Attempt to wait for the game updater to exit its game loop.
                gameUpdaterThread.join(EXIT_TIMEOUT_MILLIS);
            } catch (InterruptedException exception) {
                System.err.println("Interrupted while waiting for the game updater thread to finish execution.");
            } finally {
                running = false;
                // Throw away the game updater thread and let the GC remove it.
                gameUpdaterThread = null;
                if (frame != null && isGameOver() && type.equals(SOLO)) {
                    ErrorDialog.soloGameOver();
                    frame.changePanel(PanelType.START);
                }
            }
        }
    }

    /**
     * updates the game elements
     * <li> {@link #enemiesBullets} </li>
     * <li> {@link #asteroids} </li>
     * <li> {@link #spaceShips} </li>
     * */
    public synchronized void updateGameElements(MultiPlayerGamePackage mp) {
        updateEnemies(mp, null);
        updateAsteroids(mp);
        updateEnemiesBullets(mp.getBullets(), null);
    }

    /**
     * updates the enemies locations and scores
     * */
    public synchronized void updateEnemies(MultiPlayerGamePackage mp, List<Integer> spaceshipStepsLeft) {
        removeDeadSpaceShips(mp);
        for (int i = spaceShips.size(); i <= mp.getSpaceShips().size(); i++) {
            Spaceship spaceship = new Spaceship();
            spaceShips.add(spaceship);
        }
        for (int i = 1; i <= mp.getSpaceShips().size(); i++) {
            this.spaceShips.get(i).setSpaceShipsColor(colors[i]);
        }
        for (int i = 1; i < spaceShips.size(); i++) {
            Spaceship spaceship = spaceShips.get(i);
            int j = i - 1;
            spaceship.setLocation(mp.getSpaceShips().get(j));
            spaceship.setDirection(mp.getDirections().get(j));
            spaceship.setAccelerateKeyPressed(mp.getIsAccList().get(j));

            if (j < mp.getEnemiesNames().size()) {
                spaceship.setUserName(mp.getEnemiesNames().get(j));
            }

            if (j < mp.getEnemiesScores().size()) {
                spaceship.updateScore(mp.getEnemiesScores().get(j));
            }
            if (type.equals(HOST)) { // only host needs to update the steps left to collide
                spaceship.setStepsUntilCollisionPossible(spaceshipStepsLeft.get(j));
            }
        }
    }

    /**
     * removes dead spaceships
     * @param mp the multiplayer package holding the spaceships
     * */
    private void removeDeadSpaceShips(MultiPlayerGamePackage mp) {
        if (this.spaceShips.size() > mp.getSpaceShips().size()+1) {
            spaceShips.removeIf(spaceShip -> spaceShip!=getMainSpaceShip() && !mp.getSpaceShips().contains(spaceShip.getLocation()));
        }
    }

    /**
     * updates the {@link #enemiesBullets}.
     * */
    public synchronized void updateEnemiesBullets(List<Point2D.Double> bulletsLocations, List<Integer> stepsLeft) {
        if (enemiesBullets.size() > bulletsLocations.size()) {
            enemiesBullets.clear();
        }
        for (int i = 0; i < bulletsLocations.size(); i++) {
            if (i < enemiesBullets.size()) {
                enemiesBullets.get(i).setLocation(bulletsLocations.get(i));
                if(type.equals(HOST)) enemiesBullets.get(i).setStepsUntilCollisionPossible(stepsLeft.get(i));
            } else  {
                if (type.equals(HOST)) {
                    enemiesBullets.add(new Bullet(bulletsLocations.get(i).getX(),
                            bulletsLocations.get(i).getY(),0,0,
                            stepsLeft.get(i)));
                } else {
                    enemiesBullets.add(new Bullet(bulletsLocations.get(i).getX(),
                            bulletsLocations.get(i).getY(),0,0));
                }
            }
        }
    }

    /**
     * updates the {@link #asteroids}.
     * <p>
     * <b> Since we made the asteroids a bit more asteroid like and not basic circles
     * we need an additional field </b>
     * <li>{@link Asteroid#directionAngle}</li>
     * */
    public synchronized void updateAsteroids(MultiPlayerGamePackage mp) {
        this.asteroids.clear();
        for (int i = 0; i < mp.getAsteroids().size(); i++) {
            this.asteroids.add(new Asteroid(
                    mp.getAsteroids().get(i),
                    new Point2D.Double(0,0),
                    mp.getAsteroidRadius().get(i),
                    mp.getAsteroidsDirection().get(i)));
        }
    }

    /**
     * @return own bullets location
     * */
    public List<Point2D.Double> getOwnBulletsLocations() {
        List<Point2D.Double> bu = new ArrayList<>();
        this.ownBullets.forEach(b -> bu.add(b.getLocation()));
        return bu;
    }

    /**
     * @return the main spaceship
     * */
    public Spaceship getMainSpaceShip() {
        return spaceShips.get(0);
    }

    /**
     * removes a spaceship from the game given an index
     * @param index the index at which the spaceship is located
     * */
    public void removeSpaceShip(int index) {
        spaceShips.remove(index);
    }

    /**
     * @return the steps left for own bullets to be able to collide
     * */
    public List<Integer> getOwnBulletsStepsLeft() {
        List<Integer> bu = new ArrayList<>();
        this.ownBullets.forEach(b -> bu.add(b.getStepsUntilCollisionPossible()));
        return bu;
    }

    /**
     * adds main spaceship to the {@link Game#clientsMap}
     *
     * @param mainId the main id of the main spaceship
     * */
    public void addMainSpaceShip(String mainId) {
        this.spaceShips.get(0).setId(mainId);
        SinglePlayerPackage mainPlayer = new SinglePlayerPackage(
                getMainSpaceShip().getLocation(),
                getMainSpaceShip().getDirection(),
                getMainSpaceShip().getStepsUntilCollisionPossible(),
                getMainSpaceShip().isAccelerating(),
                getOwnBulletsLocations(),
                getOwnBulletsStepsLeft(),
                getMainSpaceShip().getUserName());
        mainPlayer.updateMainPlayer(this);
        this.mainId = mainId;
        clientsMap.put(mainId, mainPlayer);
    }

    /**
     * finds the enemies of a specified client
     * @param id the id of the client so we can get their enemies
     * @return a map containing the enemies information
     * */
    public List<SinglePlayerPackage> getEnemies (String id) {
        List<SinglePlayerPackage> singlePlayerPackages = new ArrayList<>();
        clientsMap.forEach((client, objects) -> {
            if(!client.equals(id) && !objects.isDestroyed()) {
                singlePlayerPackages.add(objects);
            }
        });
        return singlePlayerPackages;
    }

    /**
     * gives back the bullets of all enemies in the game
     * @param id the id of client
     * @return list containing the bullets in the game except for own bullets of client
     * */
    public List<Point2D.Double> getEnemiesBullets (String id) {
        List<Point2D.Double> bullets = new ArrayList<>();
        clientsMap.forEach((client, objects) -> {
            if(!client.equals(id)) {
                bullets.addAll(objects.getBullets());
            }
        });
        return bullets;
    }

    /**
     * updates players information
     * @param id the id of the player to update
     * @param spp the single player package associated with this player
     * */
    public void updateInfo(String id, SinglePlayerPackage spp) {
        updateMainSpaceShip();
        clientsMap.put(id, spp);
        updateHostsFrame();
    }

    /**
     * updates the hosts frame
     * */
    public void updateHostsFrame() {
        List<Integer> bulletsStepsLeft = new ArrayList<>();
        List<Integer> spaceshipStepsLeft = new ArrayList<>();
        getEnemies(mainId).forEach(objects -> {
            bulletsStepsLeft.addAll(objects.getOwnBulletsStepsLeft());
            spaceshipStepsLeft.add(objects.getSpaceShipStepsLeft());
        });
        MultiPlayerGamePackage mp = new MultiPlayerGamePackage(
                false,
                getMainSpaceShip().getUserName(),
                getEnemiesNames(mainId),
                getMainSpaceShip().getScore(),
                getEnemiesScores(mainId),
                getEnemies(mainId),
                new ArrayList<>(),
                getEnemiesBullets(mainId));
        updateEnemies(mp, spaceshipStepsLeft);
        updateEnemiesBullets(mp.getBullets(), bulletsStepsLeft);
    }

    /**
     * updates the information of the main spaceship
     * */
    public void updateMainSpaceShip() {
        if (!isGameOver()) {
            clientsMap.get(mainId).updateMainPlayer(this);
        } else {
            if (optionThread == null) {
                optionThread = Executors.newSingleThreadExecutor();
                if (allPlayersAreDead()) {
                    startOverOrEndGame();
                } else {
                    spectateOrQuit();
                }
                clientsMap.remove(mainId);
            }
        }
    }

    /**
     * asks host to spectate or quit
     * */
    private void spectateOrQuit() {
        optionThread.submit(() -> {
            String option = ErrorDialog.gameOver();
            if (option.equals(ErrorDialog.QUIT_TO_MAIN_MENU)) {
                running = false;
                frame.changePanel(PanelType.START);
            }
        });
    }

    /**
     * asks host to host a new game to quit
     * */
    public void startOverOrEndGame() {
        optionThread = Executors.newSingleThreadExecutor();
        optionThread.submit(() -> {
            String option = ErrorDialog.noPlayersLeft();
            running = false;
            if (option.equals(ErrorDialog.NO_PLAYERS_LEFT)) {
                frame.changePanel(PanelType.START);
            } else {
                frame.changePanel(PanelType.HOST_GAME);
            }
        });
    }

    /**
     * @return all players in this game have died
     * */
    public boolean allPlayersAreDead() {
        return spaceShips.size() == 1;
    }

    /**
     * removes a client from online to offline
     * @param id the id of client to be removed
     * */
    public void removeClient(String id) {
        clientsMap.remove(id);
        removeSpaceShip(getClientIndexById(id));
        if (isGameOver() && allPlayersAreDead()) {
            startOverOrEndGame();
        }
    }

    /**
     * @return spaceship holding the id passed as a parameter
     * */
    public Spaceship getClientById(String id) {
        for (Spaceship spaceShip : spaceShips) {
            if (spaceShip.getId().equals(id)) {
                return spaceShip;
            }
        }
        return null;
    }

    /**
     * @return index of the spaceship in the list with id passed as a parameter
     * */
    public int getClientIndexById(String id) {
        for (int i = 0; i < spaceShips.size(); i++) {
            if (spaceShips.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param id the id of the client
     * @return the {@link SinglePlayerPackage} of the client with ID in {@link #clientsMap}
     * */
    public SinglePlayerPackage getClient(String id) {
        return clientsMap.get(id);
    }

    /**
     * @return the shooter of the bullet
     * */
    public Spaceship getBulletOwner(Bullet bullet) {
        AtomicReference<String> ownerId = new AtomicReference<>();
        clientsMap.forEach((id, info) ->
            info.getBullets().forEach(b -> {
                if (bullet.getLocation().equals(b)) {
                    ownerId.set(id);
                }
            })
        );
        if (ownerId.get() == null) {
            return null;
        }
        if (getClientById(ownerId.get()) == null) {
            System.err.println("A black hole has swallowed your spaceship. Please restart the game");
            System.exit(69);
        }
        return getClientById(ownerId.get());
    }

    /**
     * @return a list of the enemies scores
     * */
    public List<Integer> getEnemiesScores(String id) {
        List<Integer> scores = new ArrayList<>();
        clientsMap.forEach((client, objects) -> {
            if(!client.equals(id) && !objects.isDestroyed()) {
                scores.add(getClientById(client).getScore());
            }
        });
        return scores;
    }

    /**
     * adds a new client to the game
     * @param spaceship the spaceship of the new client
     * */
    public void addClient(Spaceship spaceship) {
        spaceShips.get(0).setId(mainId);
        spaceShips.add(spaceship);
    }

    /**
     * @return a list of the enemies names
     */
    public List<String> getEnemiesNames(String id) {
        List<String> names = new ArrayList<>();
        clientsMap.forEach((client, objects) -> {
            if(!client.equals(id) && !objects.isDestroyed()) {
                names.add(objects.getUserName());
            }
        });
        return names;
    }
}
