package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Stack;

/**
 * The Controller class manages user interactions and game logic for Scrabble. It listens to user actions, updates the view,
 * and modifies the model accordingly.
 */
public abstract class Controller implements ActionListener {
    private static Game model;
    // The game model representing the state and logic of the game.

    private Stack<byte[]> undoStack = new Stack<>();
    private Stack<byte[]> redoStack = new Stack<>();

    private static View view; // The game view that displays the board and UI components.
    CustomButton storedButton; // Stores the button selected by the player.

    /**
     * Constructor for the {@code Controller} class.
     * Initializes the game model, view, and action listeners for all UI components.
     */
    public Controller() {
        model = new Game();
        this.view = model.getView();

        view.getVerticalButton().addActionListener(e->verticleButton());
        view.getHorizontalButton().addActionListener(e->horizontalButton());
        CustomButton[][] button = view.getButtons();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                button[row][col].addActionListener(this::clickedBoard);
            }
        }
        view.getSubmit().addActionListener(this::submitButton);
        view.getSkip().addActionListener(this::skip);
        view.getAi_turn().addActionListener(this::ai_turn);
        for (int i = 0; i < 7; i++) {
            view.getHandButtons()[i].addActionListener(this::handButton);
        }
        view.getUndoMenuItem().addActionListener(e-> undoButton());
        view.getRedoMenuItem().addActionListener(e->redoButton());
        view.getHandPanel().revalidate();
        view.getHandPanel().repaint();
//        view.initializeScoreboard(model.player);
        view.refreshHandPanel(false);
    }

    /**
     * Handles the "Vertical" button click event.
     * Enables vertical word placement mode and disables further direction changes.
     */
    private void verticleButton(){
        view.setVertical(true);
        view.getVerticalButton().setEnabled(false); // Disable after selecting vertical
        view.getHorizontalButton().setEnabled(false); // Disable horizontal as well
        view.setDirection('V');
        view.updateEnabledTiles();
        view.refreshHandPanel(true);
    }

    /**
     * Handles the "Horizontal" button click event.
     * Enables horizontal word placement mode and disables further direction changes.
     */
    private void horizontalButton() {
        view.setVertical(false);
        view.getVerticalButton().setEnabled(false); // Disable after selecting horizontal
        view.getHorizontalButton().setEnabled(false); // Disable vertical as well
        view.setDirection('H');
        view.updateEnabledTiles();
        view.refreshHandPanel(true);
    }

    /**
     * Handles the player's hand button click event.
     * Stores the selected tile and updates the hand and board buttons accordingly.
     *
     * @param e the action event triggered by clicking a hand button.
     */
    public void handButton (ActionEvent e){
        CustomButton button = (CustomButton) e.getSource();
        storedButton = (CustomButton) e.getSource();
        view.setSelectedTile(new Tile(button.getText().charAt(0)));// Store the selected tile
        if(view.getSelectedTile().getLetter() == '*'){
            blankSelector();
            return;
        }
        if (view.getBeforeStart()) {
            view.enableButtons();
        } else {
            //view.disableButtons();
            view.updateEnabledTiles();
        }
        view.setBeforeStart(false);
        view.updateHandPanel();
    }

    /**
     * Handles the board button click event.
     * Places the selected tile on the board and updates the game state.
     *
     * @param e the action event triggered by clicking a board button.
     */
    public void clickedBoard (ActionEvent e) {
        CustomButton clickedButton = (CustomButton) e.getSource();
        CustomButton[][] button = view.getButtons();
        int row = clickedButton.getRow();
        int col = clickedButton.getCol();
        if (view.getSelectedTile() != null) {
            view.setClickedRow(clickedButton.getRow());
            view.setClickedCol(clickedButton.getCol());
            button[view.getClickedRow()][view.getClickedCol()].setText(String.valueOf(view.getSelectedTile().getLetter()));
            view.addTilePlacedThisTurn(row,col);
            if (view.getFirstLetter()) { // if this is the first letter added
                view.setTargetRow(clickedButton.getRow());
                view.setTargetCol(clickedButton.getCol());
                if (view.getVertical()) {
                    if (model.getBoard().getTile(view.getTargetRow() - 1, view.getTargetCol()).getLetter() != ' ') {
                        String temp = Character.toString(view.getSelectedTile().getLetter());
                        view.setTargetRow(view.getTargetRow() - 1);
                        view.setInputWord(model.getBoard().getTile(view.getTargetRow(), view.getTargetCol()).getLetter() + temp);
                    }else {
                        view.addInputWord(view.getSelectedTile().getLetter());

                    }
                } else {
                    if (model.getBoard().getTile(view.getTargetRow(), view.getTargetCol() - 1).getLetter() != ' ') {
                        String temp = Character.toString(view.getSelectedTile().getLetter());
                        view.setTargetCol(view.getTargetCol() - 1);
                        view.setInputWord(model.getBoard().getTile(view.getTargetRow(), view.getTargetCol()).getLetter() + temp);
                    }else {
                        view.addInputWord(view.getSelectedTile().getLetter());
                    }
                }
                view.setFirstLetter(false);
            } else {
                view.addInputWord(view.getSelectedTile().getLetter());
            }
            view.setSelectedTile(null);
            //view.updateHandPanel();
            view.disableButtons();
            storedButton.setEnabled(false);
        }
    }

    /**
     * Handles the "Submit" button click event.
     * Validates the word placement, updates the board, and transitions to the next player.
     *
     * @param e the action event triggered by clicking the submit button.
     */
    public void submitButton(ActionEvent e) {
        Game currentState = model;
        view.addState(currentState);
        saveState();
        boolean isTouchingExistingLetter = false;

        // Check adjacency for each newly placed tile
        for (Point p : view.getTilesPlacedThisTurn()) {
            int row = p.x;
            int col = p.y;

            // Check adjacent tiles
            if ((row > 0 && model.getBoard().getTile(row - 1, col).getLetter() != ' ') || // Above
                    (row < 14 && model.getBoard().getTile(row + 1, col).getLetter() != ' ') || // Below
                    (col > 0 && model.getBoard().getTile(row, col - 1).getLetter() != ' ') || // Left
                    (col < 14 && model.getBoard().getTile(row, col + 1).getLetter() != ' ')) { // Right
                isTouchingExistingLetter = true;
                break;
            }
        }
        if (!view.getVertical()) {
            while (model.getBoard().getTile(view.getTargetRow(), view.getTargetCol() + view.getInputWord().length()).getLetter() != ' ') {
                view.addInputWord(model.getBoard().getTile(view.getTargetRow(), view.getTargetCol() + view.getInputWord().length()).getLetter());
            }
        } else {
            while (model.getBoard().getTile(view.getTargetRow() + view.getInputWord().length(), view.getTargetCol()).getLetter() != ' ') {
                view.addInputWord(model.getBoard().getTile(view.getTargetRow() + view.getInputWord().length(), view.getTargetCol()).getLetter());
            }
        }

        if (isTouchingExistingLetter) {
            if (model.play(view.getInputWord().toLowerCase(), view.getDirection(), view.getTargetRow(), view.getTargetCol())) {
                System.out.println("Input word " + view.getInputWord() + " Row: " + view.getTargetRow() + " Col: " + view.getTargetCol() + " Dir:" + view.getDirection());
                JOptionPane.showMessageDialog(view.getFrame(), "Submitted word: " + view.getInputWord() + "\nIt is now " + model.getCurrentPlayer().getName() + "'s turn. They have " + model.getCurrentPlayer().getPoints() + " points.");

                showScores(); // Show player scores here
            } else {
                JOptionPane.showMessageDialog(view.getFrame(), "Tried to submit word: " + view.getInputWord() + "\nInvalid word. Please try again.");
            }
        } else {
            JOptionPane.showMessageDialog(view.getFrame(), "Word must touch an existing letter on the board!");
        }

        view.getHorizontalButton().setEnabled(true);
        view.getVerticalButton().setEnabled(true);
        if(view.getVertical()){
            view.setDirection('V');
        }else{
            view.setDirection('H');
        }

        view.updateHandPanel();
        view.setFirstLetter(true);
        view.setBeforeStart(true);
        view.updateView();
        view.setInputWord("");
//        view.updateScoreboard(model.player);
        view.refreshHandPanel(false);
    }

    /**
     * Handles the "Skip" button click event.
     * Moves to the next player's turn and refreshes the game view.
     *
     * @param e the action event triggered by clicking the "Skip" button.
     */
    public void skip(ActionEvent e) {
        Game currentState = model;
        saveState();
        view.addState(currentState);
        model.nextPlayer();
        view.updateHandPanel();
        view.setBeforeStart(true);
        view.setInputWord("");
        view.getHorizontalButton().setEnabled(true);
        view.getVerticalButton().setEnabled(true);
        view.updateHandPanel();
        view.updateView();
//        view.updateScoreboard(model.player);

        JOptionPane.showMessageDialog(view.getFrame(), "Skipping turn. It is now " + model.getCurrentPlayer().getName() + "'s turn. They have " + model.getCurrentPlayer().getPoints() + " points.");

        showScores(); // Shows player scores

        view.refreshHandPanel(false);
    }

    /**
     * Handles the "AI Turn" button click event.
     * Allows the AI to make a move, validates the move, and updates the game view.
     *
     * @param e the action event triggered by clicking the "AI Turn" button.
     */
    public void ai_turn(ActionEvent e){
        Game currentState = model;
        view.addState(currentState);
        saveState();
        model.aiPlay();

        boolean isTouchingExistingLetter = false;

        // Check adjacency for each newly placed tile
        for (Point p : view.getTilesPlacedThisTurn()) {
            int row = p.x;
            int col = p.y;

            // Check adjacent tiles
            if ((row > 0 && model.getBoard().getTile(row - 1, col).getLetter() != ' ') || // Above
                    (row < 14 && model.getBoard().getTile(row + 1, col).getLetter() != ' ') || // Below
                    (col > 0 && model.getBoard().getTile(row, col - 1).getLetter() != ' ') || // Left
                    (col < 14 && model.getBoard().getTile(row, col + 1).getLetter() != ' ')) { // Right
                isTouchingExistingLetter = true;
                break;
            }
        }
        if (!view.getVertical()) {
            while (model.getBoard().getTile(view.getTargetRow(), view.getTargetCol() + view.getInputWord().length()).getLetter() != ' ') {
                view.addInputWord(model.getBoard().getTile(view.getTargetRow(), view.getTargetCol() + view.getInputWord().length()).getLetter());
            }
        } else {
            while (model.getBoard().getTile(view.getTargetRow() + view.getInputWord().length(), view.getTargetCol()).getLetter() != ' ') {
                view.addInputWord(model.getBoard().getTile(view.getTargetRow() + view.getInputWord().length(), view.getTargetCol()).getLetter());
            }
        }

        if (isTouchingExistingLetter) {
            if (model.play(view.getInputWord().toLowerCase(), view.getDirection(), view.getTargetRow(), view.getTargetCol())) {
                System.out.println("Input word " + view.getInputWord() + " Row: " + view.getTargetRow() + " Col: " + view.getTargetCol() + " Dir:" + view.getDirection());
                JOptionPane.showMessageDialog(view.getFrame(), "Submitted word: " + view.getInputWord() + "\nIt is now " + model.getCurrentPlayer().getName() + "'s turn. They have " + model.getCurrentPlayer().getPoints() + " points.");

                showScores(); // Show player scores here
            } else {
                JOptionPane.showMessageDialog(view.getFrame(), "Tried to submit word: " + view.getInputWord() + "\nInvalid word. Please try again.");
            }
        }

        view.getHorizontalButton().setEnabled(true);
        view.getVerticalButton().setEnabled(true);
        if(view.getVertical()){
            view.setDirection('V');
        }else{
            view.setDirection('H');
        }
        showScores();

        view.updateHandPanel();
        view.setFirstLetter(true);
        view.setBeforeStart(true);
        view.updateView();
        view.setInputWord("");
//        view.updateScoreboard(model.player);
        view.refreshHandPanel(false);

    }

    private void undoButton(){
        if (!undoStack.isEmpty()) {
            redoStack.push(serializeCurrentState()); // Save current state for redo
            byte[] previousState = undoStack.pop();
            model.getBoard().displayBoard();
            restoreState(previousState);
            model.getBoard().displayBoard();
            view.updateView();
        } else {
            System.out.println("No undo available!");
        }
        view.updateView();
    }

    private void redoButton(){
        if (!redoStack.isEmpty()) {
            undoStack.push(serializeCurrentState()); // Save current state for undo
            byte[] nextState = redoStack.pop();
            restoreState(nextState);
        } else {
            System.out.println("No redo available!");
        }
        view.updateView();
    }
    /**
     * Displays a dialog box for selecting a replacement letter when a blank tile is used.
     */
    public static void blankSelector() {
        // create the temporary frame
        JFrame frame = new JFrame("Which letter would you like?");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // panel to hold the letter selection buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 6, 10, 10)); // 5 rows x 6 columns with gaps

        // Create an ActionListener to handle button clicks
        ActionListener buttonClickListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource(); // get the clicked button
                String letter = source.getText();         // get the button text
                model.getCurrentPlayer().removeTile(new Tile('*')); // remove the blank
                Tile temp = new Tile(letter.charAt(0)); // add the new tile
                temp.setPoints(0); // blank tiles are still worth 0
                model.getCurrentPlayer().addTile(temp);
                view.updateHandPanel();
                frame.dispose();
            }
        };

        // create buttons A-Z and add them to the temporary panel
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            JButton letterButton = new JButton(String.valueOf(letter));
            letterButton.addActionListener(buttonClickListener); // use the above listener
            buttonPanel.add(letterButton); // add to temp panel
        }

        // add the panel to the frame
        frame.add(buttonPanel);

        // display the frame
        frame.setLocationRelativeTo(null); // center on screen
        frame.setVisible(true);
    }

    /**
     * Display the current scores of all players in a message box.
     */
    private void showScores() {
        StringBuilder scoreMessage = new StringBuilder("Current Scores:\n");
        for (Player player : model.player) {
            scoreMessage.append(player.getName())
                    .append(": ")
                    .append(player.getPoints())
                    .append(" points\n");
        }
        JOptionPane.showMessageDialog(view.getFrame(), scoreMessage.toString(), "Player Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveState() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(model);
            undoStack.push(baos.toByteArray()); // Save serialized state
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restoreState(byte[] state) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(state);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            model = (Game) ois.readObject();
            view.updateView(); // Update the view to reflect the restored state
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private byte[] serializeCurrentState() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(model);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Controller controller = new Controller() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("g");
            }
        };
    }


}
