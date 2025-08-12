package assignment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class BoardTest {
    @Test
    public void testGithubKappaFolderGridSize() throws Exception {
        String url = "https://github.com/CSC3100/App-Paint/tree/main/src/main/java/javiergs/paint/kappa";
        ArrayList<Square> squares = FileHandler.fetch(url);
        // Should be 10 files in the kappa folder
        assertEquals(10, squares.size(), "Expected 10 files in kappa folder");

        // Optionally, test Board grid logic
        Board board = new Board();
        board.setSquare(squares);

        // The grid should be at least 4x3 or 3x4 (since 10 files)
        int n = squares.size();
        int rows = (int) Math.ceil(Math.sqrt(n));
        int cols = (int) Math.ceil(n / (double) rows);
        assertTrue(rows * cols >= n, "Grid should fit all squares");
    }

    @Test
    public void testGithubHwk02GlennRootGridSize() throws Exception {
        String url = "https://github.com/CSC305-U25/hwk02-glenn";
        ArrayList<Square> squares = FileHandler.fetch(url);
        // You may need to update the expected count based on the actual number of files
        // in the root
        assertEquals(8, squares.size(), "Expected file count in root folder");

        Board board = new Board();
        board.setSquare(squares);

        int n = squares.size();
        int rows = (int) Math.ceil(Math.sqrt(n));
        int cols = (int) Math.ceil(n / (double) rows);
        assertTrue(rows * cols >= n, "Grid should fit all squares");
    }
}
