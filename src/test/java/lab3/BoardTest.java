package lab3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test class for Board's createBoard method.
 * 
 * @author Glenn Anciado
 * @author Oscar Chau
 * @version 2.0
 */
public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    /**
     * Test createBoard with a single file.
     * Should create a 1x1 grid.
     */
    @Test
    void testCreateBoardSingleFile() {
        String[] fileNames = { "test.txt" };
        board.createBoard(fileNames);

        assertEquals(1, board.getRows(), "Should have 1 row for single file");
        assertEquals(1, board.getCols(), "Should have 1 column for single file");

        Square square = board.getSquare(0, 0);
        assertNotNull(square, "Square should exist at position (0,0)");
        assertEquals("test.txt", square.getFileName(), "Square should contain correct filename");
        assertEquals("#FFFFFF", square.getColor(), "Square should have default white color");
    }

    /**
     * Test createBoard with 4 files.
     * Should create a 2x2 grid.
     */
    @Test
    void testCreateBoardFourFiles() {
        String[] fileNames = { "file1.txt", "file2.txt", "file3.txt", "file4.txt" };
        board.createBoard(fileNames);

        assertEquals(2, board.getRows(), "Should have 2 rows for 4 files");
        assertEquals(2, board.getCols(), "Should have 2 columns for 4 files");

        // Test that all squares are created with correct filenames
        assertEquals("file1.txt", board.getSquare(0, 0).getFileName());
        assertEquals("file2.txt", board.getSquare(0, 1).getFileName());
        assertEquals("file3.txt", board.getSquare(1, 0).getFileName());
        assertEquals("file4.txt", board.getSquare(1, 1).getFileName());
    }

    /**
     * Test createBoard with 9 files.
     * Should create a 3x3 grid (perfect square).
     */
    @Test
    void testCreateBoardNineFiles() {
        String[] fileNames = { "1.txt", "2.txt", "3.txt", "4.txt", "5.txt", "6.txt", "7.txt", "8.txt", "9.txt" };
        board.createBoard(fileNames);

        assertEquals(3, board.getRows(), "Should have 3 rows for 9 files");
        assertEquals(3, board.getCols(), "Should have 3 columns for 9 files");

        // Test first, middle, and last squares
        assertEquals("1.txt", board.getSquare(0, 0).getFileName());
        assertEquals("5.txt", board.getSquare(1, 1).getFileName());
        assertEquals("9.txt", board.getSquare(2, 2).getFileName());
    }

    /**
     * Test createBoard with empty file array.
     * Should handle gracefully without errors.
     */
    @Test
    void testCreateBoardEmptyArray() {
        String[] fileNames = {};
        board.createBoard(fileNames);

        assertEquals(0, board.getRows(), "Should have 0 rows for empty array");
        assertEquals(1, board.getCols(), "Should have 1 column for empty array");
    }
}
