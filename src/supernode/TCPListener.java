/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supernode;

import Interface.Beers4Peers;
import java.io.IOException;
import java.net.ServerSocket;
import static supernode.Supernode.myAddress;

/**
 *
 * @author rafael
 */
public class TCPListener extends Thread{

    Supernode supernode;
    
    public TCPListener(Supernode aThis) {
        supernode = aThis;
    }
    
    @Override
    public void run(){
        ServerSocket supernodeSocket = null;
        boolean listening = true;
            
        try {
            //Create a new socket at the port passed as argument
            supernodeSocket = new ServerSocket(Beers4Peers.PORT);
            System.out.println("Control: Supernode created at " + myAddress);
        } catch (IOException ex) {
            System.err.println("Error: Supernode (Could not listen on port: " + Beers4Peers.PORT + "): " + ex.getMessage());
            System.exit(1);
        }
        
        while(listening){
            try {
                //Wait for connection, and when one starts, it starts a new thread
                new SupernodeTCPListenerThread(supernodeSocket.accept(), supernode).start();
    
                System.out.println("Control: Listening for commands at port " + Beers4Peers.PORT);
            } catch (IOException ex) {
                System.err.println("Error: Supernode (Accept failed): " + ex.getMessage());
                System.exit(1);
            }
        }
        
        try {
            supernodeSocket.close();
        } catch (IOException ex) {
            System.err.println("Error: Server (Could close socket): " + ex.getMessage());
            System.exit(1);
        }
    }
    
}
