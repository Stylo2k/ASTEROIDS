package nl.rug.aoop.asteroids.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

/**
 * loads the image of the asteroid and rotates it to a random angle
 * */
public abstract class AsteroidRotator {
    /**
     * the upperbound of the angle
     * */
    private static final int UPPERBOUND = 360;

    /**
     * path to the asteroids icon
     * */
    private static final Path ICON_ASTEROID_PNG = Path.of("data/graphics/icon_asteroid.png");

    /**
     * Loads the icon of the asteroid and rotates it to the given angle
     * @param rotationAngle the angle to rotate the icon to
     * @return an asteroid image rotated to an angle
     * */
    public static Image getRotatedAsteroid(double rotationAngle) throws IOException {
        BufferedImage image;
        image = ImageIO.read((ICON_ASTEROID_PNG).toFile());
        AffineTransform tr = AffineTransform.getRotateInstance(rotationAngle,
                (double) image.getWidth()/2,
                (double) image.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(tr, AffineTransformOp.TYPE_BILINEAR);
        image = op.filter(image, null);
        return image;
    }

    /**
     * @return random angle between 0 and 360
     * */
    public static double generateRandomAngle() {
        Random rand = new Random();
        int randomInt = rand.nextInt(UPPERBOUND);
        return Math.toRadians(Math.toDegrees(randomInt));
    }

}
