package nl.rug.aoop.asteroids.control.actions;

import nl.rug.aoop.asteroids.model.PanelType;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * action for the button to return to main menu
 * */
public record ReturnToMainMenuAction(JButton button,
                                     AsteroidsFrame frame) implements ActionListener {
    /**
     * when button is pressed return to start
     * */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            frame.changePanel(PanelType.START);
        }
    }
}
