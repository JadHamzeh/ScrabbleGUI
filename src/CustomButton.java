package src;

import javax.swing.*;

public class CustomButton extends JButton {
    private int row;
    private int col;

    public CustomButton() {
        super();
    }

    public CustomButton(String s) {
        super(s);
    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }


}