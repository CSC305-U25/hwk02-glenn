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

        // Optional: smoother edges
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                              RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // Para green background
        Color paraGreen = new Color(0, 204, 102);
        g.setColor(paraGreen);
        g.fillRect(0, 0, getWidth(), getHeight());

        int w = getWidth();
        int h = getHeight();
        int r = Math.min(w, h) / 4; // pick any radius you like

        // ---- Quarter circles that hug the corners ----
        // Bottom-left: center at (0, h) → use bounding box (cx - r, cy - r, 2r, 2r)
        g.setColor(Color.WHITE);
        g.fillArc(-r, h - r, 2 * r, 2 * r, 0, 90);

        // Top-right: center at (w, 0)
        g.fillArc(w - r, -r, 2 * r, 2 * r, 180, 90);

        // Diagonal line from bottom right to top left
        g.setColor(Color.BLACK);
        g.drawLine(w, h, 0, 0);
    }
}
