package assignment;

import javax.swing.JPanel;
import java.awt.*;
import java.util.List;

public class Metrics extends JPanel {
    private final Blackboard blackboard;

    public Metrics(Blackboard blackboard) {
        this.blackboard = blackboard;
        setOpaque(true);
        blackboard.addObserver(bb -> repaint());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color paraGreen = new Color(160, 248, 172);
        g2.setColor(paraGreen);
        g2.fillRect(0, 0, getWidth(), getHeight());

        int w = getWidth();
        int h = getHeight();

        int x0 = 40, y0 = h - 40;
        int x1 = w - 10, y1 = 10;

        g2.setColor(Color.BLACK);
        g2.drawLine(x0, y0, x1, y0);
        g2.drawLine(x0, y0, x0, y1);
        g2.drawLine(x0, y1, x1, y1);
        g2.drawLine(x1, y1, x1, y0);

        g2.setColor(Color.BLACK);
        g2.drawString("Instability", (x0 + x1) / 2, h - 12);
        g2.drawString("Abstractness", 6, (y0 + y1) / 2);

        Shape oldClip = g2.getClip();
        Rectangle plot = new Rectangle(x0, y1, (x1 - x0), (y0 - y1));
        g2.setClip(plot);
        int r = Math.min(w, h) / 3;

        g2.setColor(Color.WHITE);
        g2.drawLine(x0, y1, x1, y0);
        g2.fillOval(x0 - r, y0 - r, 2 * r, 2 * r);
        g2.fillOval(x1 - r, y1 - r, 2 * r, 2 * r);
        g2.setColor(Color.BLACK);
        g2.drawString("Useless", x1 - 90, y1 + 90);
        g2.drawString("Painful", x0 + 70, y0 - 90);
        g2.setClip(oldClip);

        // Files
        List<FileInfo> files = blackboard.getFiles();
        List<ClassDesc> classes = blackboard.getClasses();

        java.util.Map<String, Double> abstractnessMap = new java.util.HashMap<>();
        for (FileInfo file : files) {
            String base = Names.baseName(file.name);
            boolean isAbs = false;
            for (ClassDesc cd : classes) {
                if (cd.name.equals(base) && (cd.isInterface || cd.isAbstract)) {
                    isAbs = true;
                    break;
                }
            }
            abstractnessMap.put(base, isAbs ? 1.0 : 0.0);
        }

        for (FileInfo file : files) {
            String base = Names.baseName(file.name);
            int in = blackboard.getIncomingRelationCount(file.name);
            int out = blackboard.getOutgoingRelationCount(file.name);
            double instability = (in + out) == 0 ? 0 : (double) out / (in + out);
            double abstractness = abstractnessMap.getOrDefault(base, 0.0);

            int x = (int) (x0 + instability * (x1 - x0));
            int y = (int) (y0 - abstractness * (y0 - y1));

            g2.setColor(Color.BLUE);
            g2.fillOval(x - 4, y - 4, 8, 8);

            g2.setColor(Color.BLACK);
            g2.drawString(base, x + 6, y - 6);
        }
    }
}
