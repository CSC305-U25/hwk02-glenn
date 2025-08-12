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
 * @version 3.0
 */

public class Square {
    private final String fileName;
    private final int lineCount;
    private final boolean placeholder;
    private Color color;

    public Square(String fileName, int lineCount) {
        this.fileName = fileName;
        this.lineCount = lineCount;
        this.placeholder = false;
        this.color = determineColor(lineCount);
    }

    private Square(boolean placeholder) {
        this.fileName = "";
        this.lineCount = 0;
        this.placeholder = placeholder;
        this.color = new Color(255, 255, 255);
    }

    public static Square placeholder() {
        return new Square(true);
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
        return color;
    }

    public void setAwtColor(Color c) {
        this.color=  c;
    }

    public String getColorHex() {
        Color c = (color == null) ? new Color(180, 180, 180) : color;
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static Color determineColor(int lines) {
        if (lines < 10) {
            return Color.GREEN;
        } else if (lines < 20) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    @Override
    public String toString() {
        return fileName + " (" + lineCount + " lines)";
    }
}
