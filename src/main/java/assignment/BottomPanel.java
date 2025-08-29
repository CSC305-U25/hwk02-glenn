package assignment;

import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JPanel {
    public BottomPanel(JTextField selectedField) {
        super(new BorderLayout(8, 8));
        Color bg = UIManager.getColor("Panel.background");
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 12, 8, 12)));
        setBackground(bg);

        JLabel selectedTitle = new JLabel("Error:");
        selectedTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        selectedField.setEditable(false);
        selectedField.setBackground(Color.WHITE);
        selectedField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 0, 0, 0)));

        JPanel bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.add(selectedField, BorderLayout.CENTER);
        bottomWrap.add(selectedTitle, BorderLayout.WEST);
        bottomWrap.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(bottomWrap, BorderLayout.CENTER);
    }
}
