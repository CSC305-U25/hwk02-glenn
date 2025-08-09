package lab3;

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
    private String fileName;
    private String color;
    private int row;
    private int col;

    public Square(String fileName, int row, int col, String color) {
        this.fileName = fileName;
        this.color = "#FFFFFF"; // default color white.
        this.col = col;
        this.row = row;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d) %s [%s]", row, col, fileName, color);
    }
}
