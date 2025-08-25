package assignment;

import java.util.*;
import java.util.List;
import java.awt.*;

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

public class Relations extends JPanel {
    private final Blackboard bb;
    private List<ClassDesc> classes = List.of();
    private List<Relation>  relations = List.of();
    private final Map<String, Rectangle> boxes = new HashMap<>();
    private Map<String, Integer> linesByClass = Map.of();

    public Relations(Blackboard bb) {
        this.bb = bb;
        setOpaque(true);
        bb.addObserver(b -> {
            classes   = b.getClasses();
            relations = b.getRelations();
            Map<String,Integer> m = new HashMap<>();
            for (FileInfo f : b.getFiles()) {
                if (f.name.endsWith(".java")) {
                    m.put(f.name.substring(0, f.name.length() - 5), f.lines);
                }
            }
            linesByClass = m;
            revalidate(); repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (classes.isEmpty()) return;

        layoutCircle();

        g.setColor(Color.DARK_GRAY);
        for (var r : relations) {
            Rectangle a = boxes.get(r.src), b = boxes.get(r.dst);
            if (a == null || b == null) continue;
            g.drawLine(a.x + a.width/2, a.y + a.height/2,
                       b.x + b.width/2, b.y + b.height/2);
        }
        for (var c : classes) {
            Rectangle r = boxes.get(c.name);
            if (r != null) new Square(c.name, linesByClass.getOrDefault(c.name, 0))
                    .draw(g, r.x, r.y, r.width, r.height);
        }
    }

    private void layoutCircle() {
        boxes.clear();
        int n = classes.size();
        if (n == 0) return;

        int w = 90, h = 40;
        int cx = getWidth() / 2, cy = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 2 - w;
        double step = 2 * Math.PI / n;

        for (int i = 0; i < n; i++) {
            double a = i * step;
            int x = (int)(cx + radius * Math.cos(a)) - w/2;
            int y = (int)(cy + radius * Math.sin(a)) - h/2;
            boxes.put(classes.get(i).name, new Rectangle(x, y, w, h));
        }
    }
}
