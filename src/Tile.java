package src;

/**
 * Tile class represents an individual letter tile in Scrabble.
 * Each tile has a letter and an associated point value according to Scrabble rules.
 */
public class Tile {

    // Attributes
    char letter; // The letter printed on the tile (e.g., 'a', 'e', 'x').

    // The point value assigned to the tile based on Scrabble rules.
    int points;

    // Constructor

    /**
     * Initializes the tile with a specific letter and assigns its point value
     * using the assignValue() method.
     *
     * @param letter The letter on the tile.
     */
    public Tile(char letter){
        this.letter = letter;
        this.points = assignValue(letter);
    }

    // Methods

    /**
     * Assigns a point value to a given letter
     *
     * @param letter The letter for which the point value is assigned.
     * @return The point value of the letter.
     */
    private static int assignValue(char letter) { //assign value to each letter according to scrabble rules
        return switch (Character.toLowerCase(letter)) {
            case 'a', 'e', 'i', 'o', 'u', 'l', 'n', 's', 't', 'r' -> 1;
            case 'd', 'g' -> 2;
            case 'b', 'c', 'm', 'p' -> 3;
            case 'f', 'h', 'v', 'w', 'y' -> 4;
            case 'k' -> 5;
            case 'j', 'x' -> 8;
            case 'q', 'z' -> 10;
            default -> 0; // In case of an invalid or blank ' ' character
        };
    }

    /**
     * Retrieves the letter on this tile.
     *
     * @return The character on the tile.
     */
    public char getLetter() {
        return letter;
    }

    /**
     * Retrieves the point value of this tile.
     *
     * @return The point value of the tile.
     */
    public int getPoints() {
        return points;
    }
}
