
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
 * Passes incoming connections to the session manager
 * @author Pat
 */
public class WordzServer {
    public static void main(String[] args) throws IOException{
        ArrayList<String> wordList = new ArrayList<String>();
        
        if(args.length != 3){
            System.out.println("Usage: WordzServer <host> <port> <file>");
            System.exit(1);
        }
        
        try{
            BufferedReader in = new BufferedReader(new FileReader(args[2]));
            while(in.ready()){
                wordList.add(in.readLine());
            }
        }
        catch (FileNotFoundException ex) {
            System.out.println("File " + args[2] + " not found.");
            System.exit(1);
        }
        
        String[] wordListConv = new String[wordList.size()];
        for(int i = 0; i < wordList.size(); i++){
            wordListConv[i] = wordList.get(i);
        }
        
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(host, port));
        
        WordzSessionManager wordzMan = new WordzSessionManager(wordListConv);
        
        while(true){
            Socket clientSocket = serverSocket.accept();
            ViewProxy proxy = new ViewProxy(clientSocket);
            proxy.setViewListener(wordzMan);
        }
    }
}
