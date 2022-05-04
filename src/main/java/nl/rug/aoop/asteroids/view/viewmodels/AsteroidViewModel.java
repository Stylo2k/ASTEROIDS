package nl.rug.aoop.asteroids.view.viewmodels;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.model.gameobjects.Asteroid;
import nl.rug.aoop.asteroids.util.AsteroidRotator;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

/**
 * View model for displaying an asteroid object.
 */
@Log
public class AsteroidViewModel extends GameObjectViewModel<Asteroid> {

    /**
     * the new asteroids image / shape
     * */
    private Image image;

    /**
     * Constructs a new view model with the given game object.
     *
     * @param gameObject The object that will be displayed when this view model is drawn.
     */
    public AsteroidViewModel(Asteroid gameObject) {
        super(gameObject);
        int radius = (int) getGameObject().getRadius();
        try {
            image = AsteroidRotator.getRotatedAsteroid(gameObject.getDirectionAngle());
            image = image.getScaledInstance(3 * radius, 3 * radius, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            log.warning("Unable to locate the new asteroid png. Please check data/asteroid_icon.png \n" +
                    "Asteroids will now be basic circles");
        }
    }

    /**
     * Draws the game object that was given to this view model.
     *
     * @param graphics2D The graphics object which provides the necessary drawing methods.
     * @param location   The location at which to draw the object.
     */
    @Override
    public void draw(Graphics2D graphics2D, Point.Double location) {
        double radius = getGameObject().getRadius();
        if (image == null) {
            graphics2D.setColor(Color.GRAY);
            Ellipse2D.Double asteroidEllipse = new Ellipse2D.Double(
                    location.getX() - radius,
                    location.getY() - radius,
                    2 * radius,
                    2 * radius
            );
            graphics2D.fill(asteroidEllipse);
        } else {
            graphics2D.drawImage(image,
                    (int) (location.getX() - radius),
                    (int) (location.getY() - radius),
                    null
            );
        }
    }
}
