package src;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.Point;
import java.util.Locale;
class View {


    private List<Point> tilesPlacedThisTurn = new ArrayList<>();



    private JPanel directionPanel;
    private int targetRow;
    private int targetCol;
    private boolean first_letter = true; // is the current player placing their first tile

    private boolean isVertical;

    private char direction = 'H';
    private CustomButton[][] buttons;

    public CustomButton[][] getButtons() {
        return buttons;
    }

    private JPanel skipPannel;
    private JButton skip;
    private JButton submit;
    private JButton ai_turn;
    private JPanel scoreboardPanel;
    private JLabel[] playerScores;


    public JPanel getHandPanel() {
        return handPanel;
    }

    public String getInputWord() {
        return inputWord;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    private CustomButton[] handButtons;
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

    public JButton getAi_turn(){return ai_turn;}
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
        // Initialize hand panel
        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        handPanel.setPreferredSize(new Dimension(700, 50));
        handButtons = new CustomButton[7];
        for (int i = 0; i < 7; i++) {
            CustomButton tileButton = new CustomButton(String.valueOf(model.getCurrentPlayer().getHand().get(i).getLetter()));
            handButtons[i] = tileButton;
            handPanel.add(handButtons[i]);
        }

        updateHandPanel();

        // initialize ai_turn button;

        ai_turn = new JButton("Play AI Turn!");

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


                clickedRow = row; // Since 'row' is accessible in this scope
                clickedCol = col; // Since 'col' is accessible in this scope

            }
        }
        //Answer button
        JPanel answer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        answer.setPreferredSize(new Dimension(100,100));


        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000,600));
        submit = new JButton("Submit");
        submit.setPreferredSize(new Dimension(600, 80));
        skipPannel = new JPanel(new GridLayout(2, 1));
        skipPannel.setPreferredSize(new Dimension(150,100));
        skip = new JButton("Skip");
        skipPannel.add(skip);
        skipPannel.add(ai_turn);
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
        frame.add(skipPannel,BorderLayout.EAST);
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
        // Add buttons for each tile in the current player's hand
        for (int i = 0; i < 7; i++) {
            handButtons[i].setText(Character.toString(model.getCurrentPlayer().getHand().get(i).getLetter()));
            handButtons[i].setEnabled(handButtons[i].isEnabled());
        }
        handPanel.revalidate();
        handPanel.repaint();
    }

    public void refreshHandPanel(Boolean bool){
        for (int i = 0; i < 7; i++) {
            handButtons[i].setEnabled(bool);
        }
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
                if(tile.getBonus().equals("TW")) {
                    buttons[row][col].setBackground(Color.RED);
                } else if (tile.getBonus().equals("TL")) {
                    buttons[row][col].setBackground(Color.BLUE);
                }else if (tile.getBonus().equals("DW")) {
                    buttons[row][col].setBackground(Color.PINK);
                }else if (tile.getBonus().equals("DL")) {
                    buttons[row][col].setBackground(Color.CYAN);
                }

                if (tile != null && tile.getLetter() != ' ') {
                    buttons[row][col].setText(String.valueOf(tile.getLetter()));
                } else {
                    buttons[row][col].setText("");
                }
            }
        }
        frame.revalidate();
        frame.repaint();

        System.out.println(model.getBoardLetters());
        System.out.println(model.sortPoints(model.getWords()));
    }


    /**
     * Enables all buttons on the board for tile placement.
     */
    public void enableButtons() {
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Tile tile = model.getBoard().getTile(row, col);
                if (!(tile != null && tile.getLetter() != ' ')){
                    buttons[row][col].setEnabled(true);
                }
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

    /**
     * Initializes the scoreboard panel to display the scores of all players.
     *
     * @param players an array of Player objects representing all players in game
     */
    public void initializeScoreboard(Player[] players) {
        scoreboardPanel = new JPanel(new GridLayout(players.length, 1));
        playerScores = new JLabel[players.length];

        for (int i = 0; i < players.length; i++) {
            playerScores[i] = new JLabel(players[i].getName() + ": " + players[i].getPoints() + " points");
            scoreboardPanel.add(playerScores[i]);
        }

        frame.add(scoreboardPanel, BorderLayout.SOUTH);
        frame.revalidate();
    }


    /**
     * Updates the scoreboard panel to reflect the current scores of all players.
     *
     * @param players an array of Player objects representing all players in game
     */
    public void updateScoreboard(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            playerScores[i].setText(players[i].getName() + ": " + players[i].getPoints() + " points");
        }
        scoreboardPanel.revalidate();
        scoreboardPanel.repaint();
    }


    public void addTilePlacedThisTurn(int row, int col) {
        tilesPlacedThisTurn.add(new Point(row, col));
    }

    public void clearTilesPlacedThisTurn() {
        tilesPlacedThisTurn.clear();
    }



    public List<Point> getTilesPlacedThisTurn() {
        return tilesPlacedThisTurn;
    }



    public CustomButton[] getHandButtons(){
        return handButtons;
    }
}