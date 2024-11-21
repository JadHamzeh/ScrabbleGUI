# Scrabble 

This project is a simplified implementation of the Scrabble game using **Java**. It supports multiplayer gameplay with word validation and scoring mechanisms based on Scrabble rules.

---

## **How to Play**

1. **Start the Game**  
   Launch the game, and players will take turns entering words to play on the board.

2. **Word Validation**  
   - Words are checked against a predefined list stored in `WordBank.txt`.
   - If the word is invalid, the player must try again.

3. **Placing Words**  
   - Players select a letter from their hand.
   - Players select the **direction** of the word: Horizontal or Vertical.
   - Players place tile selected on board

4. **End of Turn**  
   - If the move is valid, the board updates with the new word, and the next player’s turn begins.
   - If the move is invalid, the player retries the turn.
   - If player wants they  can skip

5. **Winning the Game**  
   - The game continues until all tiles are used or no valid moves remain.
   - The player with the **highest score** at the end wins.

---

## **Features**

- **Multiplayer Support**  
  - Allows up to **4 players**, each starting with 7 tiles.

- **Word Validation**  
  - Words are validated against a dictionary file (`WordBank.txt`).

- **Score Calculation**  
  - Tiles have point values according to **Scrabble rules**.
  - Bonus points are awarded for placing all 7 tiles in one turn (Bingo).

- **Board Management**  
  - A **15x15 board** is displayed and updated with each move.

- **Tile Management**  
  - Tiles are drawn from a shared tile pile.
  - Players manage their hand by adding and removing tiles.

---

## **Technologies Used**

- **Java**  
  - Core programming language for the project.

- **Collections Framework**  
  - `ArrayList`, `HashSet`, and `Arrays` for tile and word management.

---

## **Copyright Disclaimer**

This project is a **non-commercial educational project** created for learning purposes.  
**Scrabble®** is a registered trademark of **Hasbro, Inc.** (in the U.S. and Canada) and **Mattel** (elsewhere).  
This implementation is not affiliated with or endorsed by the official owners of the Scrabble game.  
All copyrights, trademarks, and intellectual property related to **Scrabble** belong to their respective owners.

---

## **Contributors**

- JStLouisCode 
- JadHamzeh
- jaltercode
- CamBD

---

## **License**

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more information.
