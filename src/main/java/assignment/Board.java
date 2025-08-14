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
        setPreferredSize(new Dimension(960, 640));
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
            int c = (int)Math.ceil((double) fileCount / r);
            if (r * c >= fileCount ) {
                gridRow = r; gridCol = c;
            }
        }

        this.cols = gridCol;
        this.rows = gridRow;
        System.out.printf("Grid Size: %dx%d %n", cols, rows, fileCount);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sq == null || sq.isEmpty() || cols == 0 || rows == 0) {
            return;
        }

        int width = 50;
        int height = 50;
        //int gap = 10;

        int index = 0;
        for (int x = 0; x < rows; x++) {
           for (int y = 0; y < cols; y++) {
                if (index >= sq.size()) {
                    return;
                }
                Square s = sq.get(index);
                s.draw(g, x, y, width, height);
                index++;
           }
        }
    }
}
