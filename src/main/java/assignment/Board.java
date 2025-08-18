package assignment;

import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Visual grid board for displaying files as colored squares in a GUI.
 * Calculates optimal grid layout based on the number of files and supports
 * interactive highlighting of squares on mouse hover.
 * Each square represents a file and is drawn with a color based on its line
 * count.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 4.0
 */
public class Board extends JPanel {

    private ArrayList<Square> sq = new ArrayList<>();
    private ArrayList<Rectangle> cellBound = new ArrayList<>();
    private int cols, rows;

    private Consumer<Square> hoverListener = s -> {};
    private int hoveredIndex = -1;

    public Board() {
        //setBackground(Color.WHITE);
        setOpaque(true);
        sq = new ArrayList<>();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int idx = Board.this.findCellIndex(e.getPoint());
                if (idx != hoveredIndex) {
                    hoveredIndex = idx;
                    hoverListener.accept(idx >= 0 && idx < sq.size()
                        ? sq.get(idx) : null);
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoveredIndex != -1) {
                    hoveredIndex = -1;
                    hoverListener.accept(null);
                    repaint();
                }
            }
        });
    }

    public Board(Blackboard bb) {
        this();
        if (bb != null) {
            bb.addObserver(b -> {
                ArrayList<Square> list = new ArrayList<>();
                for (Blackboard.FileInfo f : b.getFiles()) {
                    list.add(new Square(f.name, f.lines));
                }
                setSquare(list);
                b.getSelectedFiles().ifPresentOrElse(
                    sel -> hoverListener.accept(findByName(sel)),
                    () -> hoverListener.accept(null)
                );
            });
        }
    }

    public void setOnHoverChange(Consumer<Square> n) {
        this.hoverListener = (n != null) ? n : (s -> {});
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

    private Square findByName(String simpleName) {
        for (Square s : sq) {
            if (s.getFileName().equals(simpleName)) {
                return s;
            }
        }
        return null;
    }

    private int findCellIndex(Point p) {
        for (int i = 0; i < cellBound.size(); i++) {
            if (cellBound.get(i).contains(p)) {
                return i;
            }
        }
        return -1;
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
        cols = gridCol;
        rows = gridRow;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
