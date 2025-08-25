package assignment;

/**
 * Entry point for the File Grid application.
 * Launches the main application window for visualizing files from a GitHub
 * repository as a grid of colored squares and relations.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 5.0
 */

public class Driver{
    public static void main(String[] args) {
        Thread t = new Thread(new Frame());
        t.start();
    }
}
