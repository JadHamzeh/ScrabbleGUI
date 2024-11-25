
package src;

public class WordInfo {
    private String word;
    private char boardTile;
    private int row;
    private int col;

    public WordInfo(String word, int row, int col, char boardTile) {
        this.word = word;
        this.row = row;
        this.col = col;
        this.boardTile = boardTile;
    }

    public char getBoardTile() {
        return boardTile;
    }

    public String getWord() {
        return word;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public int getLetterIndex(){
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == boardTile) {
                return i;
            }
        }
        return -1;
    }
    public char getLastLetter() {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("Word cannot be null or empty");
        }
        return word.charAt(word.length() - 1); // Access the last character
    }
}



