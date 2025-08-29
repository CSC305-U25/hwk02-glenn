package assignment;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.imageio.ImageIO;


public class Diagram extends JPanel implements PropertyChangeListener{
    private Blackboard bb;
    private BufferedImage image;

    public Diagram(Blackboard blackboard) {
        this.bb = blackboard;
        setBackground(UIManager.getColor("Panel.background"));
    }

    private static BufferedImage renderPng(String uml) {
        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            new SourceStringReader(uml).outputImage(os, new FileFormatOption(FileFormat.PNG));
            try(ByteArrayInputStream in = new ByteArrayInputStream(os.toByteArray())) {
                return ImageIO.read(in);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public final void updateDiagram(List<FileInfo> files,
                                List<Relation> relations,
                                Blackboard bb) {
        List<Square> squares = new ArrayList<>();
        for (FileInfo f : bb.getJavaFiles()) {
            squares.add(new Square(f.name, f.lines));
        }
        String uml = UmlBuilder.createUml(squares, relations);
        image = renderPng(uml);
        revalidate();
        repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        bb.addPropertyChangeListener(this);
        EventQueue.invokeLater(() -> {
            List<Square> squares = new ArrayList<>();
            for (FileInfo f : bb.getJavaFiles()) squares.add(new Square(f.name, f.lines));
            String uml = UmlBuilder.createUml(squares, bb.getRelations());
            image = renderPng(uml);
            revalidate();
            repaint();
        });
    }

    @Override
    public void removeNotify() {
        bb.removePropertyChangeListener(this);
        super.removeNotify();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String p = evt.getPropertyName();
        if (!"files".equals(p) && !"classes".equals(p) && !"relations".equals(p) && !"model".equals(p)) return;

        // Re-render on model changes (inline, no helper)
        EventQueue.invokeLater(() -> {
            List<Square> squares = new ArrayList<>();
            for (FileInfo f : bb.getJavaFiles()) squares.add(new Square(f.name, f.lines));
            String uml = UmlBuilder.createUml(squares, bb.getRelations());
            image = renderPng(uml);
            revalidate();
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        } else {
            g.setColor(Color.WHITE);
            g.drawString("No Diagram to Display.", 10, 20);
        }
    }
    @Override
    public Dimension getPreferredSize() {
        return (image != null)
                ? new Dimension(image.getWidth(), image.getHeight())
                : new Dimension(640, 480);
    }
}
