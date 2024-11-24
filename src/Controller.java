package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class Controller implements ActionListener {
    private static Game model;
    private static View view;
    CustomButton storedButton;

    public Controller() {
        model = new Game();
        this.view = model.getView();

        view.disableButtons();

        view.getVerticalButton().addActionListener(e-> verticalButton());
        view.getHorizontalButton().addActionListener(e->horizontalButton());
        CustomButton[][] button = view.getButtons();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                button[row][col].addActionListener(this::clickedBoard);
            }
        }
        view.getSubmit().addActionListener(this::submitButton);
        view.getSkip().addActionListener(this::skip);
        for (int i = 0; i < 7; i++) {
            view.getHandButtons()[i].addActionListener(this::handButton);
        }
        view.getHandPanel().revalidate();
        view.getHandPanel().repaint();
    }



    private void verticalButton(){
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
        storedButton = (CustomButton) e.getSource();
        button.setEnabled(false);
        view.setSelectedTile(new Tile(button.getText().charAt(0)));// Store the selected tile
        if(view.getSelectedTile().getLetter() == '*'){
            blankSelector();
            return;
        }
        if (view.getBeforeStart()) {
            view.enableButtons();
        } else {
            view.disableButtons();
            view.updateEnabledTiles();
        }
        view.setBeforeStart(false);
        view.updateHandPanel();
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
            view.setSelectedTile(null);
            //view.updateHandPanel();
            view.disableButtons();

            if(storedButton != null){
                storedButton.setEnabled(false);
                storedButton = null;
            }

        }
    }

    public void submitButton(ActionEvent e) {
        if(!view.getVertical()){
            while(model.getBoard().getTile(view.getTargetRow(), view.getTargetCol() + view.getInputWord().length()).getLetter() != ' '){
                view.addInputWord(model.getBoard().getTile(view.getTargetRow(), view.getTargetCol() + view.getInputWord().length()).getLetter());
            }
        }else{
            while(model.getBoard().getTile(view.getTargetRow() + view.getInputWord().length(), view.getTargetCol()).getLetter() != ' '){
                view.addInputWord(model.getBoard().getTile(view.getTargetRow() + view.getInputWord().length(), view.getTargetCol()).getLetter());
            }
        }
        if (model.play(view.getInputWord().toLowerCase(), view.getDirection(), view.getTargetRow(), view.getTargetCol())){

            System.out.println("Input word " + view.getInputWord() +" Row: " + view.getTargetRow() + " Col: " + view.getTargetCol() +" Dir:" + view.getDirection());
            JOptionPane.showMessageDialog(view.getFrame(),"submitted word: " + view.getInputWord() + " it is now " + model.getCurrentPlayer().getName() + "'s turn, they have " + model.getCurrentPlayer().getPoints() + " points");

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

        view.updateHandPanel();
        view.setFirstLetter(true);
        view.setBeforeStart(true);
        view.updateView();
        view.refreshHandPanel();
        view.setInputWord("");
    }
    public void skip(ActionEvent e) {

        model.nextPlayer();
        //replace hand with next players hand
        view.updateHandPanel();
        view.setBeforeStart(true);
        view.setInputWord("");
        view.getHorizontalButton().setEnabled(true);
        view.getVerticalButton().setEnabled(true);
        view.updateHandPanel();
        view.updateView();
        JOptionPane.showMessageDialog(view.getFrame(),"Skipping turn, it is now " + model.getCurrentPlayer().getName() + "'s turn, they have " + model.getCurrentPlayer().getPoints() + " points");
    }
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

    public static void main(String[] args) {
        Controller controller = new Controller() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("g");
            }
        };
    }


}
