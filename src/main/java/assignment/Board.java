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
 * @version 3.0
 */

public class Board extends JPanel {
    private final ArrayList<Square> squares = new ArrayList<>();
    private final int gap = 6;
    private int selectedIndex = -1;
    private Consumer<Square> selectionListener = s -> {
    };

    public Board() {
        setBackground(Color.WHITE);
        addMouseMotionListener(new MouseMotionAdapter() {
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
        this.selectionListener = (listener == null) ? s -> {
        } : listener;
    }

    public void setSquare(ArrayList<Square> list) {
        squares.clear();
        if (list != null)
            squares.addAll(list);
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
        if (squares == null || squares.isEmpty()) {
            return;
        }

        Insets ins = getInsets();
        int cx = ins.left;
        int cy = ins.top;
        int cw = getWidth() - ins.left - ins.right;
        int ch = getHeight() - ins.top - ins.bottom;

        int n = Math.max(1, squares.size());
        int rows = (int) Math.ceil(Math.sqrt(n));
        int cols = (int) Math.ceil(n / (double) rows);

        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(cx, cy, cw, ch);

        int pad = 6;

        int availW = cw - (cols + 1) * gap;
        int availH = ch - (rows + 1) * gap;
        int cellSide = Math.max(10, Math.min(availW / cols, availH / rows));

        int gridW = cols * cellSide + (cols + 1) * gap;
        int gridH = rows * cellSide + (rows + 1) * gap;
        int ox = cx + (cw - gridW) / 2;
        int oy = cy + (ch - gridH) / 2;

        int cellsToDraw = rows * cols;

        for (int i = 0; i < cellsToDraw; i++) {
            int r = i / cols, c = i % cols;
            int x = ox + gap + c * (cellSide + gap);
            int y = oy + gap + r * (cellSide + gap);

            int ix = x + pad;
            int iy = y + pad;
            int iw = Math.max(1, cellSide -2 * pad);
            int ih = iw;

            Color fill;
            if (i < squares.size()) {
                Square sq = squares.get(i);
                fill = (sq.getAwtColor() != null) ? sq.getAwtColor() : new Color(60, 60, 60);
            } else {
                fill = new Color (230, 230, 230);
            }
            g.setColor(fill);
            g.fillRect(ix, iy, iw, ih);

            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1f));
            g.drawRect(x, y, Math.max(1, iw - 1), Math.max(1, ih - 1));

            if (i == selectedIndex) {
                g.setStroke(new BasicStroke(3f));
                g.setColor(new Color(0, 0, 0, 160));
                g.drawRect(x + 1, y + 1, Math.max(1, iw - 3), Math.max(1, ih - 3));
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
