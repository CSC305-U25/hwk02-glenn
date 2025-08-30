package assignment;

import java.awt.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Represents a single square in the grid board, containing file information.
 * Each square holds a filename, line count, and a color determined by the line
 * count.
 * Used for visually representing files in the grid layout.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Square {
    private static final Logger logger = LoggerFactory.getLogger(Square.class);

    public enum SquareKind { JAVA, FOLDER, OTHER }
    private final String fileName;
    private final int lineCount;
    private final Color color;
    private final SquareKind kind;

    public Square(String fileName, int lineCount) {
        this(fileName, lineCount, detectKind(fileName));
    }

    public Square(String fileName, int lineCount, SquareKind kind) {
        this.fileName = fileName;
        this.lineCount = lineCount;
        this.kind = kind;
        this.color = setColor(lineCount, kind);
        logger.debug("Square created: file='{}', lines='{}', kind='{}'",
            fileName, lineCount, kind);
    }

    private static Color setColor(int lines, SquareKind kind) {
        if (kind == null) return Color.WHITE;
        return switch(kind) {
            case JAVA -> (lines < 10) ? Color.GREEN
                        : (lines < 20) ? Color.YELLOW
                                        : Color.RED;
            case FOLDER -> Color.GRAY;
            case OTHER -> Color.LIGHT_GRAY;
        };
    }

    public static SquareKind detectKind(String name) {
        if (name != null && name.endsWith("/")) return SquareKind.FOLDER;
        return (name != null && name.endsWith(".java")) ? SquareKind.JAVA : SquareKind.OTHER;
    }

    public SquareKind getKind() { return kind; }
    public String getFileName() {
        return fileName;
    }

    public void draw(Graphics g, int x, int y, int w, int h) {
        logger.trace("Draw square '{}' at ({}, {}) {}x{}; kind={}",
                    fileName, x, y, w, h, kind);
        // Square
        g.setColor(color);
        g.fillRect(x, y, w, h);

        // Folder bar
        if (kind == SquareKind.FOLDER) {
            int barHeight = Math.max(4, h / 10);
            int halfW = w / 2;
            g.setColor(Color.BLACK);
            g.fillRect(x, y, halfW, barHeight);
            g.setColor(Color.WHITE);
            g.fillRect(x + halfW, y, w - halfW, barHeight);
        }

        // White triangle for non-java files
        if (kind == SquareKind.OTHER) {
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
