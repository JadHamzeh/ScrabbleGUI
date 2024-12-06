package src;

import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The GameTest class contains JUnit test cases to test the functionality of the Game class.
 * It performs various tests on the placement of words and scoring calculations.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameTest {
    // Attributes
    private static Controller controller;
    private static Game game;
    private static View view;
    private static Board board;
    private static Player player;
    public static int counter;
    public Board tempBoard;

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
        controller = new Controller() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        game = controller.getModel();
        view = game.getView();
        player = game.getCurrentPlayer();
        while(!player.getHand().isEmpty()){
            player.removeTile(player.getHand().getFirst());
        }
        game.tilePile.addTile(game.getBoard().getTile(7,7).letter, 1);
        game.getBoard().setTile(7,7, new Tile('I'));
        game.getBoard().getTile(7,7).setBonus("0");
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
        System.out.println(controller.getModel().getCurrentPlayer().getName());
        controller.getModel().getBoard().displayBoard(); // debug or test visualization
    }

    /**
     * AfterAll method runs once after all test cases in the class.
     * It prints a message indicating that all tests are done.
     */
    @AfterAll static void tearDown() {
        System.out.print("All tests are done");
    }


    // Test Cases

    @Test
    @Order(1)
    public void test_chaosXML() {
        controller.getView().getChaosLayout().doClick();
        assertEquals(controller.getModel().getBoard().getPremiumLayout(), "src/premiumChaos.xml", "Should be chaos layout");
    }

    @Test
    @Order(2)
    public void test_ringXML() {
        controller.getView().getRingLayout().doClick();
        assertEquals(controller.getModel().getBoard().getPremiumLayout(), "src/premiumRing.xml", "Should be ring layout");
    }

    @Test
    @Order(3)
    public void test_defaultXML() {
        controller.getView().getDefaultLayout().doClick();
        assertEquals(controller.getModel().getBoard().getPremiumLayout(), "src/premiumDefault.xml", "Should be default layout");
    }
    /**
     * Test case for valid horizontal word placement.
     * Ensures the word "HELLO" can be placed correctly on the board.
     */
    @Test
    @Order(7)
    public void test_ValidHorizontalPlacement() { // test 4
        player.addTile(new Tile('E'));
        player.addTile(new Tile('L'));
        player.addTile(new Tile('L'));

        view.setTargetRow(6);
        view.setTargetCol(7);
        view.setInputWord("HELL");
        view.setDirection('H');
        view.addTilePlacedThisTurn(6,7);
        view.addTilePlacedThisTurn(6,8);
        view.addTilePlacedThisTurn(6,9);
        view.addTilePlacedThisTurn(6,10);

        controller.getView().getSubmit().setEnabled(true);
        controller.getView().getSubmit().doClick();
        controller.getView().clearTilesPlacedThisTurn();
        assertEquals('H', game.getBoard().getTile(6, 7).getLetter(), "Should be H");
        assertEquals('L', game.getBoard().getTile(6, 10).getLetter(), "Should be L");
        controller.getModel().prevPlayer();
    }

    /**
     * Test case for invalid word placement that goes out of bounds.
     * Ensures the word "WORD" cannot be placed starting at (14, 14).
     */
    @Test
    @Order(5)
    public void test_InvalidPlacementOutOfBounds() { // test 2
        player.addTile(new Tile('W'));
        player.addTile(new Tile('O'));
        player.addTile(new Tile('R'));
        player.addTile(new Tile('D'));

        controller.getView().setTargetRow(7);
        controller.getView().setTargetCol(5);
        controller.getView().setInputWord("WORD");
        controller.getView().setDirection('H');
        controller.getView().addTilePlacedThisTurn(7,5);

        controller.getView().getSubmit().setEnabled(true);
        String tempP = controller.getModel().getCurrentPlayer().getName();
        controller.getView().getSubmit().doClick();
        controller.getView().updateView();
        assertEquals(tempP, controller.getModel().getCurrentPlayer().getName(), "Should not be able to place 'WORD' horizontally starting at (14, 14)");
    }

    /**
     * Test case for invalid placement due to tile overlap conflict.
     * Ensures the word "ABC" cannot overlap with an existing word "CAT".
     */
    @Test
    @Order(8)
    public void test_InvalidPlacementOverlapConflict() { // test 5
        player.addTile(new Tile('A'));
        player.addTile(new Tile('B'));
        player.addTile(new Tile('C'));

        boolean canPlace = controller.getModel().canPlaceWord("ABC", 7, 7, 'H', player);
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
    @Order(6)
    public void test_SingleWordScore() { // test 3
        player.addTile(new Tile('H'));
        controller.getView().setTargetRow(6);
        controller.getView().setTargetCol(7);
        controller.getView().setInputWord("HI");
        controller.getView().setDirection('V');
        controller.getView().addTilePlacedThisTurn(6,7);
        controller.getView().addTilePlacedThisTurn(7,7);
        controller.getView().getSubmit().setEnabled(true);
        System.out.println(controller.getModel().getCurrentPlayer().getName());
        controller.getView().getSubmit().doClick();
        controller.getView().clearTilesPlacedThisTurn();
        assertEquals(27, player.getPoints(), "Score for 'HI' should be 5 + 22 points (H=4, I=1)");
        controller.getModel().prevPlayer();
    }

    /**
     * Test case for score calculation with high-value tiles.
     * Verifies the score for the word "QUIZ".
     */
    @Test
    @Order(4)
    public void test_ScoreWithBonus() { // test 1
        player.addTile(new Tile('Q'));
        player.addTile(new Tile('U'));
        player.addTile(new Tile('I'));
        player.addTile(new Tile('Z'));

        controller.getView().setTargetRow(7);
        controller.getView().setTargetCol(5);
        controller.getView().setInputWord("QUIZ");
        controller.getView().setDirection('H');
        controller.getView().addTilePlacedThisTurn(7,5);
        controller.getView().addTilePlacedThisTurn(7,6);
        controller.getView().addTilePlacedThisTurn(7,7);
        controller.getView().addTilePlacedThisTurn(7,8);
        controller.getView().getSubmit().setEnabled(true);
        controller.getView().getSubmit().doClick();
        controller.getView().updateView();
        assertEquals(22, player.getPoints(), "Score for 'QUIZ' should be 22 points (Q=10, U=1, I=1, Z=10)");
        controller.getModel().prevPlayer();
    }

    /**
     * Test case for score calculation with multiple words.
     * Verifies the cumulative score for placing "HOLD" and "HELLO".
     */
    @Test
    @Order(9)
    public void test_ScoreWithMultipleWordsANDBonus() { // test 6
        player.addTile(new Tile('H'));
        player.addTile(new Tile('O'));
        player.addTile(new Tile('L'));
        player.addTile(new Tile('D'));
        player.setPoints(0);

        controller.getView().setTargetRow(5);
        controller.getView().setTargetCol(11);
        controller.getView().setInputWord("HOLD");
        controller.getView().setDirection('V');
        controller.getView().addTilePlacedThisTurn(5,11);
        controller.getView().addTilePlacedThisTurn(6,11);
        controller.getView().addTilePlacedThisTurn(7,11);
        controller.getView().addTilePlacedThisTurn(8,11);
        controller.getView().getSubmit().setEnabled(true);
        controller.getView().getSubmit().doClick();
        controller.getView().updateView();
        controller.getModel().prevPlayer();
        assertTrue(18 == player.getPoints(), "Score for 'HOLD' + 'HELLO' should be 9+9 points"); // there are two DL scores hit by 1-point letters so 16 + 2
    }

    @Test
    @Order(10)
    public void test_Undo(){
        controller.undoButton(); // hit the undo button
        assertEquals(controller.getModel().getBoard().getTile(5,11).getLetter(), ' ',"Hold should not be on the board");
    }

    @Test
    @Order(11)
    public void test_Redo(){
        controller.redoButton(); // hit the redo button
        assertEquals(controller.getModel().getBoard().getTile(5,11).getLetter(), 'H',"Hold should be on the board");
    }
}
