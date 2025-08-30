package assignment;

import java.awt.event.ActionListener;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for attaching GitHub repository loading behavior to UI
 * controls.
 * Provides an ActionListener to a button and text field to allow the user to
 * fetch files from a GitHub URL and update the application model.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */
public final class GithubLoader {
    public GithubLoader() {
    }

    public static void attach(JButton okButton, JTextField urlField,
            FileHandler fileHandler) {
        Logger logger = LoggerFactory.getLogger(GithubLoader.class);
        ActionListener load = e -> {
            String url = urlField.getText().trim();
            if (url.isEmpty()) {
                JOptionPane.showMessageDialog(okButton, "Please enter a GitHub url.");
                return;
            }
            okButton.setEnabled(false);
            urlField.setEnabled(false);
            logger.info("Fetch from Github: {}", url);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    fileHandler.fetchFromGithub(url);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        logger.info("Fetch complete");
                    } catch (Exception ex) {
                        logger.error("Failed to fetch repositoy: {}", url, ex);
                    } finally {
                        okButton.setEnabled(true);
                        urlField.setEnabled(true);
                    }
                }
            }.execute();
        };

        okButton.addActionListener(load);
        urlField.addActionListener(load);
    }
}
