package nl.rug.aoop.asteroids.view.errors;


import javax.swing.*;

/**
 * handles all error's dialog for the frame
 * */
public abstract class ErrorDialog {
    /**
     * message to be shown in the error dialog
     * */
    private static final String INCORRECT_FORMAT = "Incorrect Format.\n" +
            "Only numbers are allowed and less 6 digits";

    private static final String NO_PORT_NUMBER_PROVIDED = "No port number provided";

    private static final String INCORRECT_PORT_NUMBER = "Incorrect port number";

    private static final String GAME_OVER = "You died";

    private static final String INCORRECT_NAME = ("""
           Invalid username:
             Has to be between 4-20 chars\s
             Has to start with an alphabet\s
             Cannot contain special characters""");

    private static final String ERROR = "Error";

    public static final String QUIT_TO_MAIN_MENU = "Quit To Main Menu";

    private static final String KICKED_FROM_SERVER = "You have been kicked from the server or the host left the server";

    public static final String HOST_A_NEW_GAME = "Host A New Game";

    private static final String NO_NAME = "Please enter a name first.";

    public static final String NO_PLAYERS_LEFT = "No players left. All players are dead";


    /**
     * shows dialog when player dies in a solo game
     *
     * */
    public static void soloGameOver() {
        Object[] options = {QUIT_TO_MAIN_MENU};
        JOptionPane.showOptionDialog(new JFrame(),
                GAME_OVER,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    /**
     * shows dialog when player dies
     * @return the choice of the user
     * */
    public static String noPlayersLeft() {
        Object[] options = {HOST_A_NEW_GAME, QUIT_TO_MAIN_MENU};
        int n = JOptionPane.showOptionDialog(new JFrame(),
                NO_PLAYERS_LEFT,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        return (String) options[n];
    }

    /**
     * shows dialog when player dies
     * @return the choice of the user
     * */
    public static String gameOver() {
        Object[] options = {"Spectate", QUIT_TO_MAIN_MENU};
        int n = JOptionPane.showOptionDialog(new JFrame(),
                GAME_OVER,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        return (String) options[n];
    }


    /**
     * shows dialog when user enters an incorrect
     * format (non int) port. (client fails to connect)
     * */
    public static void incorrectFormatForPort() {
        JOptionPane.showMessageDialog(new JFrame(), INCORRECT_FORMAT, ERROR,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * shows dialog when user enters an incorrect
     * port. (client fails to connect)
     * */
    public static void incorrectPortNumber() {
        JOptionPane.showMessageDialog(new JFrame(), INCORRECT_PORT_NUMBER, ERROR,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * shows dialog when user tries to join with an empty
     * field (port)
     * */
    public static void noPortNumberProvided() {
        JOptionPane.showMessageDialog(new JFrame(), NO_PORT_NUMBER_PROVIDED, ERROR,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * shows dialog when user enters an incorrect name
     * */
    public static void noName() {
        JOptionPane.showMessageDialog(new JFrame(), NO_NAME, ERROR,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * shows dialog when user enters an incorrect name
     * */
    public static void incorrectName() {
        JOptionPane.showMessageDialog(new JFrame(), INCORRECT_NAME, ERROR,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * shows dialog when host quits
     * */
    public static void kickedFromServer() {
        JOptionPane.showMessageDialog(new JFrame(), KICKED_FROM_SERVER, ERROR,
                JOptionPane.ERROR_MESSAGE);
    }
}

