package nl.rug.aoop.asteroids.view.mainmenu;

import nl.rug.aoop.asteroids.util.database.GameData;
import nl.rug.aoop.asteroids.model.HighScoreTableModel;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;
/**
 * represents the high scores panel to show to the user
 * */
public class HighScorePanel extends MainMenuPanel {
    /**
     * makes an instance of this high score panel
     * @param frame the frame to put the panel at
     * @param gameDataList the data needed for the table
     * */
    public HighScorePanel(AsteroidsFrame frame, List<GameData> gameDataList) {
        super(frame);
        super.buttonsMade = true;
        initSwingUi(gameDataList);
    }

    /**
     * initiates the swing ui componenets
     * @param gameDataList the list of data needed to make the table
     * */
    private void initSwingUi(List<GameData> gameDataList) {
        componentsPanel = new JPanel();
        componentsPanel.setPreferredSize(new Dimension(AsteroidsFrame.WIDTH/2, 280));
        componentsPanel.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.5f));

        JTable highScoreTable = new HighScoreTable(new HighScoreTableModel(gameDataList));
        JScrollPane tableScrollPane = new JScrollPane(highScoreTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        tableScrollPane.setPreferredSize(new Dimension(AsteroidsFrame.WIDTH/2, 200));
        tableScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        componentsPanel.add(tableScrollPane);
        super.addReturnButton();
    }
}