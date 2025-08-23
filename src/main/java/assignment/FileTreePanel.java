package assignment;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FileTreePanel extends JPanel {
    private final JTree tree;
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;

    public FileTreePanel(Blackboard blackboard) {
        super(new BorderLayout());
        root = new DefaultMutableTreeNode("Files");
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);

        blackboard.addObserver(bb -> {
            updateTree(bb.getFiles());
        });
    }

    private void updateTree(List<FileInfo> files) {
        root.removeAllChildren();
        Map<String, DefaultMutableTreeNode> pathMap = new HashMap<>();
        pathMap.put("", root);

        for (FileInfo file : files) {
            String[] parts = file.name.split("/");
            StringBuilder path = new StringBuilder();
            DefaultMutableTreeNode parent = root;
            for (int i = 0; i < parts.length; i++) {
                if (path.length() > 0)
                    path.append("/");
                path.append(parts[i]);
                String currPath = path.toString();
                DefaultMutableTreeNode node = pathMap.get(currPath);
                if (node == null) {
                    node = new DefaultMutableTreeNode(parts[i]);
                    parent.add(node);
                    pathMap.put(currPath, node);
                }
                parent = node;
            }
        }
        treeModel.reload();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
}
