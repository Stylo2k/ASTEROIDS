package nl.rug.aoop.asteroids.util.database;

import lombok.Getter;

/**
 * Queries performed on the database
 */
public enum Query {
    ADD_NEW_PLAYER("SELECT max(gd) FROM GameData gd"),
    UPDATE_PLAYER("SELECT gd FROM GameData gd WHERE gd.name = '"),
    GET_ALL_PLAYERS("SELECT gd FROM GameData gd ORDER BY gd.score DESC");

    /**
     * the text for the type
     */
    @Getter
    private final String text;

    /**
     * @param text the text that this enum should represent
     */
    Query(String text) {
        this.text = text;
    }
}