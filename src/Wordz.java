
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Starts a Wordz client and connects to WordzServer
 * @author Pat
 */
public class Wordz {
    public static void main(String[] args) throws IOException{
        if(args.length != 3){
            System.out.println("Wordz <host> <port> <playername>");
            System.exit(1);
        }
        
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String playerName = args[2];
        
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        WordzUI view = WordzUI.create(playerName);
        ModelProxy proxy = new ModelProxy(socket);
        proxy.setModelListener(view);
        view.setViewListener(proxy);
        proxy.join(null, playerName);
    }
}
