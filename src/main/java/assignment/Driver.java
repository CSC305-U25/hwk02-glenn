package assignment;

import java.awt.*;

import javax.swing.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Entry point for the File Grid application.
 * Launches the main application window for visualizing files from a GitHub
 * repository as a grid of colored squares and relations.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Driver extends JFrame {

    public Driver() {
        super("Assignment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        Logger logger = LoggerFactory.getLogger(Driver.class);

        Blackboard blackboard = new Blackboard();
        FileHandler fileHandler = new FileHandler(blackboard);

        TopPanel topPanel = new TopPanel();
        add(topPanel, BorderLayout.NORTH);

        JTextField selectedField = new JTextField();
        MainPanel mainPanel = new MainPanel(blackboard, selectedField);
        add(mainPanel, BorderLayout.CENTER);

        BottomPanel bottomPanel = new BottomPanel(selectedField);
        add(bottomPanel, BorderLayout.SOUTH);

        GithubLoader.attach(topPanel.getOkButton(), topPanel.getUrlField(), fileHandler);


        setSize(1100, 800);
        setLocationRelativeTo(null);
        setVisible(true);

        logger.info("UI initialized");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Driver::new);
    }
}
