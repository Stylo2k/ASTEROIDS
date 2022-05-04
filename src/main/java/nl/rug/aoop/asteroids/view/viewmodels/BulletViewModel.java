package nl.rug.aoop.asteroids.view.viewmodels;

import lombok.Setter;
import nl.rug.aoop.asteroids.model.gameobjects.Bullet;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * View model for displaying bullet objects.
 */
public class BulletViewModel extends GameObjectViewModel<Bullet> {

    public static final String ENEMY = "enemy";
    public static final String ALLY = "ally";
    @Setter
    private String type;

    /**
     * Constructs the view model.
     *
     * @param gameObject The bullet to be displayed.
     * @param type the type of the owner of the bullet
     */
    public BulletViewModel(Bullet gameObject, String type) {
        super(gameObject);
        this.type = type;
    }

    /**
     * Draws the bullet that was given to this view model.
     *
     * @param graphics2D The graphics object which provides the necessary drawing methods.
     * @param location   The location at which to draw the object.
     */
    @Override
    public void draw(Graphics2D graphics2D, Point.Double location) {
        Ellipse2D.Double bulletEllipse = new Ellipse2D.Double(
                location.getX() - Bullet.BULLET_RADIUS / 2.0,
                location.getY() - Bullet.BULLET_RADIUS / 2.0,
                Bullet.BULLET_RADIUS,
                Bullet.BULLET_RADIUS
        );
        if (type.equals(ENEMY)) {
            graphics2D.setColor(Color.RED);
            graphics2D.fill(bulletEllipse);
            graphics2D.setColor(Color.RED);
        } else {
            graphics2D.setColor(Color.ORANGE);
            graphics2D.fill(bulletEllipse);
            graphics2D.setColor(Color.BLUE);
        }
        graphics2D.draw(bulletEllipse);
    }
}
