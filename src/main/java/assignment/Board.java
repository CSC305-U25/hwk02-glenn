package assignment;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.awt.*;
import java.awt.event.*;
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
 * @version 2.0
 */

public class Board extends JPanel {
    private final ArrayList<Square> squares = new ArrayList<>();
    private final int gap = 6;
    private int selectedIndex = -1;
    private Consumer<Square> selectionListener = s -> {};

    public Board() {
        setBackground(Color.WHITE);
        addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseMoved(MouseEvent e) {
                int i = indexAt(e.getX(), e.getY());
                selectionListener.accept(getSquare(i));
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedIndex = indexAt(e.getX(), e.getY());
                selectionListener.accept(getSquare(selectedIndex));
                repaint();
            }
        });
    }

    public void onSelectionChange(Consumer<Square> listener) {
        this.selectionListener = (listener == null) ? s -> {} : listener;
    }
    public void setSquare(ArrayList<Square> list) {
        squares.clear();
        if (list != null) squares.addAll(list);
        selectedIndex = -1;
        repaint();
    }

    private Square getSquare(int idx) {
        return (idx >= 0 && idx < squares.size()) ? squares.get(idx) : null;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(960, 600);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if(squares.isEmpty()) return;

        int n = Math.max(1, squares.size());
        int rows = (int) Math.ceil(Math.sqrt(n));
        int cols = (int) Math.ceil(n / (double) rows);

        int need = rows * cols - squares.size();
        for(int i = 0; i < need; i ++) {
            squares.add(Square.placeholder());
        }

        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cellW = (getWidth() - (cols + 1) * gap) / cols;
        int cellH = (getHeight() - (rows + 1) * gap) / cols;
        cellW = Math.max(cellW, 10);
        cellH = Math.max(cellH, 10);

        for (int i = 0; i < rows * cols; i++) {
            int r = i / cols, c = i % cols;
            int x = gap + c * (cellW + gap);
            int y = gap + r * (cellH + gap);

            Square sq = squares.get(i);
            g.setColor(sq.getAwtColor());
            g.fillRect(x, y, cellW, cellH);

            g.setColor(Color.BLACK);
            g.drawRect(x, y, cellW, cellH);

            if (i == selectedIndex) {
                g.setStroke(new BasicStroke(3f));
                g.setColor(new Color(0, 0, 0, 160));
                g.drawRect(x + 1, y + 1, cellW - 2, cellH - 2);
                g.setStroke(new BasicStroke(1f));
            }
        }
        g.dispose();
    }

    private int indexAt(int x, int y) {
        int n = Math.max(1, squares.size());
        int rows = (int) Math.ceil(Math.sqrt(n));
        int cols = (int) Math.ceil(n / (double) rows);
        int cellW = (getWidth() - (cols + 1) * gap) / cols;
        int cellH = (getHeight() - (rows + 1) * gap) / rows;
        int c = (x - gap) / (cellW + gap);
        int r = (y - gap) / (cellH + gap);
        if (r < 0 || c < 0 || r >= rows || c >= cols) {
            return -1;
        }
        int i = r * cols + c;
        return (i >= 0 && i < squares.size()) ? i : -1;
    }


}
