package assignment;

import java.awt.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a single square in the grid board, containing file information.
 * Each square holds a filename, line count, and a color determined by the line
 * count and file type (Java, non-Java, or folder).
 * Used for visually representing files and folders in the grid layout.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Square {
    private static final Logger logger = LoggerFactory.getLogger(Square.class);

    private final String fileName;
    private final int lineCount;
    private final Color color;
    private final boolean isJava;

    public Square(String fileName, int lineCount) {
        this(fileName, lineCount, false);
    }

    public Square(String fileName, int lineCount, boolean isJava) {
        this.fileName = fileName;
        this.lineCount = lineCount;
        this.isJava = isJava;
        this.color = setColor(lineCount, isJava);
        logger.debug("Square created: file='{}', lines='{}',isJava='{}',color='{}'",
                fileName, lineCount, isJava);
    }

    // Updated color logic
    private static Color setColor(int lines, boolean isJava) {
        if (!isJava) {
            return Color.LIGHT_GRAY;
        }
        if (lines < 10) {
            return Color.GREEN;
        } else if (lines < 20) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void draw(Graphics g, int x, int y, int w, int h) {
        logger.trace("Draw square '{}' at ({}, {}) {}x{}; isJava={}, color={}",
                fileName, x, y, w, h, isJava);
        // Square
        g.setColor(color);
        g.fillRect(x, y, w, h);

        // Folder bar
        if (!isJava && lineCount == 0) {
            int barHeight = Math.max(4, h / 10);
            int halfW = w / 2;
            g.setColor(Color.BLACK);
            g.fillRect(x, y, halfW, barHeight);
            g.setColor(Color.WHITE);
            g.fillRect(x + halfW, y, w - halfW, barHeight);
        }

        // White triangle
        if (!isJava && lineCount > 0) {
            int[] tx = { x + w, x + w, x + w - w / 3 };
            int[] ty = { y + h, y + h - h / 3, y + h };
            g.setColor(Color.WHITE);
            g.fillPolygon(tx, ty, 3);
            g.setColor(Color.GRAY);
            g.drawPolygon(tx, ty, 3);
        }

        // Border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);

        String name = Names.baseName(fileName);

        FontMetrics fm = g.getFontMetrics();
        String text = name;
        int tx = x + (w - fm.stringWidth(text)) / 2;
        int ty = y + ((h - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(name, tx, ty);
    }

    public static void drawEmpty(Graphics g, int x, int y, int w, int h) {
        logger.trace("Draw empty square at ({}, {}) {}x{}", x, y, w, h);
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
    }

    @Override
    public String toString() {
        return fileName + " (" + lineCount + " lines)";
    }
}
