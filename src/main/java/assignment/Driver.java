package assignment;

import java.awt.*;
import javax.swing.*;

/**
 * Entry point for the File Grid application.
 * Launches the main application window for visualizing files from a GitHub
 * repository as a grid of colored squares and relations.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Driver extends JFrame {
    private FileHandler fileHandler;
    private final JLabel statusLabel = new JLabel("");

    public Driver() {
        super("Assignment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Blackboard blackboard = new Blackboard();
        fileHandler = new FileHandler(blackboard);

        TopPanel topPanel = new TopPanel(statusLabel);
        add(topPanel, BorderLayout.NORTH);

        JTextField selectedField = new JTextField();
        MainPanel mainPanel = new MainPanel(blackboard, selectedField, statusLabel);
        add(mainPanel, BorderLayout.CENTER);

        BottomPanel bottomPanel = new BottomPanel(selectedField);
        add(bottomPanel, BorderLayout.SOUTH);

        topPanel.okButton.addActionListener(e -> loadFiles(topPanel.urlField, topPanel.okButton));
        topPanel.urlField.addActionListener(e -> loadFiles(topPanel.urlField, topPanel.okButton));

        setSize(1100, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadFiles(JTextField urlField, JButton okButton) {
        String input = urlField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a GitHub URL.");
            return;
        }

        okButton.setEnabled(false);
        urlField.setEnabled(false);
        statusLabel.setText("Loading...");

        Throwable error = null;
        try {
            fileHandler.fetchFromGithub(input);
        } catch (Throwable t) {
            error = t;
        }
        if (error == null) {
            statusLabel.setText("Done.");
        } else {
            statusLabel.setText("Error.");
        }
        okButton.setEnabled(true);
        urlField.setEnabled(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Driver::new);
    }
}