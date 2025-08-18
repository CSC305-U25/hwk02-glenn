package assignment;

import java.util.ArrayList;
import java.util.List;
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

    private final Blackboard blackboard;
    private final FileHandler fileHandler;
    private final Board board;
    private final Relations relations;

    private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    public Frame() {
        super("Assignment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        blackboard = new Blackboard();
        fileHandler = new FileHandler(blackboard);

        board = new Board();
        relations = new Relations(blackboard);

        Color bg = UIManager.getColor("Panel.background");

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
            BorderFactory.createEmptyBorder(8, 12, 0, 12)
        ));
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

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(bg);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel boardWrap = new JPanel(new BorderLayout());
        boardWrap.setBackground(bg);
        boardWrap.add(board, BorderLayout.CENTER);

        JPanel relationsWrap = new JPanel(new BorderLayout());
        relationsWrap.setBackground(bg);
        relationsWrap.add(relations, BorderLayout.CENTER);

        splitPane.setLeftComponent(boardWrap);
        splitPane.setRightComponent(relationsWrap);
        splitPane.setOneTouchExpandable(false);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        splitPane.setContinuousLayout(true);

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
            BorderFactory.createEmptyBorder(0, 12, 8, 12)
        ));
        bottomPanel.setBackground(bg);

        JLabel selectedTitle = new JLabel("Selected File Name:");
        selectedTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        selectedField.setEditable(false);
        selectedField.setBackground(Color.WHITE);
        selectedField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 0, 0, 0)
        ));

        JPanel bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.add(selectedField, BorderLayout.CENTER);
        bottomWrap.add(selectedTitle, BorderLayout.WEST);
        bottomWrap.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));


        bottomPanel.add(bottomWrap, BorderLayout.CENTER);
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

        blackboard.addObserver(bb -> {
            List<Square> squares = new ArrayList<>();
            for(var f : bb.getFiles()) {
                squares.add(new Square(f.name, f.lines));
            }
            board.setSquare(squares);
            board.repaint();
            statusLabel.setText("Scanned files: " + squares.size());
        });

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
