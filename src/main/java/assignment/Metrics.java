package assignment;

import java.awt.*;
import java.util.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders a simple metric plot for the project.
 * Places each class in an "Abstractness vs Instability" plane with two zones.
 * Listens to blackboard property changes to revalidate and repaint the plot.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public class Metrics extends JPanel implements PropertyChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(Metrics.class);
    private final Blackboard bb;

    public Metrics(Blackboard blackboard) {
        this.bb = blackboard;
        setOpaque(true);
        logger.debug("Metrics panel constructed.");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        logger.trace("paintcomponent: Size={}x{}", getWidth(), getHeight());

        final int pad = 16, dotR = 4;
        Color bg = UIManager.getColor("Panel.background");
        Color plotColor = new Color(160, 248, 172);

        FontMetrics fm = g.getFontMetrics();
        final String xName = "Instablity", yName = "Abstractness";
        int axisGap = Math.max(24, fm.stringWidth(yName) + 10);

        g.setColor(bg);
        g.fillRect(0, 0, getWidth(), getHeight());

        Rectangle plot = plotBounds(getWidth(), getHeight(), pad, axisGap);
        g.setColor(plotColor);
        g.fillRect(plot.x, plot.y, plot.width, plot.height);

        drawZones(g, plot);

        g.setColor(Color.BLACK);
        g.drawRect(plot.x, plot.y, plot.width, plot.height);

        drawAxisLabels(g, plot, xName, yName);

        Map<String, double[]> pts = MetricsPoints.build(bb);
        logger.debug("Rendering {} metric points",
                (pts != null ? Integer.valueOf(pts.size()) : Integer.valueOf(0)));
        MetricsPoints.draw(g, plot, pts, dotR);

    }

    private static Rectangle plotBounds(int w, int h, int pad, int axisGap) {
        int x = pad + axisGap;
        int y = pad;
        int pw = Math.max(1, w - x - pad);
        int ph = Math.max(1, h - y - pad - axisGap);
        return new Rectangle(x, y, pw, ph);
    }

    private static void drawAxisLabels(Graphics g, Rectangle p, String xName, String yName) {
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.BLACK);
        g.drawString(xName,
                p.x + (p.width - fm.stringWidth(xName)) / 2,
                p.y + p.height + fm.getAscent() + 6);
        g.drawString(yName,
                p.x - fm.stringWidth(yName) - 8,
                p.y + (p.height + fm.getAscent()) / 2);
    }

    private static void drawZones(Graphics g, Rectangle p) {
        Rectangle oldClip = g.getClipBounds();
        g.setClip(p.x, p.y, p.width, p.height);

        g.setColor(Color.WHITE);
        g.drawLine(p.x, p.y, p.x + p.width, p.y + p.height);

        int r = (int) Math.round(Math.min(p.width, p.height) * 0.45);

        g.fillOval(p.x - r, p.y + p.height - r, 2 * r, 2 * r);
        g.fillOval(p.x + p.width - r, p.y - r, 2 * r, 2 * r);

        g.setClip(oldClip);
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();

        String painful = "Painful";
        int zone1x = p.x + r / 2;
        int zone1y = p.y + p.height - r / 2;
        int painfulWidth = fm.stringWidth(painful);
        int painfulBaseY = zone1y + (fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(painful,
                zone1x - painfulWidth / 2,
                painfulBaseY);

        String useless = "Useless";
        int zone2x = p.x + p.width - r / 2;
        int zone2y = p.y + r / 2;

        int uselessWidth = fm.stringWidth(useless);
        int uselessBaseY = zone2y + (fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(useless,
                zone2x - uselessWidth / 2,
                uselessBaseY);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        bb.addPropertyChangeListener(this);
        logger.debug("Metrics attached as PropertyChangeListener");
    }

    @Override
    public void removeNotify() {
        bb.removePropertyChangeListener(this);
        logger.debug("Metrics detached as PropertyChangeListener");
        super.removeNotify();
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String p = e.getPropertyName();
        if ("files".equals(p) || "classes".equals(p) || "relations".equals(p) || "model".equals(p)) {
            logger.debug("Metric repaint triggered by property change: {}", p);
            revalidate();
            repaint();
        } else {
            logger.trace("ignoring property change: {}", p);
        }
    }
}