package nl.rug.aoop.asteroids.model;

import lombok.Getter;

/**
 * represents a panel type with a certain text.
 * */
public enum PanelType {
    START("start"),
    NEW_SOLO_GAME ( "New Solo Game"),
    JOIN_GAME ( "Join Game"),
    JOINED_GAME ( "Joined Game"),
    HOSTED_GAME ( "Hosted Game"),
    HOST_GAME ( "Host Game"),
    HIGH_SCORES("High Score"),
    RETURN("Return To Main Menu"),
    QUIT ( "Quit");

   /**
    * the text for the type
    * */
    @Getter
    private final String text;

    /**
     * @param text the text that this enum should represent
     * */
    PanelType(String text) {
        this.text = text;
    }

    /**
     * gets the enum through its enumeration value
     * @param text enumeration value
     * @return the enumeration of the enumeration value
     * */
    public static PanelType fromString(String text) {
        for (PanelType b : PanelType.values()) {
            if (b.text.equals(text)) {
                return b;
            }
        }
        return null;
    }
}
