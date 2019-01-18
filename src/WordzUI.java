import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Class WordzUI provides the graphical user interface for the Wordz network
 * game.
 *
 * @author  Alan Kaminsky
 * @version 07-Mar-2017
 */
public class WordzUI implements ModelListener {
    private static final int GAP = 10;
    private static final int COLS = 20;

    private JFrame frame;
    private JTextField statusField;
    private JTextField correctField;
    private JTextField incorrectField;
    private JButton newGameButton;
    
    private ViewListener viewListener;
    
    
/*************************Model Listener Methods*******************************/
    
    /**
     * Update the incorrect guesses field
     * @param incorrectGuesses The text to update it with
     * @throws IOException 
     */
    public void updateIncorrect(String incorrectGuesses) throws IOException {
        incorrectField.setText(incorrectGuesses);
        frame.repaint();
    }

    /**
     * Update the correct guesses field
     * @param revealedLetters The text to update it with as an array of char
     * @throws IOException 
     */
    public void updateCorrect(char[] revealedLetters) throws IOException {
        String revAsString = "";
        for(int i = 0; i < revealedLetters.length; i++){
            revAsString += revealedLetters[i];
        }
        correctField.setText(revAsString);
        frame.repaint();
    }
    
    /**
     * Update the game status field
     * @param status The text to update it with
     * @throws IOException 
     */
    public void statusUpdate(String status)throws IOException{
        statusField.setText(status);
        frame.repaint();
    }

    /**
     * Ends the game, closing the game window
     * @throws IOException 
     */
    public void endGame() throws IOException {
        System.exit(0);
    }
    
/*************************Model Listener Methods*******************************/

    
/*************************UI Event Methods*************************************/
    
    /**
     * Restarts the game
     */
    public void doNewGame(){
        try {
            viewListener.newGame();
        } 
        catch (IOException ex) {
        }
    }
    
    /**
     * handles key presses
     * @param letter The key pressed
     */
    public void doKeyPressed(char letter){
        try {
            viewListener.takeTurn(""+letter);
        } 
        catch (IOException ex) {
        }
    }
    
    /**
     * Closes the game
     */
    public void doGameClose(){
        try {
            viewListener.endGame();
        } 
        catch (IOException ex) {
        }
    }
    
/*************************UI Event Methods*************************************/
    
    /**
     * Swing thread startup
     * @param task The task to hand to swing
     */
    public static void onSwingThreadDo(Runnable task){
        try{
            SwingUtilities.invokeAndWait(task);
        }
        catch(Exception e){
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
    
    /**
     * Create a new WordzUI window
     * @param playerName The name of the player
     * @return 
     */
    public static WordzUI create(final String playerName) {
        final WordzUIRef ref = new WordzUIRef();
        onSwingThreadDo(new Runnable(){
            public void run(){
                ref.ui = new WordzUI(playerName);
            }
        });
        return ref.ui;
    }
    
    /**
     * Set the reference to the view listener
     * @param viewListener The view listener
     */
    public void setViewListener(final ViewListener viewListener){
        onSwingThreadDo(new Runnable(){
            public void run(){
                WordzUI.this.viewListener = viewListener;
            }
        });
    }

    /**
     * Helper class for responding to a keystroke.
     */
    private class KeyStrokeAction extends AbstractAction {
        private char letter;
        public KeyStrokeAction (char letter){
            this.letter = letter;
        }
        public void actionPerformed (ActionEvent e){
            doKeyPressed(letter);
        }
    }

    /**
     * Construct a new Wordz UI.
     *
     * @param  name  Player's name.
     */
    private WordzUI (String name) {
        frame = new JFrame ("Wordz -- " + name);
        JPanel panel = new JPanel();
        panel.setLayout (new BoxLayout (panel, BoxLayout.Y_AXIS));
        panel.setBorder (BorderFactory.createEmptyBorder (GAP, GAP, GAP, GAP));
        frame.add (panel);

        statusField = new JTextField (COLS);
        statusField.setEditable (false);
        statusField.setFocusable (false);
        statusField.setAlignmentX (0.5f);
        panel.add (statusField);
        panel.add (Box.createVerticalStrut (GAP));

        correctField = new JTextField (COLS);
        correctField.setEditable (false);
        correctField.setFocusable (false);
        correctField.setAlignmentX (0.5f);
        panel.add (correctField);
        panel.add (Box.createVerticalStrut (GAP));

        incorrectField = new JTextField (COLS);
        incorrectField.setEditable (false);
        incorrectField.setFocusable (false);
        incorrectField.setAlignmentX (0.5f);
        panel.add (incorrectField);
        panel.add (Box.createVerticalStrut (GAP));

        newGameButton = new JButton ("New Game");
        newGameButton.setAlignmentX (0.5f);
        panel.add (newGameButton);

        InputMap inputMap = newGameButton.getInputMap
                (JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = newGameButton.getActionMap();
        for (char letter = 'a'; letter <= 'z'; ++ letter)
                {
                KeyStrokeAction action = new KeyStrokeAction (letter);
                inputMap.put (KeyStroke.getKeyStroke (letter), action);
                actionMap.put (action, action);
                }

        newGameButton.addActionListener (new ActionListener()
                {
                public void actionPerformed (ActionEvent e)
                        {
                            doNewGame();
                        }
                });

        frame.addWindowListener (new WindowAdapter()
                {
                public void windowClosing (WindowEvent e)
                        {
                            doGameClose();
                        }
                });

        frame.pack();
        frame.setVisible (true);
    }
    
    /**
     * Class that holds a reference to the WordzUI
     */
    private static class WordzUIRef {WordzUI ui;}
}
