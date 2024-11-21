package src;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Locale;
class View {


    private JPanel directionPanel;
    private int targetRow;
    private int targetCol;
    private boolean first_letter = true; // is the current player placing their first tile

    private boolean isVertical;

    private CustomButton [] displayHand;

    private char direction = 'H';
    private CustomButton[][] buttons;

    public CustomButton[][] getButtons() {
        return buttons;
    }

    private JPanel skipPannel;
    private JButton skip;
    private JButton submit;

    public JPanel getHandPanel() {
        return handPanel;
    }

    public String getInputWord() {
        return inputWord;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    private JPanel handPanel;
    private Game model;
    private Word check;
    private JFrame frame;



    int clickedRow; // Since 'row' is accessible in this scope
    int clickedCol;

    String inputWord = "";

    private boolean beforeStart = true;

    private Board board;
    private Tile selectedTile; // To store the selected tile from the hand

    private JButton verticalButton;
    private JButton horizontalButton;
    private CustomButton tileButton;

    public int getTargetRow() {
        return targetRow;
    }

    public int getTargetCol() {
        return targetCol;
    }

    public boolean getFirstLetter() {
        return first_letter;
    }

    public void setFirstLetter(boolean bool) {
        first_letter = bool;
    }

    public void setTargetRow(int row) {
        targetRow = row;
    }

    public void setTargetCol(int col) {
        targetCol = col;
    }

    public Game getModel(){
        return model;
    }
    public char getDirection() {
        return direction;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public int getClickedRow() {
        return clickedRow;
    }

    public int getClickedCol() {
        return clickedCol;
    }
    public void setClickedRow(int row){
        clickedRow = row;
    }
    public void setClickedCol(int col){
        clickedCol = col;
    }
    public JButton getVerticalButton() {
        return verticalButton;
    }

    public JButton getHorizontalButton() {
        return horizontalButton;
    }
    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }
    public boolean getVertical(){
        return isVertical;
    }
    public boolean getBeforeStart(){
        return beforeStart;
    }
    public void setBeforeStart(boolean input){
        beforeStart = input;
    }

    public void addInputWord(char letter){
        inputWord = inputWord + letter;
    }
    public void setSelectedTile(Tile tile){
        selectedTile = tile;
    }
    public JButton getSkip(){
        return skip;
    }
    public JButton getSubmit(){
        return submit;
    }
    public JFrame getFrame(){
        return frame;
    }

    public void setInputWord(String inputWord) {
        this.inputWord = inputWord;
    }

    /**
     * Constructor for the View class.
     * Initializes the game model, sets up the UI components, and displays the main game window.
     *
     * @param model The Game object representing the game model.
     */


    public View(Game model) {
        this.model = model;
        this.check = new Word();
        model.initializeTiles();
        model.initializePlayer();
        displayHand= new CustomButton[7];
        // Initialize hand panel
        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        handPanel.setPreferredSize(new Dimension(700, 50));
        updateHandPanel();

        // Initialize the direction buttons
        directionPanel = new JPanel(new GridLayout(2, 1));
        verticalButton = new JButton("Vertical");
        horizontalButton = new JButton("Horizontal");
        directionPanel.add(verticalButton);
        directionPanel.add(horizontalButton);

        buttons = new CustomButton[15][15];

        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                buttons[row][col] = new CustomButton();
                buttons[row][col].setPreferredSize(new Dimension(40, 25));
                buttons[row][col].setEnabled(false);
                buttons[row][col].setRow(row);
                buttons[row][col].setCol(col);


                clickedRow = buttons[row][col].getRow();

                // Add the ActionListener directly to each button
                clickedRow = row; // Since 'row' is accessible in this scope
                clickedCol = col; // Since 'col' is accessible in this scope

            }
        }

        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        handPanel.setPreferredSize(new Dimension(700, 50));
        updateHandPanel();

        //Answer button
        JPanel answer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        answer.setPreferredSize(new Dimension(100,100));


        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        submit = new JButton("Submit");
        skipPannel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        skipPannel.setPreferredSize(new Dimension(100,100));
        skip = new JButton("skip");
        JPanel container = new JPanel(new GridLayout(15, 15, 0, 0));
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                container.add(buttons[row][col]);
            }
        }


        frame.add(handPanel, BorderLayout.SOUTH);
        frame.add(directionPanel, BorderLayout.WEST);
        frame.add(container, BorderLayout.NORTH);
        frame.add(submit);
        frame.add(skip,BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        updateView();
    }

    public CustomButton getTileButton() {
        return tileButton;
    }

    /**
     * Updates the hand panel with the current player's tiles.
     */
    public void updateHandPanel() {
        handPanel.removeAll();

        // Add buttons for each tile in the current player's hand
        for (Tile tile : model.getCurrentPlayer().getHand()) {
            CustomButton tileButton = new CustomButton(String.valueOf(tile.getLetter()));

            // Inline event listener for selecting a tile
            tileButton.addActionListener(e -> {
                tileButton.setEnabled(false); // Disable the selected tile
                selectedTile = new Tile(tileButton.getText().charAt(0)); // Store the selected tile

                if (beforeStart) {
                    enableButtons(); // Allow board placement
                } else {
                    disableButtons();
                    updateEnabledTiles();
                }

                beforeStart = false; // Placement has started
            });
            handPanel.add(tileButton); // Add the button to the panel
        }
        handPanel.revalidate();
        handPanel.repaint();
    }


    /**
     * Enables tiles for the next placement based on the selected direction.
     */
    public void updateEnabledTiles() {
        if (isVertical) {
            if (clickedRow + 1 < 15) {

                if(!buttons[clickedRow + 1][clickedCol].getText().isEmpty()){
                    buttons[clickedRow + 2][clickedCol].setEnabled(true);
                    inputWord = inputWord + buttons[clickedRow+1][clickedCol].getText();

                }
                else {buttons[clickedRow + 1][clickedCol].setEnabled(true);} // Enable tile below for vertical
            }
        } else { // Horizontal
            if (clickedCol + 1 < 15) {
                if (!buttons[clickedRow][clickedCol + 1].getText().isEmpty()) {
                    buttons[clickedRow][clickedCol + 2].setEnabled(true);
                    inputWord = inputWord + buttons[clickedRow][clickedCol + 1].getText();
                } else {
                    buttons[clickedRow][clickedCol + 1].setEnabled(true); // Enable tile to the right for horizontal}
                }
            }

        }
    }

    /**
     * Updates the board view with the current state of the tiles.
     */
    public void updateView() {
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Tile tile = model.getBoard().getTile(row, col);
                if (tile != null && tile.getLetter() != ' ') {
                    buttons[row][col].setText(String.valueOf(tile.getLetter()));
                } else {
                    buttons[row][col].setText("");
                }
            }
        }
        frame.revalidate();
        frame.repaint();
    }


    /**
     * Enables all buttons on the board for tile placement.
     */
    public void enableButtons() {
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                buttons[row][col].setEnabled(true);
            }
        }
    }
    /**
     * Disables all buttons on the board after a tile is placed.
     */
    public void disableButtons(){
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                buttons[row][col].setEnabled(false);
            }
        }

    }

    public CustomButton getDisplayHandTile(int i) {
        return displayHand[i];
    }
}