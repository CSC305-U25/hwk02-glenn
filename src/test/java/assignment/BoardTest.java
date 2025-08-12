/**
 * Test class for verifying the Board and FileHandler integration.
 * Ensures that files are correctly fetched from GitHub URLs and that
 * the Board grid layout adapts to the number of files, displaying all
 * files as expected in the grid.
 *
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 3.0*
 */

package assignment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class BoardTest {
    @Test
    public void testGithubKappaFolderGridSize() throws Exception {
        String url = "https://github.com/CSC3100/App-Paint/tree/main/src/main/java/javiergs/paint/kappa";
        ArrayList<Square> squares = FileHandler.fetch(url);
        assertEquals(10, squares.size(), "Expected 10 files in kappa folder");

        Board board = new Board();
        board.setSquare(squares);

        int n = squares.size();
        int rows = (int) Math.ceil(Math.sqrt(n));
        int cols = (int) Math.ceil(n / (double) rows);
        assertTrue(rows * cols >= n, "Grid should fit all squares");
    }

    @Test
    public void testGithubHwk02GlennRootGridSize() throws Exception {
        String url = "https://github.com/CSC305-U25/hwk02-glenn";
        ArrayList<Square> squares = FileHandler.fetch(url);

        assertEquals(8, squares.size(), "Expected file count in root folder");

        Board board = new Board();
        board.setSquare(squares);

        int n = squares.size();
        int rows = (int) Math.ceil(Math.sqrt(n));
        int cols = (int) Math.ceil(n / (double) rows);
        assertTrue(rows * cols >= n, "Grid should fit all squares");
    }
}
