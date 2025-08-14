package assignment;

import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

/**
 * Represents a visual grid board that displays files as colored squares in a
 * GUI.
 * This class extends JPanel and provides functionality to create an optimal
 * grid layout
 * for displaying files, allowing users to interact with squares by changing
 * their colors.
 * The board automatically calculates the best grid dimensions to fit all files
 * efficiently.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 3.0
 */

public class Board extends JPanel {
    private ArrayList<Square> sq;
    private int cols, rows;

    public Board() {
        setOpaque(true);
        setBackground(Color.WHITE);
        sq = new ArrayList<>();
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }

    public void setSquare(ArrayList<Square> list) {
        this.sq = (list == null) ? new ArrayList<>() : list;
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
            int c = (int)Math.ceil(fileCount / (double)r);
            if (r * c >= fileCount ) {
                gridRow = r; gridCol = c;
            }
        }

        cols = gridCol;
        rows = gridRow;
        System.out.printf("Grid Size: %dx%d %n", cols, rows, fileCount);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sq == null || sq.isEmpty() || cols == 0 || rows == 0) {
            return;
        }
        calculateSize();
        if (cols == 0 || rows == 0) {
            return;
        }

        int w = getWidth(), h = getHeight();
        int cell = Math.max(1, Math.min(w / cols, h / rows));

        int totalW = cols * cell;
        int totalH = rows * cell;
        int startX = (w - totalW) / 2;
        int startY = (h - totalH) / 2;

        int index = 0;
        for (int r = 0; r < rows; r++) {
           for (int c = 0; c < cols; c++) {
                int x = startX + c * (cell);
                int y = startY + r * (cell);

                if (index < sq.size()) {
                    sq.get(index++).draw(g, x, y, cell, cell);
                } else {
                    Square.drawEmpty(g, x, y, cell, cell);
                }
           }
        }
    }
}
