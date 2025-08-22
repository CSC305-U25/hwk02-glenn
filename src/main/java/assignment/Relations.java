package assignment;

import java.util.*;
import java.util.List;
import java.awt.*;

import javax.swing.*;

import assignment.Blackboard;

public class Relations extends JPanel{

    private final Blackboard bb;

    private List<Blackboard.ClassDesc> classes = List.of();
    private List<Blackboard.Relation> relations = List.of();

    private final Map<String, Rectangle> boxByClass = new HashMap<>();

    public Relations(Blackboard bb) {
        this.bb = bb;
        setOpaque(true);

        bb.addObserver(b -> {
            classes = b.getClasses();
            relations = b.getRelations();
            revalidate();
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(classes.isEmpty()) {
            g.setColor(Color.GRAY);
            return;
        }
        layoutCircle();

        g.setColor(Color.DARK_GRAY);
        for(var r : relations) {
            Rectangle a = boxByClass.get(r.src);
            Rectangle b = boxByClass.get(r.dst);
            if (a == null || b == null) continue;

            Point p1 = centerOf(a);
            Point p2 = centerOf(b);

            if(r.kind == Blackboard.Relation.Kind.AGGREGATION) {
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            } else if (r.kind == Blackboard.Relation.Kind.DEPENDENCY) {
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        for (var c : classes) {
            Rectangle r = boxByClass.get(c.name);
            if(r == null) continue;
            drawClassBox(g, r, c.name);
        }
    }

    private void layoutCircle() {
        boxByClass.clear();
        int n = classes.size();
        if (n == 0) return;

        int w = 90, h = 40;
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 2 - w;

        double step = 2 * Math.PI / n;
        for(int i = 0; i < n; i++) {
            String name = classes.get(i).name;
            double angle = i * step;
            int x = (int)(cx + radius * Math.cos(angle)) - w / 2;
            int y = (int)(cy + radius * Math.sin(angle)) - h / 2;
            boxByClass.put(name, new Rectangle(x, y, w, h));
        }
    }

    private void drawClassBox(Graphics g, Rectangle r, String name) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(Color.BLACK);
        g.drawRect(r.x, r.y, r.width, r.height);

        FontMetrics fm = g.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(name)) / 2;
        int ty = r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(name, tx, ty);
    }

    private static Point centerOf(Rectangle r) {
        return new Point(r.x + r.width / 2, r.y + r.height / 2);
    }
}
