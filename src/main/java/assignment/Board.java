package assignment;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import javax.swing.*;


/**
 * Visual grid board for displaying files as colored squares in a GUI.
 * Calculates optimal grid layout based on the number of files and supports
 * interactive highlighting of squares on mouse hover.
 * Each square represents a file and is drawn with a color based on its line
 * count.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Board extends JPanel{

    private ArrayList<Square> sq = new ArrayList<>();
    private int cols, rows;

    public Board() {
        setOpaque(true);
        sq = new ArrayList<>();
    }

    public int getCols() { return cols; }

    public int getRows() { return rows; }

    public void setSquare(List<Square> list) {
        this.sq = new ArrayList<>(list);
        calculateSize();
        repaint();
    }

    public void calculateSize() {
        int fileCount = (sq == null) ? 0 : sq.size();
        if (fileCount == 0) { cols = rows = 0; return; }
        int gridCol = 1, gridRow = fileCount;
        for (int r = 1; r <= Math.sqrt(fileCount); r++) {
            int c = (int) Math.ceil((double) fileCount / r);
            if (r * c >= fileCount) { gridRow = r; gridCol = c; }
        }
        cols = gridCol; rows = gridRow;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ArrayList<Rectangle> cellBound = new ArrayList<>();
        cellBound.clear();

        calculateSize();

        if (sq == null || sq.isEmpty() || cols == 0 || rows == 0) {
            return;
        }

        int w = getWidth() / cols;
        int h = getHeight() / rows;

        int index = 0;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                int drawX = y * w;
                int drawY = x * h;
                cellBound.add(new Rectangle(drawX, drawY, w, h));

                if (index < sq.size()) {
                    sq.get(index).draw(g, drawX, drawY, w, h);
                } else {
                    Square.drawEmpty(g, drawX, drawY, w, h);
                }
                index++;
            }
        }
    }
}
