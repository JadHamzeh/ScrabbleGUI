package src;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * The Game class manages the overall flow of the Scrabble game.
 * It initializes the game components, handles player actions, word validation, and tile placement.
 */
public class Game {

    private View view;

    private JButton[][] buttons;
    // Attributes

    // The collection of tiles available in the game.
    final public TilePile tilePile;

    // The array of players participating in the game.
    final public Player[] player;

    // Game board where words are placed.
    final private Board board;

    // Word validator that checks the validity of words against a dictionary.
    final private Word check;

    //current player

    private int currentPlayerIndex = 0;
    private int currentPlayer = 0;


    // Constructor

    /**
     * Initializes the game components, including the players, tile pile, board, and word validator.
     */
    public Game() {
        this.player = new Player[4];
        this.tilePile = new TilePile();
        this.initializeTiles();
        this.initializePlayer();
        Tile temp = tilePile.deleteTile();
        while (temp.getLetter() == '*') { // make sure centre tile isnt blank tile
            tilePile.addTile('*', 1);
            temp = tilePile.deleteTile();
        }
        this.board = new Board(temp);
        this.check = new Word();
        this.view = new View(this);
    }

    // Methods

    /**
     * Initializes the tile pile with the correct quantity of each letter tile.
     * Shuffles the tiles to randomize their order.
     */
    public void initializeTiles() {
        this.tilePile.addTile('A', 9);
        this.tilePile.addTile('B', 2);
        this.tilePile.addTile('C', 2);
        this.tilePile.addTile('D', 4);
        this.tilePile.addTile('E', 12);
        this.tilePile.addTile('F', 2);
        this.tilePile.addTile('G', 3);
        this.tilePile.addTile('H', 2);
        this.tilePile.addTile('I', 9);
        this.tilePile.addTile('J', 1);
        this.tilePile.addTile('K', 1);
        this.tilePile.addTile('L', 4);
        this.tilePile.addTile('M', 2);
        this.tilePile.addTile('N', 6);
        this.tilePile.addTile('O', 8);
        this.tilePile.addTile('P', 2);
        this.tilePile.addTile('Q', 1);
        this.tilePile.addTile('R', 6);
        this.tilePile.addTile('S', 4);
        this.tilePile.addTile('T', 6);
        this.tilePile.addTile('U', 4);
        this.tilePile.addTile('V', 2);
        this.tilePile.addTile('W', 2);
        this.tilePile.addTile('X', 1);
        this.tilePile.addTile('Y', 2);
        this.tilePile.addTile('Z', 1);
        this.tilePile.addTile('*', 2); // BLANKS HERE. increase to test
        Collections.shuffle(tilePile.getPile());
    }

    /**
     * Initializes the players and gives each of them 7 tiles from the tile pile.
     */
    public void initializePlayer() {
        for (int i = 0; i < 4; i++) {
            player[i] = new Player(i);
            for (int j = 0; j < 7; j++) {
                player[i].addTile(tilePile.deleteTile());
            }
        }
    }

    /**
     * Continuously manages player turns, collects input for word placement,
     * and updates the board until the game ends.
     */
    public boolean play(String word, char direction, int row, int col) {
        if (!check.isWord(word.toLowerCase()) || word.length() < 2) { // is the word valid in the wordbank
            return false;
        }
        if (canPlaceWord(word.toUpperCase(), row, col, direction, player[currentPlayer])) { // can the word be legally placed
            placeWord(word.toUpperCase(), row, col, direction, player[currentPlayer]); // place it
            nextPlayer();
            return true;
        }
        currentPlayerIndex = currentPlayer;
        view.updateView();
        return false;
    }

    /**
     * Checks if the word can be legally placed on the board at the specified position.
     *
     * @param word      The word to be placed.
     * @param row       The starting row index for the word (0-14).
     * @param col       The starting column index for the word (0-14).
     * @param direction The direction of the word: 'H' for horizontal, 'V' for vertical.
     * @param player    The player attempting to place the word.
     * @return true if the word can be legally placed, false otherwise.
     */
    public boolean canPlaceWord(String word, int row, int col, char direction, Player player) {
        boolean flag = true;
        // out of bounds check
        if (direction == 'H') {
            if (word.length() + col > 15) return false;
        } else {
            if (word.length() + row > 15) return false;
        }
//        boolean isVertical;
//        if(direction == 'H'){
//            isVertical = false;
//        } else {isVertical = true;}
//        if(!player.hasAllTiles(word, board.getTiles(), row, col, isVertical)){
//            return false;
//        }

        // first see if the word fits on the board
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            Tile boardTile = (direction == 'H') ? board.getTile(row, col + i) : board.getTile(row + i, col);

            if (boardTile.getLetter() == ' ') {
                continue;
            }

            if (boardTile.getLetter() != currentChar) {
                return false;
            }
        }
        // now second layer check, new formed adjacent words must also be valid
        if (direction == 'H') {
            if (word.length() + col > 15) return false;
            flag &= horizontalAdjacencyCheck(word, row, col); // does the word extend other letters?

            for (int i = 0; i < word.length(); i++) {
                if (board.getTile(row, col + i).getLetter() == ' ') {
                    if (!verticalAdjacencyCheck(String.valueOf(word.charAt(i)), row, col + i)) { // does each letter create new valid vertical adjacent word
                        return false;
                    }
                }
            }
        } else {
            if (word.length() + row > 15) return false;
            flag &= verticalAdjacencyCheck(word, row, col); // does the word extend other letters?

            for (int i = 0; i < word.length(); i++) {
                if (board.getTile(row + i, col).getLetter() == ' ') {
                    if (!horizontalAdjacencyCheck(String.valueOf(word.charAt(i)), row + i, col)) { // does each letter create new valid horizontal adjacent word
                        return false;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * Checks if placing a word horizontally forms a valid word with adjacent tiles.
     *
     * @param word The word being placed.
     * @param row  The starting row of the word.
     * @param col  The starting column of the word.
     * @return true if the word forms valid horizontal adjacent words, false otherwise.
     */
    public boolean horizontalAdjacencyCheck(String word, int row, int col) {
        StringBuilder adjacent = new StringBuilder();
        int startCol = col;

        while (startCol > 0 && board.getTile(row, startCol - 1).getLetter() != ' ') { // find starting letter index within row
            startCol--;
        }

        for (int i = startCol; i < col; i++) {
            adjacent.append(board.getTile(row, i).getLetter()); // add extension left of new letter(s), if present
        }

        adjacent.append(word); // add newly placed letter(s)

        for (int i = col + word.length(); i < 15; i++) {
            if (board.getTile(row, i).getLetter() == ' ') break;
            adjacent.append(board.getTile(row, i).getLetter()); // add letters to the right of new letter(s), if present
        }

        String formedWord = adjacent.toString().toLowerCase(); // form word as String
        return check.isWord(formedWord); // check valid word, (single character words are valid)
    }

    /**
     * Checks if placing a word vertically forms a valid word with adjacent tiles.
     *
     * @param word The word being placed.
     * @param row  The starting row of the word.
     * @param col  The starting column of the word.
     * @return true if the word forms valid vertical adjacent words, false otherwise.
     */
    public boolean verticalAdjacencyCheck(String word, int row, int col) {
        StringBuilder adjacent = new StringBuilder();
        int startRow = row;

        while (startRow > 0 && board.getTile(startRow - 1, col).getLetter() != ' ') { // find starting letter index within col
            startRow--;
        }

        for (int i = startRow; i < row; i++) {
            adjacent.append(board.getTile(i, col).getLetter()); // add extension above new word, if present
        }

        adjacent.append(word); // add newly placed letter(s)

        for (int i = row + word.length(); i < 15; i++) {
            if (board.getTile(i, col).getLetter() == ' ') break;
            adjacent.append(board.getTile(i, col).getLetter()); // add letters below the new letter(s), if present
        }

        String formedWord = adjacent.toString().toLowerCase(); // form word as String
        return check.isWord(formedWord); //check valid word, (single character words are valid)
    }

    /**
     * Places a word on the board at the specified position and updates the player's hand.
     *
     * @param word      The word to be placed on the board.
     * @param row       The starting row index for the word (0-14).
     * @param col       The starting column index for the word (0-14).
     * @param direction The direction of the word: 'H' for horizontal, 'V' for vertical.
     * @param player    The player placing the word.
     */
    public void placeWord(String word, int row, int col, char direction, Player player) {
        for (int i = 0; i < word.length(); i++) {
            char wordtile = word.charAt(i);
            Tile boardTile = (direction == 'H') ? board.getTile(row, col + i) : board.getTile(row + i, col);

            if (boardTile.getLetter() == ' ') { // If it's an empty space, place the tile (this is checked within canPlaceWord() as well)
                Tile newTile = player.removeTile(new Tile(wordtile)); // remove from player rack
                player.addTile(tilePile.deleteTile()); // take from tilePile (bag)
                if (direction == 'H') {
                    board.setTile(row, col + i, newTile);
                    String temp = buildWord(row, col + i, 'V', Character.toString(board.getTile(row, col + i).getLetter()));

                    if (temp.length() > 1) {
                        int start = row;
                        while (start > 0 && board.getTile(start -1, col + i).getLetter() != ' ') { // find starting letter index within col
                            start--;
                        }

                        addPoints(temp, player, start, col + i, 'V');
                    }
                } else { // direction == 'V'
                    board.setTile(row + i, col, newTile);
                    String temp = buildWord(row+i, col, 'H', Character.toString(board.getTile(row+i, col).getLetter()));
                    if (temp.length() > 1) {
                        int start = col;
                        while (start > 0 && board.getTile(row +i, start - 1).getLetter() != ' ') { // find starting letter index within col
                            start--;
                        }
                        addPoints(temp, player, row + i, start, 'H');

                    }
                }
            }
        }
        addPoints(word, player, row, col, direction); // add points for original word
    }


    /**
     * Adds points to the player's total score based on the word placed on the board.
     *
     * @param word   The word that was placed on the board.
     * @param player The player who placed the word and earned the points.
     */
    public void addPoints(String word, Player player, int row, int col, char direction) {
        int total = 0;
        int multiplier = 1;
        if (direction == 'V') {
            for (int i = 0; i < word.length(); i++) {
                if (board.getTile(row + i, col).getBonus().equals("TW")) {
                    total += board.getTile(row + i, col).getPoints();
                    multiplier *= 3;
                } else if (board.getTile(row + i, col).getBonus().equals("DW")) {
                    total += board.getTile(row + i, col).getPoints();
                    multiplier *= 2;
                } else if (board.getTile(row + i, col).getBonus().equals("TL")) {
                    total += 3 * board.getTile(row + i, col).getPoints();
                } else if (board.getTile(row + i, col).getBonus().equals("DL")) {
                    total += 2 * board.getTile(row + i, col).getPoints();
                } else {
                    total += board.getTile(row + i, col).getPoints();
                }
            }
        } else {
            for (int i = 0; i < word.length(); i++) {
                if (board.getTile(row, col + i).getBonus().equals("TW")) {
                    total += board.getTile(row + i, col).getPoints();
                    multiplier *= 3;
                } else if (board.getTile(row, col + i).getBonus().equals("DW")) {
                    total += board.getTile(row, col + i).getPoints();
                    multiplier *= 2;
                } else if (board.getTile(row, col + i).getBonus().equals("TL")) {
                    total += 3 * board.getTile(row, col + i).getPoints();
                } else if (board.getTile(row, col + i).getBonus().equals("DL")) {
                    total += 2 * board.getTile(row, col + i).getPoints();
                } else {
                    total += board.getTile(row, col + i).getPoints();
                }
            }
        }
        player.addPoints(total * multiplier);
    }

    public void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % 4;
        currentPlayerIndex = currentPlayer;
    }

    public Player getCurrentPlayer() {
        return player[currentPlayerIndex];
    }

    public Board getBoard() {
        return this.board;
    }

    public Word getCheck() {
        return check;
    }

    public View getView() {
        return view;
    }


    //AI Logic
    public ArrayList<Tile> getBoardLetters(){
        ArrayList<Tile> letters = new ArrayList<>();

        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Tile tile = getBoard().getTile(row,col);
                tile.setCol(col);
                tile.setRow(row);
                if (!(tile.getLetter() == ' ')){
                    letters.add(tile);

                }
            }
        }
        return letters;
    }


    public ArrayList<WordInfo> getWords() {
        ArrayList<Tile> boardLetters = getBoardLetters();
        ArrayList<Character> handLetters = new ArrayList<>();
        ArrayList<WordInfo> words = new ArrayList<>();

        for (Tile tile : getCurrentPlayer().getHand()) {
            Character tileButton = tile.getLetter();
            handLetters.add(tileButton);
        }
        for (Tile boardLetter : boardLetters) {

            words.addAll(findWords(boardLetter.getLetter(), handLetters, boardLetter.getRow(), boardLetter.getCol()));
        }

        return words;
    }

    public ArrayList<WordInfo> findWords(Character boardLetter, ArrayList<Character> handLetters, int row, int col) {
        ArrayList<WordInfo> words = new ArrayList<>();
        ArrayList<Character> allLetters = new ArrayList<>(handLetters);
        allLetters.add(boardLetter); // Include the board letter

        // Generate combinations for word lengths 2-5
        for (int wordLength = 2; wordLength <= 5; wordLength++) {
            generateCombinations(allLetters, "", wordLength, words, boardLetter, row, col);
        }
        return words;
    }

    private final Set<String> placedWords = new HashSet<>();

    private void generateCombinations(ArrayList<Character> letters, String currentWord, int maxLength,
                                      ArrayList<WordInfo> words, Character boardLetter, int row, int col) {
        if (currentWord.length() == maxLength) {
            if (currentWord.contains(boardLetter.toString()) && !placedWords.contains(currentWord)) {
                words.add(new WordInfo(currentWord, row, col, boardLetter));
                placedWords.add(currentWord);
            }
            return;
        }
        for (int i = 0; i < letters.size(); i++) {
            Character letter = letters.get(i);
            ArrayList<Character> remainingLetters = new ArrayList<>(letters);
            remainingLetters.remove(i);
            generateCombinations(remainingLetters, currentWord + letter, maxLength, words, boardLetter, row, col);
        }
    }


    public int getPoints(String word) {
        int points = 0;
        for (Character letter : word.toUpperCase().toCharArray()) {
            Tile tile = new Tile(letter);
            points += tile.getPoints();
        }
        return points;
    }

    public ArrayList<WordInfo> sortPoints(ArrayList<WordInfo> wordList) {
        wordList.sort((wordInfo1, wordInfo2) -> {
            int points1 = getPoints(wordInfo1.getWord());
            int points2 = getPoints(wordInfo2.getWord());
            return Integer.compare(points2, points1); // Descending order
        });

        return wordList;
    }


    private int getWordCol(WordInfo word, Character orientation) {
        if (orientation.equals('V')) {
            // Vertical case: Column stays the same
            return word.getCol();
        } else {
            // Horizontal case: Column changes based on the position of the last letter
            int LetterIndex = word.getLetterIndex(); // Index of the last letter
            return word.getCol() - LetterIndex;
        }
    }

    private int getWordRow(WordInfo word, Character orientation) {
        if (orientation.equals('V')) {
            // Vertical case: Row changes based on the position of the last letter
            int LetterIndex = word.getLetterIndex(); // Index of the last letter
            return word.getRow() - LetterIndex;
        } else {
            // Horizontal case: Row stays the same
            return word.getRow();
        }
    }

    public void aiPlay(){
        String chosenWord = null;
        ArrayList<WordInfo> wordList = sortPoints(getWords());
        char direction = 'V';
        int row = 0;
        int col = 0;


        for (WordInfo word : wordList) {
            // Horizontal Case
            col = getWordCol(word, 'V');
            row = getWordRow(word, 'V');

            // Ensure row and col are between 1 and 15
            if (row >= 0 && row <= 14 && col >= 0 && col <= 14) {
                if (canPlaceWord(word.getWord(), row, col, 'V', getCurrentPlayer())) {
                    chosenWord = word.getWord();
                    break;
                }
            }

            // Vertical Case
            col = getWordCol(word, 'H');
            row = getWordRow(word, 'H');

            // Ensure row and col are between 1 and 15
            if (row >= 0 && row <= 14 && col >= 1 && col <= 14) {
                if (canPlaceWord(word.getWord(), row, col, 'H', getCurrentPlayer())) {
                    chosenWord = word.getWord();
                    direction = 'H';
                    break;
                }
            }
        }

        if (chosenWord != null){
            play(chosenWord,direction,row,col);
            System.out.println(chosenWord);
        }
        else{
            System.out.println("no words can be played");
        }
    }

    public String buildWord(int row, int col, char direction, String word) {
        if (direction == 'H') {
            StringBuilder adjacent = new StringBuilder();
            int startCol = col;

            while (startCol > 0 && board.getTile(row, startCol - 1).getLetter() != ' ') { // find starting letter index within row
                startCol--;
            }

            for (int i = startCol; i < col; i++) {
                adjacent.append(board.getTile(row, i).getLetter()); // add extension left of new letter(s), if present
            }

            adjacent.append(word); // add newly placed letter(s)

            for (int i = col + word.length(); i < 15; i++) {
                if (board.getTile(row, i).getLetter() == ' ') break;
                adjacent.append(board.getTile(row, i).getLetter()); // add letters to the right of new letter(s), if present
            }
            return adjacent.toString().toLowerCase(); // check valid word, (single character words are valid)

        } else {
            StringBuilder adjacent = new StringBuilder();
            int startRow = row;

            while (startRow > 0 && board.getTile(startRow - 1, col).getLetter() != ' ') { // find starting letter index within col
                startRow--;
            }

            for (int i = startRow; i < row; i++) {
                adjacent.append(board.getTile(i, col).getLetter()); // add extension above new word, if present
            }

            adjacent.append(word); // add newly placed letter(s)

            for (int i = row + word.length(); i < 15; i++) {
                if (board.getTile(i, col).getLetter() == ' ') break;
                adjacent.append(board.getTile(i, col).getLetter()); // add letters below the new letter(s), if present
            }

            return adjacent.toString().toLowerCase();
        }
    }
}
