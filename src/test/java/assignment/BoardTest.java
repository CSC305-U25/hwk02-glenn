package assignment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

/**
 * Test class for the Board component.
 * Verifies that the Board correctly displays the expected number of squares
 * when files are fetched from specific GitHub repository folders.
 * Each test checks the number of files fetched and ensures the Board grid
 * layout can accommodate all files.
 * 
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 4.0
 */

public class BoardTest {
    @Test
    public void testBoard_KappaFolder_Shows10Squares() throws Exception {
        String url = "https://github.com/CSC3100/App-Paint/tree/main/src/main/java/javiergs/paint/kappa";
        ArrayList<Square> squares = FileHandler.fetchFromGithub(url);
        assertEquals(10, squares.size(), "Expected 10 files in kappa folder");

        Board board = new Board();
        board.setSquare(squares);

        assertEquals(10, squares.size(), "Board should have 10 squares for kappa folder");
        assertTrue(board.getRows() > 0 && board.getCols() > 0, "Rows and columns should be positive");
        assertTrue(board.getRows() * board.getCols() >= 10, "Grid should fit all squares");
    }

    @Test
    public void testBoard_AssignmentFolder_Shows5Squares() throws Exception {
        String url = "https://github.com/CSC305-U25/hwk02-glenn/tree/main/src/main/java/assignment";
        ArrayList<Square> squares = FileHandler.fetchFromGithub(url);
        assertEquals(5, squares.size(), "Expected 5 files in assignment folder");

        Board board = new Board();
        board.setSquare(squares);

        assertEquals(5, squares.size(), "Board should have 5 squares for assignment folder");
        assertTrue(board.getRows() > 0 && board.getCols() > 0, "Rows and columns should be positive");
        assertTrue(board.getRows() * board.getCols() >= 5, "Grid should fit all squares");
    }
}
