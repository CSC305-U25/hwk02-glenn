package assignment;

import java.awt.*;
import javax.swing.*;

/**
 * Main application window for the file grid board.
 * Provides a GUI for entering a GitHub folder URL, displays files as colored
 * squares, and shows file names on hover. Handles user interaction and
 * asynchronous
 * loading of files.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Frame extends JFrame {
    private final JTextField urlField = new JTextField();
    private final JButton okButton = new JButton("OK");
    private final JTextField selectedField = new JTextField();
    private final JLabel statusLabel = new JLabel("");

    private final FileHandler fileHandler;

    public Frame() {
        super("Assignment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Blackboard blackboard = new Blackboard();
        fileHandler = new FileHandler(blackboard);

        Color bg = UIManager.getColor("Panel.background");

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(8, 12, 0, 12)));
        topPanel.setBackground(bg);

        JLabel urlLabel = new JLabel("GitHub Folder URL");
        urlLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JPanel urlRow = new JPanel(new BorderLayout(4, 0));
        urlRow.add(urlField, BorderLayout.CENTER);
        urlRow.add(okButton, BorderLayout.EAST);

        JPanel urlWrap = new JPanel(new BorderLayout());
        urlWrap.add(urlLabel, BorderLayout.NORTH);
        urlWrap.add(urlRow, BorderLayout.CENTER);
        urlWrap.add(statusLabel, BorderLayout.SOUTH);
        urlWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        topPanel.add(urlWrap, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        MainPanel mainPanel = new MainPanel(blackboard, selectedField, statusLabel);
        add(mainPanel, BorderLayout.CENTER);

        okButton.addActionListener(e -> loadFiles());
        urlField.addActionListener(e -> loadFiles());

        setSize(1100, 800);
        setLocationRelativeTo(null);
    }

    public void showHover(String text) {
        selectedField.setText(text == null ? "" : text);
    }

    private void loadFiles() {
        String input = urlField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a GitHub URL.");
            return;
        }

        okButton.setEnabled(false);
        urlField.setEnabled(false);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                fileHandler.fetchFromGithub(input);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause();
                    if (cause == null)
                        cause = ex;
                    JOptionPane.showMessageDialog(Frame.this, "Error: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    okButton.setEnabled(true);
                    urlField.setEnabled(true);
                }
            }
        }.execute();
    }
}