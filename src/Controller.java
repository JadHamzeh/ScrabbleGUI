package src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class Controller implements ActionListener {
    private Game model;
    private View view;



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
        for (Tile tile: model.getCurrentPlayer().getHand()) {
            CustomButton tileButton = new CustomButton(String.valueOf(tile.getLetter()));
            tileButton.addActionListener(this::handButton);
        }
        view.getHandPanel().revalidate();
        view.getHandPanel().repaint();
    }



    private void verticleButton(){
        view.setVertical(true);
        view.getVerticalButton().setEnabled(false); // Disable after selecting vertical
        view.getHorizontalButton().setEnabled(false); // Disable horizontal as well
        view.setDirection('V');
        view.updateEnabledTiles();
    }
    private void horizontalButton() {
        view.setVertical(false);
        view.getVerticalButton().setEnabled(false); // Disable after selecting horizontal
        view.getHorizontalButton().setEnabled(false); // Disable vertical as well
        view.setDirection('H');
        view.updateEnabledTiles();
    }

    public void handButton (ActionEvent e){
        CustomButton button = (CustomButton) e.getSource();
        button.setEnabled(false);
        view.setSelectedTile(new Tile(button.getText().charAt(0)));// Store the selected tile
        System.out.println(view.getSelectedTile());
        if (view.getBeforeStart()) {
            view.enableButtons();
        } else {
            view.disableButtons();
            view.updateEnabledTiles();
        }
        view.setBeforeStart(false);
    }

    public void clickedBoard (ActionEvent e) {
        CustomButton clickedButton = (CustomButton) e.getSource();
        CustomButton[][] button = view.getButtons();
        if (view.getSelectedTile() != null) {
            view.setClickedRow(clickedButton.getRow());
            view.setClickedCol(clickedButton.getCol());
            button[view.getClickedRow()][view.getClickedCol()].setText(String.valueOf(view.getSelectedTile().getLetter()));
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
            System.out.println("Row: " + view.getTargetRow() + " Col: " + view.getTargetCol() +" Word:" + view.getInputWord());
            view.setSelectedTile(null);
            view.updateHandPanel();
            view.disableButtons();
        }
    }

    public void submitButton(ActionEvent e) {

        if (model.getCheck().isWord(view.getInputWord().toLowerCase()) && view.getInputWord().length() > 1){
            //replace all used tiles

            model.placeWord(view.getInputWord(), view.clickedRow, view.clickedCol, view.getDirection(), model.getCurrentPlayer(), view.getInputWord().length());
            JOptionPane.showMessageDialog(view.getFrame(),"submitted word: " + view.getInputWord() + " it is now " + model.getCurrentPlayer().getName() + "'s turn, they have " + model.getCurrentPlayer().getPoints() + " points");
            //replace hand with next players hand
            model.nextPlayer();
            view.updateHandPanel();

        }else{
            JOptionPane.showMessageDialog(view.getFrame(),"tried to submitted word: " + view.getInputWord() +" invalid word please try again");

        }

        view.getHorizontalButton().setEnabled(true);
        view.getVerticalButton().setEnabled(true);
        if(view.getVertical()){
            view.setDirection('V');
        }else{
            view.setDirection('H');
        }
        System.out.println(view.getInputWord());
        view.setBeforeStart(true);
        view.updateView();
        view.setInputWord("");
    }
    public void skip(ActionEvent e) {

        JOptionPane.showMessageDialog(view.getFrame(),"skipping turn, it is now " + model.getCurrentPlayer().getName() + "'s turn, they have " + model.getCurrentPlayer().getPoints() + " points");
        //replace hand with next players hand
        view.updateHandPanel();
        view.setBeforeStart(true);
        view.setInputWord("");
        view.getHorizontalButton().setEnabled(true);
        view.getVerticalButton().setEnabled(true);
        model.nextPlayer();
        view.updateHandPanel();
        view.updateView();
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
