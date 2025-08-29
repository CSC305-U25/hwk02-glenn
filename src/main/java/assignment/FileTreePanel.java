package assignment;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.tree.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
/**
 * Panel for displaying a file tree structure using a JTree.
 * Observes the Blackboard and updates the tree when file data changes.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public class FileTreePanel extends JPanel implements PropertyChangeListener{
    private final Blackboard bb;
    private static final Logger logger = LoggerFactory.getLogger(FileTreePanel.class);

    public FileTreePanel(Blackboard blackboard) {
        super(new BorderLayout());
        this.bb = blackboard;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Files");
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        JTree tree = new JTree(treeModel);
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);

        JScrollPane scrollPane = new JScrollPane(tree);

        add(scrollPane, BorderLayout.CENTER);
        logger.debug("FileTreePanel created");
        updateTree(bb.getFiles(), treeModel, tree, root);

        SwingUtilities.invokeLater(
            () -> bb.addPropertyChangeListener(this)
        );
    }

    private void updateTree(List<FileInfo> files,
                            DefaultTreeModel treeModel,
                            JTree tree,
                            DefaultMutableTreeNode root) {
        root.removeAllChildren();
        Map<String, DefaultMutableTreeNode> pathMap = new HashMap<>();
        pathMap.put("", root);

        for (FileInfo file : files) {
            String[] parts = file.name.split("/");
            StringBuilder path = new StringBuilder();
            DefaultMutableTreeNode parent = root;
            for (String part : parts) {
                if (path.length() > 0)
                    path.append("/");
                path.append(part);
                String currPath = path.toString();
                DefaultMutableTreeNode node = pathMap.get(currPath);
                if (node == null) {
                    node = new DefaultMutableTreeNode(part);
                    parent.add(node);
                    pathMap.put(currPath, node);
                }
                parent = node;
            }
        }
        treeModel.reload();
        for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
        logger.debug("Tree updated with {} files (rows={})", files.size(), tree.getRowCount());
    }

    @Override
    public void removeNotify() {
        bb.removePropertyChangeListener(this);
        super.removeNotify();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String p = evt.getPropertyName();
        if (p != null && !p.equals("files") && !p.equals("model")) return;
        logger.debug("Tree Refresh triggered: {}", p);
        EventQueue.invokeLater(()-> {
            JTree tree = null;
            DefaultTreeModel model = null;
            DefaultMutableTreeNode root = null;
            for (Component c : getComponents()) {
                if(c instanceof JScrollPane scrollPane) {
                    Component v = scrollPane.getViewport().getView();
                    if(v instanceof JTree jt) {
                        tree = jt;
                        model = (DefaultTreeModel) jt.getModel();
                        root = (DefaultMutableTreeNode) model.getRoot();
                        break;
                    }
                }
            }
            if(tree == null) return;
            updateTree(bb.getFiles(), model, tree, root);
        });
    }
}
