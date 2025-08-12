package assignment;

import javax.swing.*;

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
 * @version 3.0
 */

public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Frame frame = new Frame();
            frame.setVisible(true);
        });
    }
}
