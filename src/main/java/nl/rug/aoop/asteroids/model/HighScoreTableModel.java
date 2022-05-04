package nl.rug.aoop.asteroids.model;

import nl.rug.aoop.asteroids.util.database.GameData;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class HighScoreTableModel extends AbstractTableModel {

    /**
     * Column names
     */
    public final String COL_USERNAME = "Name";
    public final String COL_SCORE = "Score";
    /**
     * Column size
     */
    public final int NUM_COLUMNS = 2;

    /**
     * List of data to be used in table
     */
    private final List<GameData> gameDataList;

    /**
     * Creates a new table model
     * @param gameDataList the data to be displayed in the table
     */
    public HighScoreTableModel(List<GameData> gameDataList) {
        this.gameDataList = gameDataList;
    }

    @Override
    public int getRowCount() {
        return gameDataList.size();
    }

    @Override
    public int getColumnCount() {
        return NUM_COLUMNS;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        GameData gameData = gameDataList.get(rowIndex);
        if (gameData == null) return null;
        return switch (columnIndex) {
            case 0 -> gameData.getName();
            case 1 -> gameData.getScore();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> COL_USERNAME;
            case 1 -> COL_SCORE;
            default -> null;
        };
    }
}