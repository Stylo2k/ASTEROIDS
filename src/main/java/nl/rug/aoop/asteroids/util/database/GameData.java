package nl.rug.aoop.asteroids.util.database;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * GameData table used for the objectDB
 */
@Getter
@Setter
@Entity
public class GameData {
    @Id
    private int id;
    private String name;
    private int score;

    /**
     * create a new record for the GameData table
     * @param name name of the player
     * @param score score of the player
     */
    public GameData(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * Empty constructor for the class
     */
    public GameData() { }
}
