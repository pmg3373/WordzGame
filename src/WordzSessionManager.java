
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Connects Wordz players to a WordzGame sessions
 * @author Pat
 */
public class WordzSessionManager implements ViewListener{
    private String[] wordList;
    private ArrayList<WordzModel> sessions;
    private WordzModel currSession;
    
    /**
     * Creates a new session manager to keep track of the models
     * @param wordList The list of possible words
     */
    public WordzSessionManager(String[] wordList){
        this.wordList = wordList;
        sessions = new ArrayList<WordzModel>();
        currSession = new WordzModel(wordList);
    }
    
    /**
     * Connects the player to a session
     * @param proxy Reference to the view proxy
     * @param playerName name of the player joining
     */
    public void join(ViewProxy proxy, String playerName) {
        try{
            if(currSession == null) 
                    currSession = new WordzModel(wordList);
            if(currSession.getNumPlayers() == 0){
                currSession.addModelListener(proxy, playerName);
                proxy.setViewListener(currSession);
            }
            else{
                currSession.addModelListener(proxy, playerName);
                proxy.setViewListener(currSession);
                currSession = null;
            }
        }
        catch(Exception e){
            
        }
    }
    
    /**
     * Resets the Wordz game with a new word
     * @param word The word for the game
     */
    public void newGame() {}
    
    /**
     * Closes the game and instructs each player to close the game
     */
    public void endGame(){}
    
    /**
     * Adds a ModelListener to this session, also need to know the player's name
     * @param newPlayer The ModelListener to add
     * @param playerName The name of the player
     */
    public void addModelListener(ModelListener newPlayer, String playerName) {}
    
    /**
     * The currently active player takes a turn with the given guess
     * @param guess A single character string, the player's guess
     */
    public void takeTurn(String guess) {}
}
