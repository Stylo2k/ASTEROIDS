package nl.rug.aoop.asteroids.control.actions;

import nl.rug.aoop.asteroids.model.PanelType;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An action that represents when a user indicates that they wish to host a game
 */
public class HostGameAction extends AbstractAction {
    /**
     * frame to manipulate
     * */
    private final AsteroidsFrame frame;

    /**
     * Construct a new high score action. This calls the parent constructor to give the action a name.
     * this shows host game panel
     * */
    public HostGameAction(AsteroidsFrame frame) {
        super(PanelType.HOST_GAME.getText());
        this.frame = frame;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param event The event to be processed.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        frame.changePanel(PanelType.HOST_GAME);
    }
}
