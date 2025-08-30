package assignment;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visual grid board for displaying files as colored squares in a GUI.
 * Calculates optimal grid layout based on the number of files and supports
 * dynamic updates when the file model changes.
 * Each square represents a file and is drawn with a color based on its line
 * count and type.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Board extends JPanel implements PropertyChangeListener {

    private final Blackboard bb;
    private ArrayList<Square> sq = new ArrayList<>();
    private int cols, rows;
    private final Logger logger = LoggerFactory.getLogger(Board.class);

    public Board(Blackboard blackboard) {
        this.bb = blackboard;
        setOpaque(true);
        logger.debug("Board Squares set: {}, grid {}x{}", sq.size(), cols, rows);
        sq = new ArrayList<>();
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public void setSquare(List<Square> list) {
        this.sq = new ArrayList<>(list);
        calculateSize();
        repaint();
    }

    public void calculateSize() {
        int fileCount = (sq == null) ? 0 : sq.size();
        if (fileCount == 0) {
            cols = rows = 0;
            return;
        }
        int gridCol = 1, gridRow = fileCount;
        for (int r = 1; r <= Math.sqrt(fileCount); r++) {
            int c = (int) Math.ceil((double) fileCount / r);
            if (r * c >= fileCount) {
                gridRow = r;
                gridCol = c;
            }
        }
        logger.trace("calculated grid {}x{} for {} files", cols, rows, fileCount);
        cols = gridCol;
        rows = gridRow;
    }

    private void refreshFromModel() {
        List<Square> list = new ArrayList<>();
        for (FileInfo f : bb.getFiles()) {
            list.add(new Square(f.name + ".java", f.lines, JavaFilter.INSTANCE.test(f)));
        }
        setSquare(list);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sq == null || sq.isEmpty() || cols == 0 || rows == 0) {
            return;
        }

        int w = getWidth() / cols, h = getHeight() / rows;

        int index = 0;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                int drawX = y * w;
                int drawY = x * h;
                if (index < sq.size()) {
                    sq.get(index).draw(g, drawX, drawY, w, h);
                } else {
                    Square.drawEmpty(g, drawX, drawY, w, h);
                }
                index++;
            }
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        bb.addPropertyChangeListener(this);
        refreshFromModel();
    }

    @Override
    public void removeNotify() {
        bb.removePropertyChangeListener(this);
        super.removeNotify();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String p = evt.getPropertyName();
        if ("files".equals(p) || "model".equals(p)) {
            SwingUtilities.invokeLater(this::refreshFromModel);
        }
    }
}
