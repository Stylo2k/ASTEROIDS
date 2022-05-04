package nl.rug.aoop.asteroids.view.mainmenu;

import lombok.Getter;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.client.Client;
import nl.rug.aoop.asteroids.model.PanelType;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import nl.rug.aoop.asteroids.view.errors.ErrorDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * panel that holds the field to get input from the client and
 * tries to connect them with the server
 * */
@Log
public class JoinGamePanel extends MainMenuPanel implements ActionListener {

    /**
     * the join button
     * */
    @Getter
    public final JButton button;

    /**
     * set of listeners to this class's join button {@link #button}
     * */
    @Getter
    private final Set<JoinListener> listeners;

    /**
     * field holding input from user
     * */
    private final JTextField field;

    /**
     * instance of client to connect through with server
     * */
    private final Client client;

    /**
     * panel asks user for port
     * @param client the client instance
     * @param frame the original frame
     * */
    public JoinGamePanel(Client client, AsteroidsFrame frame) {
        super(frame);
        super.buttonsMade = true;
        this.client = client;
        button = new JButton("Join");
        field = new JTextField();
        listeners = new HashSet<>();
        initSwingUi();
    }

    /**
     * initializes swing ui for the JoinGamePanel
     * <p> adds buttons to {@link #componentsPanel}</p>
     * */
    public void initSwingUi() {
        componentsPanel = new JPanel();
        componentsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        componentsPanel.setPreferredSize(new Dimension(AsteroidsFrame.WIDTH/3+20, 260));
        componentsPanel.setBackground(Color.BLACK);

        componentsPanel.add(field);
        componentsPanel.add(button);

        this.addReturnButton();

        field.setPreferredSize(new Dimension(AsteroidsFrame.WIDTH/3, 80));
        field.setFont(new Font("hyperspace", Font.BOLD, 40));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(Color.BLACK);
        field.setForeground(Color.WHITE);

        button.setPreferredSize(new Dimension(AsteroidsFrame.WIDTH/4, 80));
        button.addActionListener(this);
        button.setFont(new Font("hyperspace", Font.BOLD, 40));
        button.setHorizontalAlignment(JTextField.CENTER);
        button.setBackground(Color.BLACK);
        button.setOpaque(true);
    }

    /**
     * adds listener to list of listeners {@link #listeners}
     * */
    public void addListener(JoinListener listener) {
        listeners.add(listener);
    }

    /**
     * if button is pressed try to parse input from field and
     * try to connect with server
     * */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            if (field.getText().isEmpty()) {
                ErrorDialog.noPortNumberProvided();
                return;
            }
            int port;
            try {
                port = Integer.parseInt(field.getText());
                listeners.forEach(listener -> listener.attemptToJoin(port));
                if (client.isJoined()) {
                    frame.changePanel(PanelType.JOINED_GAME);
                    frame.getService().submit(client);
                } else {
                    frame.changePanel(PanelType.JOIN_GAME);
                    ErrorDialog.incorrectPortNumber();
                }
            } catch (NumberFormatException s) {
                frame.changePanel(PanelType.JOIN_GAME);
                ErrorDialog.incorrectFormatForPort();
            }
        }
    }
}
