package nl.rug.aoop.asteroids.view.mainmenu;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.server.Server;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;

import javax.swing.*;
import java.awt.*;

/**
 * the panel that shows the port for the client to get connected to
 * */
@Log
public class HostGamePanel extends MainMenuPanel {

    /**
     * instance of server to extract port from
     * */
    private final Server server;

    /**
     * makes the host panel with the made port
     * @param server the server to extract port from
     * */
    public HostGamePanel(Server server) {
        super(server.getFrame());
        super.buttonsMade = true;
        this.server = server;
        initHostSwingUi();
    }

    /**
     * makes components and adds them to {@link #componentsPanel}
     * */
    private void initHostSwingUi() {
        componentsPanel = new JPanel();
        componentsPanel.setPreferredSize(new Dimension(AsteroidsFrame.WIDTH/2, 260));
        componentsPanel.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.5f));

        JLabel label = new JLabel("Hosting A Game Successful.");
        label.setFont(new Font("hyperspace", Font.BOLD, 40));

        JLabel portLabel = new JLabel("Port Number: " + server.getPort());
        portLabel.setFont(new Font("hyperspace", Font.BOLD, 40));

        label.setHorizontalAlignment(JLabel.CENTER);
        portLabel.setHorizontalAlignment(JLabel.CENTER);

        componentsPanel.add(label);
        componentsPanel.add(portLabel);
        super.addReturnButton();
    }
}
