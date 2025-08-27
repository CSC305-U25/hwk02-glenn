package assignment;

import javax.swing.JPanel;
import java.awt.*;

public class Metrics extends JPanel {

    public Metrics(Blackboard blackboard) {
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        Color paraGreen = new Color(0, 204, 102);
        g.setColor(paraGreen);
        g.fillRect(0, 0, getWidth(), getHeight());

        int w = getWidth();
        int h = getHeight();
        int r = Math.min(w, h) / 4;

        g.setColor(Color.WHITE);
        g.fillArc(-r, h - r, 2 * r, 2 * r, 0, 90);

        g.fillArc(w - r, -r, 2 * r, 2 * r, 180, 90);

        g.setColor(Color.BLACK);
        g.drawLine(w, h, 0, 0);
    }
}
