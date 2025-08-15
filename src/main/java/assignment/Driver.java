package assignment;

import javax.swing.*;

/**
 * Entry point for the File Grid application.
 * Launches the main application window for visualizing files from a GitHub repository
 * as a grid of colored squares.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 4.0
 */

public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Frame frame = new Frame();
            frame.setVisible(true);
        });
    }
}
