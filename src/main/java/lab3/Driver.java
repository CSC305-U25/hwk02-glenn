package lab3;

import lab3.FileHandler;
import lab3.Board;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.io.File;
import java.util.ArrayList;

/**
 * Main driver class for the File Grid application.
 * This class serves as the entry point that orchestrates the file selection
 * process,
 * reads files from a chosen directory, creates a board grid, and displays the
 * interactive GUI. Users can select a folder and view its files arranged in
 * an optimal grid layout with color-coded squares.
 * 
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 2.0
 */

public class Driver {
    public static void main(String[] args) {
        String directory = FileHandler.chooseFolder();

        if (directory != null) {
            String[] fileNames = FileHandler.readFileNames(directory);

            if (fileNames == null || fileNames.length == 0) {
                System.out.println("No files.");
                return;
            }

            Board board = new Board();
            board.createBoard(fileNames);
            board.displayBoard();
        } else {
            System.out.println("Folder Selecting Cancelled.");
        }

    }
}
