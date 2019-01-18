
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pat
 */
public interface ViewListener {
    
    /**
     * Resets the Wordz game with a new random word
     */
    public void newGame() throws IOException;
    
    /**
     * Closes the game and instructs each player to close the game
     */
    public void endGame() throws IOException;
    
    /**
     * Adds a ModelListener to this session, also need to know the player's name
     * @param newPlayer The ModelListener to add
     * @param playerName The name of the player
     */
    public void addModelListener(ModelListener newPlayer, String playerName) throws IOException;
    
    /**
     * Connect to a game session
     * @param proxy Reference to the view proxy
     * @param playerName Name of the player
     * @throws IOException 
     */
    public void join(ViewProxy proxy, String playerName) throws IOException;
    
    /**
     * The currently active player takes a turn with the given guess
     * @param guess A single character string, the player's guess
     */
    public void takeTurn(String guess) throws IOException;
}
