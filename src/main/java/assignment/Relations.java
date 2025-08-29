package assignment;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

/**
 * Panel for visualizing class relationships as a diagram.
 * Draws classes and their aggregation/dependency relations in a circular
 * layout.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Relations extends JPanel implements PropertyChangeListener{
    private final Blackboard bb;

    public Relations(Blackboard blackboard) {
        this.bb = blackboard;
        setOpaque(true);
    }

    @Override public void addNotify(){
        super.addNotify();
        bb.addPropertyChangeListener(this);
    }

    @Override
    public void removeNotify() {
        bb.removePropertyChangeListener(this);
        super.removeNotify();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String p = evt.getPropertyName();
        if("files".equals(p) || "classes".equals(p) || "relations".equals(p)
            || "model".equals(p)) {
            revalidate();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Set<String> allowed = new LinkedHashSet<>();
        Map<String,Integer> linesByClass = new HashMap<>();
        for (FileInfo f : bb.getJavaFiles()) {
            String base = Names.baseName(f.name);
            allowed.add(base);
            linesByClass.put(base, f.lines);
        }
        if (allowed.isEmpty()) return;

        var classIsJava = JavaFilter.forClasses(bb.getJavaFiles());
        List<ClassDesc> classes = new ArrayList<>();
        for (ClassDesc c : bb.getClasses()) {
            if(classIsJava.test(c)) classes.add(c);
        }
        if (classes.isEmpty()) return;

        Map<String, Rectangle> boxes = layoutCircle(getWidth(), getHeight(),
                classes, 90, 40);

        g.setColor(Color.BLACK);
        for (Relation r : bb.getRelations()) {
            if (!allowed.contains(r.src) || !allowed.contains(r.dst)) continue;
            Rectangle a = boxes.get(r.src), b = boxes.get(r.dst);
            if (a == null || b == null) continue;
            g.drawLine(a.x + a.width / 2, a.y + a.height / 2,
                        b.x + b.width / 2, b.y + b.height / 2);
        }

        for (ClassDesc c : classes) {
            Rectangle rect = boxes.get(c.name);
            if (rect != null) {
                int lines = linesByClass.getOrDefault(c.name, 0);
                new Square(c.name, lines, true).draw(g, rect.x, rect.y, rect.width, rect.height);
            }
        }
    }

    private static Map<String, Rectangle> layoutCircle(
            int panelW, int panelH, List<ClassDesc> classes, int w, int h) {
        Map<String, Rectangle> out = new HashMap<>(classes.size() * 2);
        int n = classes.size();
        if (n == 0) return out;

        int cx = panelW / 2, cy = panelH / 2;
        int radius = Math.max(0, Math.min(panelW, panelH) / 2 - w);
        double step = 2 * Math.PI / n;

        for (int i = 0; i < n; i++) {
            double a = i * step;
            int x = (int) (cx + radius * Math.cos(a)) - w / 2;
            int y = (int) (cy + radius * Math.sin(a)) - h / 2;
            out.put(classes.get(i).name, new Rectangle(x, y, w, h));
        }
        return out;
    }
}
