package nl.rug.aoop.asteroids.model.connection;

import lombok.Getter;
import nl.rug.aoop.asteroids.model.gameobjects.Asteroid;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * a package that holds information about more than 1 player
 * */
@Getter
public class MultiPlayerGamePackage implements Serializable {

    /**
     * indicates whether own ships has been destroyed
     * */
    private final boolean ownShipDestroyed;

    /**
     * a list of booleans each of which indicate whether a spaceship
     * is accelerating.
     * <p>
     * needed to draw the exhaust
     * */
    private List<Boolean> isAccList;

    /**
     * a list of each ships' direction angle
     * */
    private List<Double> directions;

    /**
     * a list of each ships' location
     * */
    private List<Point2D.Double> spaceShips;

    /**
     * a list of each bullets' location
     * */
    private List<Point2D.Double> bullets;

    /**
     * a list of each asteroids' location
     * */
    private List<Point2D.Double> asteroids;

    /**
     * a list of each asteroids' direction.
     * <p>Needed to draw the non basic circle asteroid</p>
     * */
    private List<Double> asteroidsDirection;

    /**
     * list of asteroids' size
     * */
    private List<Double> asteroidRadius;

    /**
     * score of own ship
     * */
    private final int ownScore;

    /**
     * list of enemies scores
     * */
    private final List<Integer> enemiesScores;

    /**
     * name of own ship
     */
    private final String ownName;

    /**
     * list of enemies ships
     */
    private final List<String> enemiesNames;



    /**
     * creates a multiplayer game package to send to a client
     *
     * @param ownShipDestroyed own state of being a live or dead
     * @param ownName own user name
     * @param enemiesNames names of all other enemies
     * @param ownScore the score of own spaceship
     * @param enemiesScores the scores of all other enemies
     * @param singlePlayerPackages the single player packages to make a multiplayer package from
     * @param asteroids the asteroids of the game
     * @param bullets the bullets of the game
     * */
    public MultiPlayerGamePackage(boolean ownShipDestroyed,
                                  String ownName,
                                  List<String> enemiesNames,
                                  int ownScore,
                                  List<Integer> enemiesScores,
                                  List<SinglePlayerPackage> singlePlayerPackages,
                                  List<Asteroid> asteroids,
                                  List<Point2D.Double> bullets
    ) {
        this.ownShipDestroyed = ownShipDestroyed;
        this.ownScore = ownScore;
        this.ownName = ownName;
        extractSpaceShipInfo(singlePlayerPackages);
        extractAsteroidsInfo(asteroids);
        extractBulletsInfo(bullets);
        this.enemiesScores = enemiesScores;
        this.enemiesNames = enemiesNames;
    }

    /**
     * extracts the bullets' location from the list
     *
     * @param bullets the bullets to add
     * */
    private void extractBulletsInfo(List<Point2D.Double> bullets) {
        this.bullets = bullets;
    }

    /**
     * extracts the asteroids relative information to send
     * from the list
     *
     * @param asteroids the list of the asteroids
     * */
    private void extractAsteroidsInfo(List<Asteroid> asteroids) {
        this.asteroids = new ArrayList<>();
        asteroidsDirection = new ArrayList<>();
        asteroidRadius = new ArrayList<>();
        asteroids.forEach(asteroid -> {
            this.asteroids.add(asteroid.getLocation());
            this.asteroidRadius.add(asteroid.getRadius());
            this.asteroidsDirection.add(asteroid.getDirectionAngle());
        });
    }

    /**
     * extracts the relative spaceships' information to send over to client
     *
     * @param singlePlayerPackages a collection of the single players package to be combined into this multiplayer package
     * */
    private void extractSpaceShipInfo(List<SinglePlayerPackage> singlePlayerPackages) {
        spaceShips = new ArrayList<>();
        directions = new ArrayList<>();
        isAccList = new ArrayList<>();
        singlePlayerPackages.forEach(player -> {
            spaceShips.add(player.getSpaceShip());
            directions.add(player.getDirection());
            isAccList.add(player.isAcc);
        });
    }
}
