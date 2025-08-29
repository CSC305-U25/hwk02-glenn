package assignment;

import java.awt.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Computes and renders points for the "Abstractness vs Instability" metric
 * plots.
 * Only relations between classes contribute to in/out degrees.
 * Used by the Metrics panel for visualizing design quality.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public class MetricsPoints {
    private static final Logger logger = LoggerFactory.getLogger(MetricsPoints.class);

    private MetricsPoints() {
    }

    public static Map<String, double[]> build(Blackboard bb) {
        Set<String> allowed = new LinkedHashSet<>();
        for (FileInfo f : bb.getJavaFiles())
            allowed.add(Names.baseName(f.name));
        if (allowed.isEmpty()) {
            logger.debug("no allowed classes (No java files)");
            return Collections.emptyMap();
        }
        Map<String, Double> A = new HashMap<>();
        for (ClassDesc cd : bb.getClasses()) {
            if (allowed.contains(cd.name)) {
                A.put(cd.name, (cd.isInterface || cd.isAbstract) ? 1.0 : 0.0);
            }
        }

        Map<String, int[]> degrees = new HashMap<>();
        for (String c : allowed)
            degrees.put(c, new int[2]);
        for (Relation r : bb.getRelations()) {
            if (allowed.contains(r.src) && allowed.contains(r.dst)) {
                degrees.get(r.src)[0]++;
                degrees.get(r.dst)[1]++;
            }
        }

        Map<String, double[]> out = new LinkedHashMap<>();
        for (String c : allowed) {
            int outD = degrees.get(c)[0], inD = degrees.get(c)[1];
            double instability = (inD + outD) == 0 ? 0.0 : (double) outD / (inD + outD);
            double abstractness = A.getOrDefault(c, 0.0);

            out.put(c, new double[] { instability, abstractness });
        }
        logger.debug("Produced {} points (allowed={} clases)", out.size(), allowed.size());
        return out;
    }

    public static void draw(Graphics g, Rectangle plot, Map<String, double[]> pts, int dotR) {
        g.setFont(g.getFont().deriveFont(11f));
        for (Map.Entry<String, double[]> e : pts.entrySet()) {
            String name = e.getKey();
            double instability = e.getValue()[0];
            double abstractness = e.getValue()[1];

            int x = plot.x + (int) Math.round(instability * plot.width);
            int y = plot.y + (int) Math.round((1.0 - abstractness) * plot.height);

            g.setColor(Color.BLUE);
            g.fillOval(x - dotR, y - dotR, dotR * 2, dotR * 2);
            g.setColor(Color.BLACK);
            g.drawString(name, x + 6, y - 6);
            logger.trace("Draw '{}' at ({}, {}) from instability={}, abstractness={}",
                    name, x, y, instability, abstractness);
        }
    }
}
