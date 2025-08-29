package assignment;

import javax.swing.*;
import java.awt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
public class BottomPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(BottomPanel.class);
    private final JTextField field;

    public BottomPanel(JTextField selectedField) {
        super(new BorderLayout(8, 8));
        this.field = selectedField;
        Logger logger = LoggerFactory.getLogger(BottomPanel.class);
        Color bg = UIManager.getColor("Panel.background");
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 12, 8, 12)));
        setBackground(bg);

        JLabel selectedTitle = new JLabel("Error:");
        field.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        field.setEditable(false);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 0, 0, 0)));

        JPanel bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.add(selectedField, BorderLayout.CENTER);
        bottomWrap.add(selectedTitle, BorderLayout.WEST);
        bottomWrap.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        add(bottomWrap, BorderLayout.CENTER);
    }
    private void installErrorLogSink() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }
}
