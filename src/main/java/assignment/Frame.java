package assignment;

import java.util.ArrayList;
import java.awt.*;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

/**
 * Represents a visual grid board that displays files as colored squares in a
 * GUI.
 * This class extends JPanel and provides functionality to create an optimal
 * grid layout
 * for displaying files, allowing users to interact with squares by changing
 * their colors.
 * The board automatically calculates the best grid dimensions to fit all files
 * efficiently.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 3.0
 */

public class Frame extends JFrame {
    private final JTextField urlField = new JTextField();
    private final JButton okButton = new JButton("OK");
    private final JTextField selectedField = new JTextField();
    private final Board board = new Board();

    public Frame() {
        super("Assignment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 8));

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JLabel urlLabel = new JLabel("GitHub Folder URL");
        urlLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JPanel urlRow = new JPanel(new BorderLayout(8, 0));
        urlRow.add(urlField, BorderLayout.CENTER);
        urlRow.add(okButton, BorderLayout.EAST);

        JPanel urlWrap = new JPanel(new BorderLayout());
        urlWrap.add(urlLabel, BorderLayout.NORTH);
        urlWrap.add(urlRow, BorderLayout.CENTER);

        topPanel.add(urlWrap, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        board.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 12, 0, 12),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, UIManager.getColor("Panel.background").darker()),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12))));
        add(board, BorderLayout.CENTER);

        selectedField.setEditable(false);
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        bottomPanel.add(new JLabel("Selected File Name:"), BorderLayout.WEST);
        bottomPanel.add(selectedField, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        board.onSelectionChange(sq -> {
            selectedField.setText(
                    (sq == null || sq.isPlaceHolder()) ? "" : sq.getFileName());
        });

        okButton.addActionListener(e -> loadFiles());
        urlField.addActionListener(e -> loadFiles());

        setSize(1100, 800);
        setLocationRelativeTo(null);
    }

    private void loadFiles() {
        String input = urlField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a GitHub URL or local folder path.");
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
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(
                        Frame.this,
                        "Loading was interrupted.",
                        "Interrupted",
                        JOptionPane.ERROR_MESSAGE
                    );
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    String msg = (cause != null && cause.getMessage() != null) ? cause.getMessage() : ex.toString();
                    JOptionPane.showMessageDialog(
                        Frame.this,
                        "Unexpected error: " + msg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
                finally {
                    okButton.setEnabled(true);
                    urlField.setEnabled(true);
                }
            }
        }.execute();
    }
}
