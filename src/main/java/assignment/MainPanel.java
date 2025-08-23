package assignment;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main content panel for the application.
 * Contains the split pane with the file tree and tabbed board/relations view,
 * as well as the bottom panel for file selection status.
 * 
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class MainPanel extends JPanel {
    public MainPanel(Blackboard blackboard, JTextField selectedField, JLabel statusLabel) {
        super(new BorderLayout());
        Color bg = UIManager.getColor("Panel.background");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        Board board = new Board();
        Relations relations = new Relations(blackboard);

        JPanel boardWrap = new JPanel(new BorderLayout());
        boardWrap.setBackground(bg);
        boardWrap.add(board, BorderLayout.CENTER);

        JPanel relationsWrap = new JPanel(new BorderLayout());
        relationsWrap.setBackground(bg);
        relationsWrap.add(relations, BorderLayout.CENTER);

        FileTreePanel treeWrap = new FileTreePanel(blackboard);
        treeWrap.setBackground(bg);

        JTabbedPane rightTabs = new JTabbedPane();
        rightTabs.addTab("Board", boardWrap);
        rightTabs.addTab("Relations", relationsWrap);
        rightTabs.setBackground(bg);

        splitPane.setLeftComponent(treeWrap);
        splitPane.setRightComponent(rightTabs);
        splitPane.setOneTouchExpandable(false);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        splitPane.setContinuousLayout(true);

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 12, 8, 12)));
        bottomPanel.setBackground(bg);

        JLabel selectedTitle = new JLabel("Selected File Name:");
        selectedTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        selectedField.setEditable(false);
        selectedField.setBackground(Color.WHITE);
        selectedField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 0, 0, 0)));

        JPanel bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.add(selectedField, BorderLayout.CENTER);
        bottomWrap.add(selectedTitle, BorderLayout.WEST);
        bottomWrap.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        bottomPanel.add(bottomWrap, BorderLayout.CENTER);

        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        board.setOnHoverChange(sq -> {
            String name = (sq == null) ? null : sq.getFileName();
            selectedField.setText(name == null ? "" : name);
            blackboard.setSelectedFile(name);
        });

        blackboard.addObserver(bb -> {
            List<Square> squares = new ArrayList<>();
            for (var f : bb.getFiles()) {
                squares.add(new Square(f.name, f.lines));
            }
            board.setSquare(squares);
            board.repaint();
            statusLabel.setText("Scanned files: " + squares.size());
        });

        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.5));
    }
}
