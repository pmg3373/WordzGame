
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Proxy object for the Wordz game
 * @author Pat
 */
class ModelProxy implements ViewListener{

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ModelListener modelListener;
    private ArrayList<String> guessedLetters;
    private char[] revealedLetters;
    private String  incorrectGuesses, status;
    
    /**
     * Creates a new ModelProxy object
     * @param socket Socket to connect to
     * @throws IOException 
     */
    ModelProxy(Socket socket) throws IOException {
        this.socket = socket;
        socket.setTcpNoDelay(true);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        guessedLetters = new ArrayList<String>();
        incorrectGuesses = "";
    }
    
    /**
     * Sets this objects reference to a model listener
     * @param modelListener Model listener reference
     */
    public void setModelListener(ModelListener modelListener){
        if(this.modelListener == null){
            this.modelListener = modelListener;
            new ReaderThread().start();
        }
        else{
            this.modelListener = modelListener;
        }
    }

    /**
     * Sends request to start new game
     * @throws IOException 
     */
    public void newGame() throws IOException {
        out.write('N');
        out.flush();
    }

    /**
     * Sends request to end the game
     */
    public void endGame() throws IOException{
        out.write('E');
        out.flush();
    }

    /**
     * Adds a reference to a model listener object
     * @param newPlayer Model listener reference
     * @param playerName Name of the new player
     */
    public void addModelListener(ModelListener newPlayer, String playerName) {}

    /**
     * Writes a join message
     * @param proxy Reference to the view proxy
     * @param playerName Name of the player
     * @throws IOException 
     */
    public void join(ViewProxy proxy, String playerName) throws IOException{
        out.write('J');
        out.writeUTF(playerName);
        out.flush();
    }

    /**
     * Writes a guess to the game
     * @param guess The guessed letter
     * @throws IOException 
     */
    public void takeTurn(String guess) throws IOException {
        if(status.equals("Your turn")){
            out.write('G');
            out.writeUTF(guess);
            out.flush();
        }
    }
    
    
    /**
     * Reads and interprets server messages
     */
    public class ReaderThread extends Thread{
        
        public void run(){
            while(true){
                try {
                    byte b = in.readByte();
                    if(b == 'G'){
                        incorrectGuesses = in.readUTF();
                        modelListener.updateIncorrect(incorrectGuesses);
                        
                    }
                    else if(b == 'R'){
                        int bytes = in.readInt();
                        revealedLetters = new char[bytes];
                        for(int i = 0; i < bytes; i++){
                            revealedLetters[i] = Character.toUpperCase((char)in.readByte());
                        }
                        modelListener.updateCorrect(revealedLetters);
                    }
                    else if(b == 'E'){
                        System.exit(0);
                    }
                    else if(b == 'M'){
                        status = in.readUTF();
                        modelListener.statusUpdate(status);
                    }
                    else{
                        System.err.println("Bad Message");
                    }
                } catch (IOException ex) {
                    
                }
                finally{
                    /*
                    try{
                        //socket.close();
                    }
                    catch(IOException e){

                    }
                    */
                }
            }
        }
    }
}
