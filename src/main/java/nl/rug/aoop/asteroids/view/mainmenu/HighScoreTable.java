package nl.rug.aoop.asteroids.view.mainmenu;

import nl.rug.aoop.asteroids.model.HighScoreTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Collection;
import java.util.List;
/**
 * represents the table to be shown in the {@link HighScorePanel}
 * */
public class HighScoreTable extends JTable {

    /**
     * Creates a new table to display the game data
     * @param highScoreTableModel table model used for the table
     */
    public HighScoreTable(HighScoreTableModel highScoreTableModel) {
        super(highScoreTableModel);
        setColumnAlignment(DefaultTableCellRenderer.LEFT, List.of(highScoreTableModel.COL_USERNAME));
        setColumnAlignment(DefaultTableCellRenderer.CENTER, List.of(highScoreTableModel.COL_SCORE));
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableUI();
    }

    /**
     * Method that sets the alignment of a given column.
     *
     * @param alignment The alignment the columns should have.
     * @param columns   The names of the columns that should be aligned.
     */
    private void setColumnAlignment(int alignment, Collection<String> columns) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(alignment);
        columns.forEach(c -> this.getColumn(c).setCellRenderer(renderer));
    }

    /**
     * Sets the look and feel of the table
     */
    private void tableUI(){
        this.setBackground(Color.black);
        this.getTableHeader().setBackground(Color.black);
        this.setFont(new Font("hyperspace", Font.BOLD, 15));
        this.getTableHeader().setFont(new Font("hyperspace", Font.BOLD, 20));
    }
}