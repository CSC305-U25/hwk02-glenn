package assignment;

import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

/**
 * Represents a visual grid board that displays files as colored squares in a
 * GUI.
 * Calculates optimal grid layout and supports drawing each square.
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

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

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
            int c = (int) Math.ceil((double) fileCount / r);
            if (r * c >= fileCount) {
                gridRow = r;
                gridCol = c;
            }
        }
        this.cols = gridCol;
        this.rows = gridRow;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sq == null || sq.isEmpty() || cols == 0 || rows == 0) {
            return;
        }

        int gap = 10;
        int totalGapX = gap * (cols + 1);
        int totalGapY = gap * (rows + 1);
        int leftoverWidth = getWidth() - totalGapX;
        int leftoverHeight = getHeight() - totalGapY;
        int width = leftoverWidth / cols;
        int height = leftoverHeight / rows;

        int index = 0;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                if (index >= sq.size()) {
                    return;
                }
                Square s = sq.get(index);
                int drawX = gap + y * (width + gap);
                int drawY = gap + x * (height + gap);
                s.draw(g, drawX, drawY, width, height);
                index++;
            }
        }
    }
}
