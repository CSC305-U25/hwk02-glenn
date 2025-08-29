package assignment;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main content panel for the application.
 * Contains the split pane with the file tree and tabbed board/relations view,
 * as well as the bottom panel for file selection status.
 * Listens for property changes to update the board view dynamically.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public class MainPanel extends JPanel implements PropertyChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(MainPanel.class);
    private final Blackboard bb;
    private final Board board;

    public MainPanel(Blackboard blackboard, JTextField selectedField) {
        super(new BorderLayout());
        this.bb = blackboard;
        Color bg = UIManager.getColor("Panel.background");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        board = new Board(blackboard);
        Relations relations = new Relations(blackboard);
        Diagram diagram = new Diagram(blackboard);
        Metrics metrics = new Metrics(blackboard);

        JPanel centerDiagram = new JPanel(new GridBagLayout());
        centerDiagram.add(diagram, new GridBagConstraints());
        JScrollPane classDiagramScroll = new JScrollPane(centerDiagram);
        classDiagramScroll.setBorder(BorderFactory.createEmptyBorder());
        classDiagramScroll.getViewport().setBackground(bg);

        JPanel boardWrap = wrap(bg, board);
        JPanel relationsWrap = wrap(bg, relations);
        JPanel classDiagramWrap = wrap(bg, classDiagramScroll);
        JPanel metricsWrap = wrap(bg, metrics);

        FileTreePanel treeWrap = new FileTreePanel(blackboard);
        treeWrap.setBackground(bg);
        treeWrap.setPreferredSize(new Dimension(260, 1));

        JTabbedPane rightTabs = new JTabbedPane();
        rightTabs.addTab("Board", boardWrap);
        rightTabs.addTab("Relations", relationsWrap);
        rightTabs.addTab("Class Diagram", classDiagramWrap);
        rightTabs.addTab("Metrics", metricsWrap);
        rightTabs.setBackground(bg);

        splitPane.setLeftComponent(treeWrap);
        splitPane.setRightComponent(rightTabs);
        splitPane.setOneTouchExpandable(false);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(5);
        splitPane.setContinuousLayout(true);

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 12, 8, 12)));
        bottomPanel.setBackground(bg);

        JLabel selectedTitle = new JLabel("Error:");
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

        List<Square> squares = new ArrayList<>();
        for (var f : bb.getFiles())
            squares.add(new Square(f.name, f.lines));
        board.setSquare(squares);
    }

    private static JPanel wrap(Color bg, JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        bb.addPropertyChangeListener(this);
        logger.debug("MainPanel attached as PropertyChangeListener");
    }

    @Override
    public void removeNotify() {
        bb.removePropertyChangeListener(this);
        super.removeNotify();
        logger.debug("MainPanel detached as a PropertyChangeListener");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String p = evt.getPropertyName();
        if ("files".equals(p) || "model".equals(p)) {
            logger.trace("Ignoring property change: {}", p);
            return;
        }
        EventQueue.invokeLater(() -> {
            List<Square> squares = new ArrayList<>();
            for (var f : bb.getFiles())
                squares.add(new Square(f.name, f.lines));
            board.setSquare(squares);
            board.repaint();
            logger.info("Board updated with {} files after property change: {}", squares.size(), p);
        });
    }
}
