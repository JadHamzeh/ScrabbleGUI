package src;

/**
 * Board class represents the Scrabble board, which is a 15x15 grid of tiles.
 * It initializes the board, displays it, and manages tiles at specific positions.
 */
public class Board {

    // Attributes
    private Tile[][] board = new Tile[15][15]; // A 15x15 grid of Tile objects representing the Scrabble board.


    // Constructor

    /**
     * Initializes the board with blank tiles (' ') at all positions.
     * Sets the center position (7,7) with the specified tile.
     *
     * @param tile The Tile to be placed at the center of the board (position 7,7).
     */
    public Board(Tile tile){
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                board[row][col] = new Tile(' '); // Initialize with blank tiles
            }
        }
        board[7][7] = tile; // Place the specified tile at the center
    }

    /**
     * Displays the current state of the board in a formatted grid.
     * Each tile's letter is shown within the grid, and row/column numbers are printed for clarity.
     */
    public void displayBoard(){
        System.out.print("   ");
        // Print column numbers
        for (int i = 0; i < 15; i++) {
            if (i < 12) {
                System.out.print("   " + (i+1) + "   ");
            }
            else {
                System.out.print(" " + (i + 1) + "   ");
            }
        }

        System.out.println();

        // Print each row with its tiles and row number
        for (int row = 0; row < 15; row++) {
            System.out.print(row+1);
            if(row<9){
                System.out.print(" ");
            }
            for (int col = 0; col < 15; col++) {
                System.out.print("  |  " + board[row][col].getLetter() + " ");
            }
            System.out.println("  |");

        }
    }

    /**
     * Retrieves the tile at the specified position on the board.
     * If the position is out of bounds, a blank tile (' ') is returned.
     *
     * @param row The row index (0-14).
     * @param col The column index (0-14).
     * @return The Tile at the specified position, or a blank tile if out of bounds.
     */
    public Tile getTile(int row, int col) {
        if (row >= 0 && row < 15 && col >= 0 && col < 15) {
            return board[row][col];
        } else {
            return new Tile(' '); // Out-of-bounds positions are treated as empty
        }
    }

    /**
     * Places a tile at the specified position on the board if the position is valid.
     *
     * @param row The row index (0-14).
     * @param col The column index (0-14).
     * @param tile The Tile to place at the specified position.
     */
    public void setTile(int row, int col, Tile tile) {
        if (row >= 0 && row < 15 && col >= 0 && col < 15) {
            board[row][col] = tile;
        }
    }







}