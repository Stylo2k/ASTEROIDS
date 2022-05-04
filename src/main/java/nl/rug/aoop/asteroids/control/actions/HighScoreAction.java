package nl.rug.aoop.asteroids.control.actions;

import nl.rug.aoop.asteroids.model.PanelType;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An action that represents when a user indicates that they wish to see high score
 */
public class HighScoreAction extends AbstractAction {
    /**
     * frame to manipulate
     * */
    private final AsteroidsFrame frame;

    /**
     * Construct a new high score action. This calls the parent constructor to give the action a name.
     * this shows high score panel
     * */
    public HighScoreAction(AsteroidsFrame frame) {
        super(PanelType.HIGH_SCORES.getText());
        this.frame = frame;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param event The event to be processed.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        frame.changePanel(PanelType.HIGH_SCORES);
    }
}
