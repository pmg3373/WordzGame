
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Runs to Wordz game, keeping track of the game state and notifying players of changes
 * @author Pat
 */
public class WordzModel implements ViewListener{
    
    /*************************private fields********************************/
    private String word, incorrectGuesses;
    private ArrayList<String> guessedLetters, correctGuesses;
    private char[] revealedLetters;
    private ModelListener[] players;
    private String[] playerNames;
    private String[] wordList;
    private int numPlayers;
    
    /**
     * Returns the number of players in this session
     * @return The number of players in the session
     */
    public synchronized int getNumPlayers(){
        return numPlayers;
    }
    
    private int activePlayer;
    //0: Not Started, 1: p1, 2: p2, 3: Game won p1, 4: Game won p2
    
    /*************************private fields********************************/
    
    /*************************Wordz Game Setup********************************/
    /**
     * Create a new WordzModel session
     * @param wordList List of possible words to play with
     */
    public WordzModel(String[] wordList){
        numPlayers = 0;
        playerNames = new String[2];
        players = new ModelListener[2];
        activePlayer = 0;
        this.wordList = wordList;
        newGame(getRandomWord());
    }
    
    /**
     * Returns a random word from the stored words list
     * @return A random string from the words list
     */
    private synchronized String getRandomWord(){
        return wordList[(int)(Math.random() * wordList.length)];
    }
    
    /**
     * Resets the Wordz game with a new random word
     */
    public synchronized void newGame(){
        newGame(getRandomWord());
        startGame();
    }
    
    /**
     * Puts the game into the start state for each player
     */
    private synchronized void startGame() {
        activePlayer = 1;
        try{
            players[0].updateCorrect(revealedLetters);
            players[0].updateIncorrect(incorrectGuesses);
            players[0].statusUpdate(playerMessage(1));

            players[1].updateCorrect(revealedLetters);
            players[1].updateIncorrect(incorrectGuesses);
            players[1].statusUpdate(playerMessage(2));
        }
        catch(Exception e){
            System.err.println("Caught IO exception:" + e.getMessage());
        }
    }
    
    /*************************Connection Setup********************************/
    
    /**
     * Adds a ModelListener to this session, also need to know the player's name
     * @param newPlayer The ModelListener to add
     * @param playerName The name of the player
     */
    public synchronized void addModelListener(ModelListener newPlayer, String playerName){
        try{
            if(numPlayers < 2){
                //Join up the viewproxy to the game and then start if we have two players
                players[numPlayers] = newPlayer;
                playerNames[numPlayers] = playerName;
                numPlayers++;
                if(numPlayers == 2){
                    startGame();
                }
                else{
                    players[0].statusUpdate(playerMessage(1));
                }
            }
            else{
                //Something has gone wrong?
            }
            
        }
        catch(Exception e){
            
        }
    }
    
    /**
     * Connects the player to a session
     * @param proxy Reference to the view proxy
     * @param playerName name of the player joining
     */
    public synchronized void join(ViewProxy proxy, String playerName){}
    
    /*************************Connection Setup********************************/
    
    /*************************Wordz Game Setup********************************/
    
    
    /*************************Wordz Game Actions********************************/
    
    /**
     * Passes the turn to the other player.
     */
    private synchronized void passTurn(){
        if(activePlayer == 1){
            activePlayer = 2;
        }
        else{
            activePlayer = 1;
        }
    }
    
    /**
     * Retrieves the message that should be displayed for the given player
     * @param player The player number, 1/2
     * @return The message to be displayed
     */
    private synchronized String playerMessage(int player){
        if(player == 1){
            if(activePlayer == 0){
                return "Waiting for partner";
            }
            else if(activePlayer == 1){
                return "Your turn";
            }
            else if(activePlayer == 2){
                return playerNames[1] + "'s turn";
            }
            else if(activePlayer == 3){
                return "You win!";
            }
            else{
                return playerNames[1] + " wins!";
            }
        }
        else{
            if(activePlayer == 1){
                return playerNames[0] + "'s turn";
            }
            else if(activePlayer == 2){
                return "Your turn";
            }
            else if(activePlayer == 3){
                return playerNames[0] + " wins!";
            }
            else{
                return "You win!";
            }
        }
    }
    
    /**
     * The currently active player takes a turn with the given guess
     * @param guess A single character string, the player's guess
     */
    public synchronized void takeTurn(String guess){
        if(!guessLetter(guess)){
            //If the player guessed wrong, pass the turn and tell each player
            //the guessed letter and whose turn it is
            passTurn();
            try{
                players[0].updateIncorrect(incorrectGuesses);
                players[0].statusUpdate(playerMessage(1));
                
                players[1].updateIncorrect(incorrectGuesses);
                players[1].statusUpdate(playerMessage(2));
            }
            catch(Exception e){
                
            }
        }
        else{
            
            //Did someone win the game?
            boolean gameWon = true;
            for(int i = 0; i < revealedLetters.length; i++){
                if(revealedLetters[i] == '*'){
                    gameWon = false;
                    break;
                }
            }
            if(gameWon)
                activePlayer += 2; // 1 -> 3: p1 wins, 2 -> 4 p2 wins
            
            //Either way both players need to be updated on the details of the game
            try{
                players[0].statusUpdate(playerMessage(1));
                players[0].updateCorrect(revealedLetters);

                players[1].statusUpdate(playerMessage(2));
                players[1].updateCorrect(revealedLetters);
            }
            catch(Exception e){
                
            }
        }
    }

    /**
     * Closes the game and instructs each player to close the game
     */
    public synchronized void endGame(){
        try{
        players[0].endGame();
        if(numPlayers == 2)
            players[1].endGame();
        numPlayers = 0;
        }
        catch(Exception e){
            
        }
    }
    
    /**
     * Resets the Wordz game with a new word
     * @param word The word for the game
     */
    public synchronized void newGame(String word){
        this.word = word;
        guessedLetters = new ArrayList<String>();
        incorrectGuesses = "";
        revealedLetters = new char[word.length()];
        correctGuesses = new ArrayList<String>();
        for(int i = 0; i < word.length(); i++){
            revealedLetters[i] = '*';
        }
    }
    
    /**
     * Guess whether or not a letter is in the game word
     * @param guess a single character, the guess
     * @return True if the character is in the word, false otherwise
     */
    public synchronized boolean guessLetter(String guess){
        String guessToCap = guess.toUpperCase();
        if((!guessedLetters.contains(guess) && !correctGuesses.contains(guess)) &&
                (!guessedLetters.contains(guessToCap) && !correctGuesses.contains(guessToCap))){
            if(word.contains(guess)){
                for (int i = 0; i < word.length(); i++) {
                    if(word.charAt(i) == guess.charAt(0)){
                        revealedLetters[i] = guess.charAt(0);
                    }
                }
                correctGuesses.add(guess);
                return true;
            }
            else if(word.contains(guessToCap)){
                for (int i = 0; i < word.length(); i++) {
                    if(word.charAt(i) == guessToCap.charAt(0)){
                        revealedLetters[i] = guessToCap.charAt(0);
                    }
                }
                correctGuesses.add(guessToCap);
                return true;
            }
            else{
                incorrectGuesses = incorrectGuesses.concat(guessToCap);
                guessedLetters.add(guessToCap);
                return false;
            }
        }
        else{
            return false;
        }
    }
    
    /*************************Wordz Game Actions********************************/
}
