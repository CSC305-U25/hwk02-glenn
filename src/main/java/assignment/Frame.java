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

public class Frame extends JFrame implements Runnable{
    private FileHandler fileHandler;
    private final JLabel statusLabel = new JLabel("");

    public Frame() {
        super("Assignment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
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
        JTextField urlField = new JTextField();
        urlRow.add(urlField, BorderLayout.CENTER);
        JButton okButton = new JButton("OK");
        urlRow.add(okButton, BorderLayout.EAST);

        JPanel urlWrap = new JPanel(new BorderLayout());
        urlWrap.add(urlLabel, BorderLayout.NORTH);
        urlWrap.add(urlRow, BorderLayout.CENTER);

        urlWrap.add(statusLabel, BorderLayout.SOUTH);
        urlWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        topPanel.add(urlWrap, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JTextField selectedField = new JTextField();
        MainPanel mainPanel = new MainPanel(blackboard, selectedField, statusLabel);
        add(mainPanel, BorderLayout.CENTER);

        okButton.addActionListener(e -> loadFiles(urlField, okButton));
        urlField.addActionListener(e -> loadFiles(urlField, okButton));

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

        new Thread(() -> {
            Throwable error = null;
            try { fileHandler.fetchFromGithub(input); }
            catch (Throwable t) { error = t; }
            final Throwable err = error;
            EventQueue.invokeLater(() -> {
                try {
                    if(err == null) { statusLabel.setText("Done.");}
                    else { statusLabel.setText("Error."); }
                } finally{
                    okButton.setEnabled(true);
                    urlField.setEnabled(true);
                }
            });
       }, "Fetch-Worker").start();
    }
}
