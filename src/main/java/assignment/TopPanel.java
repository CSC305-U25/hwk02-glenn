package assignment;

import javax.swing.*;
import java.awt.*;

public class TopPanel extends JPanel {
    private final JTextField urlField = new JTextField();
    private final JButton okButton = new JButton("OK");

    public TopPanel( ) {
        super(new BorderLayout(8, 8));

        Color bg = UIManager.getColor("Panel.background");
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(8, 12, 0, 12)));
        setBackground(bg);

        JLabel urlLabel = new JLabel("GitHub Folder URL");
        urlLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JPanel urlRow = new JPanel(new BorderLayout(4, 0));
        urlRow.add(urlField, BorderLayout.CENTER);
        urlRow.add(okButton, BorderLayout.EAST);

        JPanel urlWrap = new JPanel(new BorderLayout());
        urlWrap.add(urlLabel, BorderLayout.NORTH);
        urlWrap.add(urlRow, BorderLayout.CENTER);
        urlWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        add(urlWrap, BorderLayout.CENTER);
    }
    public JTextField getUrlField()   { return urlField; }
    public JButton   getOkButton()    { return okButton; }
}
