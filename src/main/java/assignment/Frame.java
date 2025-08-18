package assignment;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders;

/**
 * Main application window for the file grid board.
 * Provides a GUI for entering a GitHub folder URL, displays files as colored squares,
 * and shows file names on hover. Handles user interaction and asynchronous loading of files.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 4.0
 */

public class Frame extends JFrame {
    private final JTextField urlField = new JTextField();
    private final JButton okButton = new JButton("OK");
    private final JTextField selectedField = new JTextField();
    private final JLabel statusLabel = new JLabel("");

    private final Board board = new Board();
    private final Relations relations = new Relations();
    private final Blackboard blackboard = new Blackboard();

    private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    public Frame() {
        super("Assignment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 8));

        Color bg = UIManager.getColor("Panel.background");

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
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

        topPanel.add(urlWrap, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UIManager.getColor("Panel.background"));
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel boardWrap = new JPanel(new BorderLayout());
        boardWrap.setBorder(BorderFactory.createEmptyBorder());
        boardWrap.setBackground(bg);
        boardWrap.add(board, BorderLayout.CENTER);

        JPanel relationsWrap = new JPanel(new BorderLayout());
        relationsWrap.setBorder(BorderFactory.createEmptyBorder());
        relationsWrap.setBackground(bg);
        relationsWrap.add(relations, BorderLayout.CENTER);

        splitPane.setLeftComponent(boardWrap);
        splitPane.setRightComponent(relationsWrap);
        splitPane.setOneTouchExpandable(false);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        splitPane.setContinuousLayout(true);

        selectedField.setEditable(false);
        selectedField.setBackground(Color.WHITE);

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
            BorderFactory.createEmptyBorder(5, 12, 4, 12)
        ));
        bottomPanel.add(new JLabel("Selected File Name:"), BorderLayout.WEST);
        bottomPanel.add(selectedField, BorderLayout.CENTER);

        centerPanel.add(splitPane, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        board.setOnHoverChange(sq -> {
            String name = (sq == null) ? null : sq.getFileName();
            selectedField.setText(name == null ? "" : name);
            blackboard.setSelectedFile(name);
        });

        okButton.addActionListener(e -> loadFiles());
        urlField.addActionListener(e -> loadFiles());

        setSize(1100, 800);
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.5));
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

        new SwingWorker<ArrayList<Square>, Void>() {
            @Override
            protected ArrayList<Square> doInBackground() throws Exception {
                return FileHandler.fetchFromGithub(input);
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Square> squares = get();
                    board.setSquare(squares);
                    board.repaint();
                    statusLabel.setText("Scanned Files: " + squares.size());
                } catch (Exception ex) {
                    Throwable cause = ex.getCause();
                    if (cause == null) cause = ex;
                    JOptionPane.showMessageDialog(Frame.this, "Error: " + cause.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                finally {
                    okButton.setEnabled(true);
                    urlField.setEnabled(true);
                }
            }
        }.execute();
    }
}
