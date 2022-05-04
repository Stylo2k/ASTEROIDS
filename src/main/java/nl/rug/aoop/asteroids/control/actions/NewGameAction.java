package nl.rug.aoop.asteroids.control.actions;

import nl.rug.aoop.asteroids.model.PanelType;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * This action represents when a user wants to quit the current game and start a new one.
 */
public class NewGameAction extends AbstractAction {

    /**
     * the frame to manipulate
     * */
    private final AsteroidsFrame frame;

    /**
     * Constructs the action. Calls the parent constructor to set the name of this action.
     *
     */
    public NewGameAction(AsteroidsFrame frame) {
        super(PanelType.NEW_SOLO_GAME.getText());
        this.frame = frame;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param event The event to be processed. In this case, no information from the actual event is needed. Simply the
     *              knowledge that it occurred is enough.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        frame.changePanel(PanelType.NEW_SOLO_GAME);
    }
}
