package nl.rug.aoop.asteroids.model.connection;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.gameobjects.Spaceship;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * holds information about a single player.
 * <p>
 *     this package gets sent from client to server
 * </p>
 * */
@Getter @Setter
public class SinglePlayerPackage extends TrafficHandler implements Serializable {

    /**
     * direction of the spaceship
     * */
    public double direction;

    /**
     * if the spaceship is accelerating
     * */
    public boolean isAcc;

    /**
     * location of spaceship in the game
     * */
    private Point2D.Double spaceShip;

    /**
     * list of locations of other players bullets
     * */
    private List<Point2D.Double> bullets;

    /**
     * indicates whether the spaceship has been destroyed
     * */
    private boolean isDestroyed;

    /**
     * steps left for own bullets until being able to collide
     * */
    private List<Integer> ownBulletsStepsLeft;

    /**
     * steps left of own spaceship till being able to collide
     * */
    private int spaceShipStepsLeft;

    /**
     * username of spaceship
     */
    @Setter @Getter
    private String userName;

    /**
     * represents a package holding info about one single player
     *
     * @param spaceShip the spaceships location
     * @param direction the direction of the spaceship
     * @param spaceShipStepsLeft the steps left until this ship is allowed to collide
     * @param isAcc the fact if this spaceship is accelerating
     * @param bullets the own bullets of the spaceship
     * @param ownBulletsStepsLeft the steps left for the bullets to be able to collide
     * @param userName the username of the client
     * */
    public SinglePlayerPackage(Point2D.Double spaceShip, double direction, int spaceShipStepsLeft, boolean isAcc, List<Point2D.Double> bullets,
                               List<Integer> ownBulletsStepsLeft, String userName) {
        this.spaceShip = spaceShip;
        this.direction = direction;
        this.isAcc = isAcc;
        this.bullets = bullets;
        this.ownBulletsStepsLeft = ownBulletsStepsLeft;
        this.spaceShipStepsLeft = spaceShipStepsLeft;
        this.userName = userName;
    }

    /**
     * updates the main player of the game information
     * @param game the game to extract the main spaceship and other info from
     * */
    public void updateMainPlayer(Game game) {
        Spaceship mainSpaceShip = game.getMainSpaceShip();
        List<Point2D.Double> bullets = new ArrayList<>();
        game.getOwnBullets().forEach(bullet -> bullets.add(bullet.getLocation()));
       this.spaceShip = mainSpaceShip.getLocation();
       this.direction = mainSpaceShip.getDirection();
       this.isAcc = mainSpaceShip.isAccelerating();
       this.bullets = bullets;
       this.userName = mainSpaceShip.getUserName();
    }
}