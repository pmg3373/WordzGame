
import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pat
 */
public interface ModelListener {

    /**
     * Update the list of guessed letters
     * @param guessedLetters The updated list
     */
    public void updateIncorrect(String incorrectGuesses) throws IOException;

    /**
     * Update the revealed letters
     * @param revealedLetters The word with revealed letters and * for unrevealed
     */
    public void updateCorrect(char[] revealedLetters) throws IOException;
    
    /**
     * Updates the player's status
     * @param status Status message
     * @throws IOException 
     */
    public void statusUpdate(String status) throws IOException;

    /**
     * Tell the client to close the connection and end the game
     */
    public void endGame() throws IOException;
    
}
