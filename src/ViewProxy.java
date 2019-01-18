
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Proxy object for the Wordz Client
 * @author Pat
 */
class ViewProxy implements ModelListener {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ViewListener viewListener;
    
    /**
     * Create a new view proxy
     * @param clientSocket Socket of the incoming client
     * @throws SocketException
     * @throws IOException 
     */
    ViewProxy(Socket clientSocket) throws SocketException, IOException {
        socket = clientSocket;
        socket.setTcpNoDelay(true);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }
    
    /**
     * Sets the viewListener for this ViewProxy
     * @param viewListener The ViewListener
     */
    void setViewListener(ViewListener viewListener) {
        if(this.viewListener == null){
            this.viewListener = viewListener;
            new ReaderThread().start();
        }
        else{
            this.viewListener = viewListener;
        }
    }

    /**
     * Writes the incorrect guesses
     * @param incorrectGuesses All of the incorrect guesses
     * @throws IOException 
     */
    public void updateIncorrect(String incorrectGuesses) throws IOException {
        out.writeByte('G');
        out.writeUTF(incorrectGuesses);
        out.flush();
    }

    /**
     * Writes the word with the revealed letters and * in the other places
     * @param revealedLetters
     * @throws IOException 
     */
    public void updateCorrect(char[] revealedLetters) throws IOException{
        out.writeByte('R');
        out.writeInt(revealedLetters.length);
        for(int i = 0; i < revealedLetters.length; i++){
            out.writeByte(revealedLetters[i]);
        }
        out.flush();
    }

    /**
     * Issues an order to end the game
     * @throws IOException 
     */
    public void endGame() throws IOException {
        out.writeByte('E');
        out.flush();
    }

    /**
     * Writes a status message update to the clients
     * @param status The new status message
     * @throws IOException 
     */
    public void statusUpdate(String status) throws IOException {
        out.writeByte('M');
        out.writeUTF(status);
        out.flush();
    }

    /**
     * Reads input from clients and determines appropriate actions
     */
    private class ReaderThread extends Thread{
        public void run(){
            try{
                while(true){
                    String playerName;
                    byte b = in.readByte();
                    if(b == 'J'){
                        playerName = in.readUTF();
                        viewListener.join(ViewProxy.this, playerName);
                    }
                    else if(b == 'G'){
                        viewListener.takeTurn(in.readUTF());
                    }
                    else if(b == 'E'){
                        viewListener.endGame();
                    }
                    else if(b == 'N'){
                        viewListener.newGame();
                    }
                    else{
                        System.err.println("Bad Message");
                    }
                }
            }
            catch(IOException e){
                
            }
            finally{
                /*
                try{
                    //System.out.println("DEBUG: ViewProxy Closing Socket");
                    //socket.close();
                }
                catch(IOException e){
                    
                }*/
            }
        }
    }

    
    
}
