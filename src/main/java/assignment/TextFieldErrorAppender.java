package assignment;

import javax.swing.*;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class TextFieldErrorAppender extends AppenderBase<ILoggingEvent> {
    private final JTextField field;

    public TextFieldErrorAppender(JTextField field) {
        this.field = field;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            String msg = event.getFormattedMessage();
            SwingUtilities.invokeLater(() -> field.setText(msg));
        }
    }
}
