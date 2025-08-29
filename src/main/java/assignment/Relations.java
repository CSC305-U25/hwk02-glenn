package assignment;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for visualizing class relationships as a diagram.
 * Draws classes and their aggregation/dependency relations in a circular
 * layout.
 * Listens to property changes from the Blackboard to update the visualization.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Relations extends JPanel implements PropertyChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(Relations.class);
    private final Blackboard bb;

    public Relations(Blackboard blackboard) {
        this.bb = blackboard;
        setOpaque(true);
        logger.debug("Relations panel constructed");
    }

    @Override
    public void addNotify() {
        super.addNotify();
        bb.addPropertyChangeListener(this);
        logger.debug("Relations attached as PropertyChangeListener");
    }

    @Override
    public void removeNotify() {
        bb.removePropertyChangeListener(this);
        logger.debug("Relations detached as PropertyChangeListener");
        super.removeNotify();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String p = evt.getPropertyName();
        if ("files".equals(p) || "classes".equals(p) || "relations".equals(p)
                || "model".equals(p)) {
            logger.debug("Relations repaint triggered by property change: {}", p);
            revalidate();
            repaint();
        } else {
            logger.trace("Relations ignoring property change: {}", p);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        logger.trace("paintcomponent size={}x{}", getWidth(), getHeight());

        Set<String> allowed = new LinkedHashSet<>();
        Map<String, Integer> linesByClass = new HashMap<>();
        for (FileInfo f : bb.getJavaFiles()) {
            String base = Names.baseName(f.name);
            allowed.add(base);
            linesByClass.put(base, f.lines);
        }
        if (allowed.isEmpty()) {
            logger.debug("no Java files found; nothing to render");
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Allowed classes for relations: {}", allowed.size());
        }

        var classIsJava = JavaFilter.forClasses(bb.getJavaFiles());
        List<ClassDesc> classes = new ArrayList<>();
        for (ClassDesc c : bb.getClasses()) {
            if (classIsJava.test(c))
                classes.add(c);
        }
        if (classes.isEmpty()) {
            logger.debug("no ClassDesc enrtries matched allowed classes; nothing to render");
            return;
        }
        Map<String, Rectangle> boxes = layoutCircle(getWidth(), getHeight(),
                classes, 90, 40);

        g.setColor(Color.BLACK);
        for (Relation r : bb.getRelations()) {
            if (!allowed.contains(r.src) || !allowed.contains(r.dst))
                continue;
            Rectangle a = boxes.get(r.src), b = boxes.get(r.dst);
            if (a == null || b == null)
                continue;
            g.drawLine(a.x + a.width / 2, a.y + a.height / 2,
                    b.x + b.width / 2, b.y + b.height / 2);
        }

        int boxesDrawn = 0;
        for (ClassDesc c : classes) {
            Rectangle rect = boxes.get(c.name);
            if (rect != null) {
                int lines = linesByClass.getOrDefault(c.name, 0);
                new Square(c.name, lines, true).draw(g, rect.x, rect.y, rect.width, rect.height);
                boxesDrawn++;
            }
        }
        logger.debug("Drew {} class box(es)", boxesDrawn);
    }

    private static Map<String, Rectangle> layoutCircle(
            int panelW, int panelH, List<ClassDesc> classes, int w, int h) {
        Map<String, Rectangle> out = new HashMap<>(classes.size() * 2);
        int n = classes.size();
        if (n == 0)
            return out;

        int cx = panelW / 2, cy = panelH / 2;
        int radius = Math.max(0, Math.min(panelW, panelH) / 2 - w);
        double step = 2 * Math.PI / n;

        logger.trace("Layout Circle: n={}, center=({},{}), radius={}, step={}",
                n, cx, cy, radius, step);
        for (int i = 0; i < n; i++) {
            double a = i * step;
            int x = (int) (cx + radius * Math.cos(a)) - w / 2;
            int y = (int) (cy + radius * Math.sin(a)) - h / 2;
            out.put(classes.get(i).name, new Rectangle(x, y, w, h));
        }
        return out;
    }
}
