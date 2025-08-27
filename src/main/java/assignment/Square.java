package assignment;

import java.awt.*;

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
    private final String fileName;
    private final int lineCount;
    private final Color color;

    public Square(String fileName, int lineCount) {
        this.fileName = fileName;
        this.lineCount = lineCount;
        this.color = setColor(lineCount);
    }

    private static Color setColor(int lines) {
        if (lines < 10) { return Color.GREEN; }
        else if (lines < 20) { return Color.YELLOW; }
        else { return Color.RED; }
    }

    public String getFileName() {
        return fileName;
    }

    public void draw(Graphics g, int x, int y, int w, int h) {
        // Square
        g.setColor(color);
        g.fillRect(x, y, w, h);

        // Border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);

        String name = fileName.substring(fileName.lastIndexOf('/') + 1);
        FontMetrics fm = g.getFontMetrics();
        String text = name;
        int tx = x + (w - fm.stringWidth(text)) / 2;
        int ty = y + ((h - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(name, tx, ty);
    }

    public static void drawEmpty(Graphics g, int x, int y, int w, int h) {
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
