package nl.rug.aoop.asteroids.view.mainmenu;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.control.actions.ReturnToMainMenuAction;
import nl.rug.aoop.asteroids.model.PanelType;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import nl.rug.aoop.asteroids.view.errors.ErrorDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * panel holding buttons that listen to user actions.
 * Depending on the button pressed certain actions/changes are
 * done to the frame
 * */
@Log
public class MainMenuPanel extends JPanel implements ActionListener {

    /**
     * some properties for the buttons to get their size right
     * */
    private static final int BUTTON_HEIGHT = 55;
    private static final int BUTTON_WIDTH = AsteroidsFrame.WIDTH / 4;

    /**
     * some spacing to center the buttons on the panel
     * */
    private static final int BUTTONS_WIDTH = BUTTON_WIDTH + 20;
    private static final int NR_BUTTONS = 6;
    private static final int BUTTONS_HEIGHT = BUTTON_HEIGHT * NR_BUTTONS + 100;

    /**
     * the original frame
     * */
    public final AsteroidsFrame frame;

    /**
     * field to get input data from the user
     * */
    @Getter
    public JTextField field;

    /**
     * the list of buttons of the panel
     * */
    public List<JButton> buttonList;

    /**
     * represents if the buttons are already made or not
     * */
    public boolean buttonsMade;

    /**
     * panel holding the swing ui
     * */
    @Getter
    public JPanel componentsPanel;

    /**
     * background picture
     * */
    public Image mainMenuPic;

    /**
     * represents if user is in the {@link MainMenuPanel}
     * to either play or not play background music
     * */
    @Getter @Setter
    public boolean inMainMenu;

    /**
     * return button to go back to {@link MainMenuPanel}
     * */
    private JButton returnButton;

    /**
     * makes an instance of this main menu panel
     * @param frame the frame to put this panel in
     * */
    public MainMenuPanel(AsteroidsFrame frame) {
        this.frame = frame;
        inMainMenu = true;
        setLayout(new FlowLayout(FlowLayout.CENTER,10 , AsteroidsFrame.HEIGHT/2-80));
        loadImage();
    }

    /**
     * initializes the buttons for the main menu
     * <p>
     *     adds the buttons to {@link #componentsPanel}
     * </p>
     * */
    private void initMainMenuButtons() {
        componentsPanel = new JPanel();
        componentsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        componentsPanel.setPreferredSize(new Dimension(BUTTONS_WIDTH, BUTTONS_HEIGHT));
        componentsPanel.setBackground(Color.BLACK);

        field = new JTextField();
        componentsPanel.add(field);
        field.setPreferredSize(new Dimension(AsteroidsFrame.WIDTH/3, 60));
        field.setFont(new Font("hyperspace", Font.BOLD, 25));
        field.setText("Enter Name");
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(Color.BLACK);
        field.setForeground(Color.WHITE);

        JButton newGameButton = new JButton(PanelType.NEW_SOLO_GAME.getText());
        JButton joinGameButton = new JButton(PanelType.JOIN_GAME.getText());
        JButton hostGame = new JButton(PanelType.HOST_GAME.getText());
        JButton quitButton = new JButton(PanelType.QUIT.getText());
        JButton highScores = new JButton(PanelType.HIGH_SCORES.getText());

        buttonList = new ArrayList<>();
        buttonList.add(newGameButton);
        buttonList.add(joinGameButton);
        buttonList.add(hostGame);
        buttonList.add(highScores);
        buttonList.add(quitButton);

        buttonList.forEach(button -> button.addActionListener(this));
        buttonList.forEach(button -> button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT)));
        buttonList.forEach(button -> button.setFont(new Font("hyperspace", Font.BOLD, 26)));
        buttonList.forEach(button -> {
            button.setBackground(Color.BLACK);
            button.setOpaque(true);
        });
        buttonList.forEach(componentsPanel::add);

        buttonsMade = true;
    }

    /**
     * loads the background image for the main menu
     * <p> {@link #mainMenuPic}</p>
     * */
    private void loadImage() {
        try {
            BufferedImage image = ImageIO.read(new File("data/graphics/mainMenuPic.jpg"));
            mainMenuPic = image.getScaledInstance(AsteroidsFrame.WIDTH, AsteroidsFrame.HEIGHT, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            log.warning("Could not load the image. Check : data/mainMenuPic.jpg. Why would u remove it" +
                    "-_-");
        }
    }

    /**
     * paints the {@link #mainMenuPic} and
     * adds the latest {@link #componentsPanel}
     * */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(mainMenuPic,0,0,null);
        if(!buttonsMade) initMainMenuButtons();
        if (componentsPanel!=null) {
            remove(componentsPanel);
        }
        add(componentsPanel);
    }

    /**
     * adds the return button {@link #returnButton} to all panels
     * extending {@link MainMenuPanel}
     * */
    public void addReturnButton() {
        if (returnButton != null) componentsPanel.remove(returnButton);
        returnButton = new JButton(PanelType.RETURN.getText());
        returnButton.addActionListener(new ReturnToMainMenuAction(returnButton, frame));
        returnButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        returnButton.setFont(new Font("hyperspace", Font.BOLD, 26));
        returnButton.setBackground(Color.BLACK);
        returnButton.setOpaque(true);
        componentsPanel.add(returnButton);
    }

    /**
     * @param name name user enters
     * @return bool value tells the user if name is valid
     */
    private boolean isValidName(String name) {
        Pattern p = Pattern.compile("^[A-Za-z]\\w{3,19}$");
        if (name == null) {
            return false;
        }
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * fired when a key is pressed.
     * We then take the button's text ( that got pressed), extract the {@link PanelType}
     * then pass that to the frame to change to it
     * */
    @Override
    public void actionPerformed(ActionEvent e) {
        // read text from button -> Make that a panel type
        String buttonsPressedText = ((JButton)e.getSource()).getText();
        PanelType panel = PanelType.fromString((buttonsPressedText));
        if (panel == null) return;

        if (panel.equals(PanelType.HIGH_SCORES) || panel.equals(PanelType.QUIT)){
            frame.changePanel(panel);
            return;
        }

        String userName = field.getText();
        if (buttonList.contains((JButton) e.getSource())) {
            if (field.getText().isEmpty() || field.getText().equals("Enter Name")) {
                ErrorDialog.noName();
            } else if (!isValidName(userName)) {
                field.setText("");
                ErrorDialog.incorrectName();
            } else {
                frame.changePanel(panel);
            }
        }
    }
}
