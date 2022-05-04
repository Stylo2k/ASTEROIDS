package nl.rug.aoop.asteroids.control.actions;

import nl.rug.aoop.asteroids.model.PanelType;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An action that represents when a user indicates that they wish to quit to main menu.
 */
public class QuitToMainMenuAction extends AbstractAction {
    /**
     * frame to manipulate
     * */
    private final AsteroidsFrame frame;
    /**
     * Construct a new quit action. This calls the parent constructor to give the action a name.
     */
    public QuitToMainMenuAction(AsteroidsFrame frame) {
        super("Quit To Main Menu");
        this.frame = frame;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param event The event to be processed.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        frame.changePanel(PanelType.START);
    }
}
