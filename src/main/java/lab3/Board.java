package lab3;

import lab3.Square;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    private int rows;
    private int cols;
    private ArrayList<Square> squares = new ArrayList<>();

    public Board() {
    }

    public void createBoard(String[] fileNames) {
        int fileCount = fileNames.length;

        int gridCol = 1;
        int gridRow = fileCount;

        for (int r = 1; r <= Math.sqrt(fileCount); r++) {
            int c = (int) Math.ceil((double) fileCount / r);

            if (r * c >= fileCount) {
                gridRow = r;
                gridCol = c;
            }
        }

        this.cols = gridCol;
        this.rows = gridRow;

        System.out.println("Best grid for " + fileCount + " files: " + cols + "x" + rows);

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < fileCount) {
                    String fileName = new File(fileNames[index]).getName();
                    Square square = new Square(fileName, i, j, "#FFFFFF");
                    squares.add(square);
                    index++;
                }
            }
        }
    }

    public void displayBoard() {
        JFrame frame = new JFrame("File Grid (" + cols + " x " + rows + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(rows, cols, 10, 10));

        JPanel[][] gridPanels = new JPanel[rows][cols];

        Map<String, String> rainbowColors = new LinkedHashMap<>();
        rainbowColors.put("Red", "#FF0000");
        rainbowColors.put("Orange", "#FF7F00");
        rainbowColors.put("Yellow", "#FFFF00");
        rainbowColors.put("Green", "#00FF00");
        rainbowColors.put("Blue", "#0000FF");
        rainbowColors.put("Indigo", "#4B0082");
        rainbowColors.put("Violet", "#8F00FF");

        String[] colors = rainbowColors.keySet().toArray(new String[0]);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gridPanels[i][j] = new JPanel();
                gridPanels[i][j].setPreferredSize(new Dimension(100, 60));
                gridPanels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }
        for (Square square : squares) {
            int r = square.getRow();
            int c = square.getCol();

            gridPanels[r][c].setBackground(Color.decode(square.getColor()));
            gridPanels[r][c].setToolTipText(square.getFileName());
            gridPanels[r][c].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    String selectedColor = (String) JOptionPane.showInputDialog(
                            frame,
                            "Choose New Color:",
                            "Color Picker",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            colors,
                            null);

                    if (selectedColor != null) {
                        String newColor = rainbowColors.get(selectedColor);
                        square.setColor(newColor);
                        gridPanels[r][c].setBackground(Color.decode(newColor));
                        frame.repaint();
                    }
                }
            });
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JPanel cellWrapper = new JPanel(new BorderLayout());
                cellWrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                cellWrapper.add(gridPanels[i][j], BorderLayout.CENTER);
                frame.add(cellWrapper);
            }
        }
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void setSquare(int row, int col, Square s) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            for (int i = 0; i < squares.size(); i++) {
                Square sq = squares.get(i);
                if (sq.getRow() == row && sq.getCol() == col) {
                    squares.set(i, s);
                    return;
                }
            }
            squares.add(s);
        } else {
            System.out.println("Invalid grid position: (" + row + ", " + col + ")");
        }
    }

    public Square getSquare(int row, int col) {
        for (Square s : squares) {
            if (s.getRow() == row && s.getCol() == col) {
                return s;
            }
        }
        return null;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int squareSize = 50;
        int padding = 10;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * (squareSize + padding);
                int y = row * (squareSize + padding);
                g.setColor(Color.WHITE);
                g.fillRect(x, y, squareSize, squareSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, squareSize, squareSize);
            }
        }
    }

    public static void showGrid(int rows, int cols) {
        JFrame frame = new JFrame("File Grid");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Board());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
