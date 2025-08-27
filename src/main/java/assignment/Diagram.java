package assignment;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List; import java.util.ArrayList;
import javax.swing.*;
import javax.imageio.ImageIO;


public class Diagram extends JPanel{
    private BufferedImage image;

    public Diagram(Blackboard blackboard) {
        Color bg = UIManager.getColor("Panel.background");
        setBackground(bg);
        updateDiagram(blackboard.getFiles(),
                        blackboard.getRelations(),
                        blackboard);
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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        } else {
            g.setColor(UIManager.getColor("Panel.background"));
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
