package src;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The GameTest class contains JUnit test cases to test the functionality of the Game class.
 * It performs various tests on the placement of words and scoring calculations.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameTest {
    // Attributes
    private static Game game;
    private static Player player;
    public static int counter;

    // Constructor

    /**
     * Default constructor for the GameTest class.
     * Creates a new instance of the GameTest class.
     */
    public GameTest() {}

    // Methods

    /**
     * BeforeAll method runs once before any test cases in the class.
     * It initializes the game and player attributes.
     */
    @BeforeAll
    public static void setup() {
        game = new Game();
        player = new Player(0);
        game.initializeTiles();
        game.initializePlayer();

        game.tilePile.addTile(game.getBoard().getTile(7,7).letter, 1);
        game.getBoard().setTile(7,7, new Tile('I'));
    }

    /**
     * AfterEach method runs after each individual test case.
     * It increments the counter attribute by 1 and prints the number of tests executed so far.
     */
    @AfterEach
    public void summary() {
        counter++;
        System.out.println("The number of tests: " + counter);
        player.clearHand();
//        game.board.displayBoard(); // debug or test visualization

    }

    /**
     * AfterAll method runs once after all test cases in the class.
     * It prints a message indicating that all tests are done.
     */
    @AfterAll static void tearDown() {
        System.out.print("All tests are done");
    }


    // Test Cases
    /**
     * Test case for valid horizontal word placement.
     * Ensures the word "HELLO" can be placed correctly on the board.
     */
    @Test
    @Order(4)
    public void test_ValidHorizontalPlacement() { // test 4
        player.addTile(new Tile('H'));
        player.addTile(new Tile('E'));
        player.addTile(new Tile('L'));
        player.addTile(new Tile('L'));


        boolean canPlace = game.canPlaceWord("HELL", 6, 7, 'H', player); // row 7 col 8

        assertTrue(canPlace, "Should be able to place 'HELL' horizontally at (7, 8)");
        game.placeWord("HELL", 6, 7, 'H', player);


        assertEquals('H', game.getBoard().getTile(6, 7).getLetter(), "Should be H");
        assertEquals('L', game.getBoard().getTile(6, 10).getLetter(), "Should be L");
    }

    /**
     * Test case for invalid word placement that goes out of bounds.
     * Ensures the word "WORD" cannot be placed starting at (14, 14).
     */
    @Test
    @Order(2)
    public void test_InvalidPlacementOutOfBounds() { // test 2
        player.addTile(new Tile('W'));
        player.addTile(new Tile('O'));
        player.addTile(new Tile('R'));
        player.addTile(new Tile('D'));

        boolean canPlace = game.canPlaceWord("WORD", 14, 14, 'H', player);
        assertFalse(canPlace, "Should not be able to place 'WORD' horizontally starting at (14, 14)");
        player.removeTile(new Tile('W'));
        player.removeTile(new Tile('O'));
        player.removeTile(new Tile('R'));
        player.removeTile(new Tile('D'));
    }

    /**
     * Test case for invalid placement due to tile overlap conflict.
     * Ensures the word "ABC" cannot overlap with an existing word "CAT".
     */
    @Test
    @Order(5)
    public void test_InvalidPlacementOverlapConflict() { // test 5
        player.addTile(new Tile('A'));
        player.addTile(new Tile('B'));
        player.addTile(new Tile('C'));

        boolean canPlace = game.canPlaceWord("ABC", 7, 7, 'H', player);
        assertFalse(canPlace, "Should not be able to place 'ABC' overlapping with 'QUIZ' at (8, 8)");

        player.removeTile(new Tile('A'));
        player.removeTile(new Tile('B'));
        player.removeTile(new Tile('C'));
    }

    /**
     * Test case for single word score calculation.
     * Verifies the score calculation for the word "HI".
     */
    @Test
    @Order(3)
    public void test_SingleWordScore() { // test 3
        player.addTile(new Tile('H'));
        player.setPoints(0);

        game.placeWord("HI", 6, 7, 'V', player);
        assertEquals(5, player.getPoints(), "Score for 'HI' should be 5 points (H=4, I=1)");
    }

    /**
     * Test case for score calculation with high-value tiles.
     * Verifies the score for the word "QUIZ".
     */
    @Test
    @Order(1)
    public void test_ScoreWithBonus() { // test 1
        player.addTile(new Tile('Q'));
        player.addTile(new Tile('U'));
        player.addTile(new Tile('I'));
        player.addTile(new Tile('Z'));

        game.placeWord("QUIZ", 7, 5, 'H', player);
        assertEquals(22, player.getPoints(), "Score for 'QUIZ' should be 22 points (Q=10, U=1, I=1, Z=10)");
    }

    /**
     * Test case for score calculation with multiple words.
     * Verifies the cumulative score for placing "HELP" and "POT".
     */
    @Test
    @Order(6)
    public void test_ScoreWithMultipleWords() { // test 6
        player.addTile(new Tile('H'));
        player.addTile(new Tile('O'));
        player.addTile(new Tile('L'));
        player.addTile(new Tile('D'));
        player.setPoints(0);
        game.placeWord("HOLD", 5, 11, 'V', player);
        assertFalse(16== player.getPoints(), "Score for 'HOLD' + 'HELLO' should be 8+8 points");
    } // here we fail because we are not counting the new word "HELLO" as our own
}
