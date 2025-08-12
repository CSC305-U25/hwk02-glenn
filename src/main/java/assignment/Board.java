package assignment;

import java.util.ArrayList;
import java.util.List;
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
    private List<Square> squares = new ArrayList<>();
    private int selectedIndex = -1;
    private Consumer<Square> onSelectionChange;

    private static final int GAP = 18;
    private static final int PAD = 6;
    private static final Color PLACEHOLDER = new Color(230, 230, 230);

    public Board() {
        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(960, 640));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getPoint());
            }
        });
    }

    public void onSelectionChange(Consumer<Square> listener) {
        this.onSelectionChange = listener;
    }

    public void setSquare(ArrayList<Square> list) {
        this.squares = (list == null) ? new ArrayList<>() : list;
        this.selectedIndex = -1;
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (squares == null || squares.isEmpty()) {
            return;
        }

        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Layout L = computeLayout(getSize(), getInsets(), squares.size());
        int cells = L.rows * L.cols;

        for (int i = 0; i < cells; i++) {
            Rectangle cell = cellInnerRect(L, i);
            Color fill = (i < squares.size() && squares.get(i).getAwtColor() != null)
                    ? squares.get(i).getAwtColor()
                    : PLACEHOLDER;
             g.setColor(fill);
            g.fillRect(cell.x, cell.y, cell.width, cell.height);

            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1f));
            g.drawRect(cell.x, cell.y, Math.max(1, cell.width - 1), Math.max(1, cell.height -1));

            if(i == selectedIndex) {
                g.setColor(new Color(0, 0, 0, 160));
                g.setStroke(new BasicStroke(3f));
                g.drawRect(cell.x + 2, cell.y + 2,
                        Math.max(1, cell.width - 4), Math.max(1, cell.height -4));
                g.setStroke(new BasicStroke(1f));
            }
        }
        g.dispose();
    }

    private void handleClick(Point p) {
        if (squares == null || squares.isEmpty()){
            return;
        }

        Layout L = computeLayout(getSize(), getInsets(), squares.size());
        int index = pointToIndex(L, p);
        if(index < 0 || index >= squares.size()){
            return;
        }

        selectedIndex = index;
        if (onSelectionChange != null) {
            onSelectionChange.accept(squares.get(index));
        }
        repaint();
    }

    private static final class Layout {
        final int cx, cy, cw, ch;
        final int rows, cols;
        final int cell;
        final int ox, oy;

        Layout(int cx, int cy, int cw, int ch, int rows, int cols, int cell, int ox, int oy) {
            this.cx = cx;
            this.cy = cy;
            this.cw = cw;
            this.ch = ch;
            this.rows = rows;
            this.cols = cols;
            this.cell = cell;
            this.ox = ox;
            this.oy = oy;
        }
    }

    private static Layout computeLayout(Dimension size, Insets ins, int itemCount) {
        int cx = ins.left, cy = ins.top;
        int cw = size.width  - ins.left - ins.right;
        int ch = size.height - ins.top  - ins.bottom;

        int n = Math.max(1, itemCount);
        int rows = (int) Math.ceil(Math.sqrt(n));
        int cols = (int) Math.ceil(n / (double) rows);

        int availW = cw - (cols + 1) * GAP;
        int availH = ch - (rows + 1) * GAP;
        int cell = Math.max(10, Math.min(availW / cols, availH / rows));

        int gridW = cols * cell + (cols + 1) * GAP;
        int gridH = rows * cell + (rows + 1) * GAP;
        int ox = cx + (cw - gridW) / 2;
        int oy = cy + (ch - gridH) / 2;

        return new Layout(cx, cy, cw, ch, rows, cols, cell, ox, oy);
    }

    private static Rectangle cellOuterRect(Layout L, int index) {
        int r = index / L.cols, c = index % L.cols;
        int x = L.oy;
        x = L.ox + GAP + c * (L.cell + GAP);
        int y = L.oy + GAP + r * (L.cell + GAP);
        return new Rectangle(x, y, L.cell, L.cell);
    }

    private static Rectangle cellInnerRect(Layout L, int index) {
        Rectangle o = cellOuterRect(L, index);
        return new Rectangle(o.x + PAD, o.y + PAD,
                Math.max(1, o.width - 2 * PAD), Math.max(1, o.height - 2 * PAD));
    }

    private static int pointToIndex(Layout L, Point p) {
        int gridW = L.cols * L.cell + (L.cols + 1) * GAP;
        int gridH = L.rows * L.cell + (L.rows + 1) * GAP;
        if (p.x < L.ox || p.y < L.oy || p.x > L.ox + gridW || p.y > L.oy + gridH) return -1;

        int relX = p.x - L.ox - GAP;
        int relY = p.y - L.oy - GAP;
        int step = L.cell + GAP;

        int col = relX / step, row = relY / step;
        if (col < 0 || col >= L.cols || row < 0 || row >= L.rows) return -1;

        int idx = row * L.cols + col;
        Rectangle inner = cellInnerRect(L, idx);
        return inner.contains(p) ? idx : -1;
    }
}
