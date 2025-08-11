package assignment;

import java.awt.Color;

/**
 * Represents a single square in the grid board that contains file information.
 * Each square has a filename, position (row and column), and a color.
 * Squares are used to visually represent files in the grid layout with
 * customizable colors for better organization and visual appeal.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 2.0
 */

public class Square {
    private final String fileName;
    private final int lineCount;
    private final boolean placeholder;
    private String colorHex;

    public Square(String fileName, int lineCount) {
        this(fileName, lineCount, false);
    }

    private Square(String fileName, int lineCount, boolean placeholder) {
        this.fileName = fileName;
        this.lineCount = lineCount;
        this.placeholder = placeholder;
    }

    public static Square placeholder() {
        return new Square("", 0, true);
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineCount() {
        return lineCount;
    }

    public boolean isPlaceHolder() {
        return placeholder;
    }

    public Color getAwtColor() {
        if (placeholder) return Color.WHITE;
        if (lineCount < 10) return new Color(0xB6E7A0); // light green
        if (lineCount < 20) return new Color(0xFFF176); // yellow
        return new Color(0xEF5350); // red
    }

    public String getColorHex() {
        if (colorHex == null) {
            Color c = getAwtColor();
            colorHex = String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
        }
        return colorHex;
    }

    public String toString(int row, int col) {
        return String.format("(%d, %d) %s [%s]", row, col, fileName, getColorHex());
    }

    @Override
    public String toString() {
        return toString(-1, -1);
    }
}
