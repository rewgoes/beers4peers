/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supernode;

import Interface.Beers4Peers;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static supernode.Supernode.myAddress;

/**
 *
 * @author rafael
 */
public class TCPListener extends Thread{

    Supernode supernode;
    ServerSocket supernodeSocket;
    boolean listening;
    
    public TCPListener(Supernode aThis) {
        supernode = aThis;
    }
    
    public void closeSocket(){
        try {
            synchronized(this){
                listening = true;
            }
            supernodeSocket.close();
        } catch (IOException ex) {
            System.err.println("Error: TCPListener (Could not close socket): " + ex.getMessage());
            return;
        }
    }
    
    @Override
    public void run(){
        supernodeSocket = null;
        
        listening = true;
            
        try {
            //Create a new socket at the port passed as argument
            supernodeSocket = new ServerSocket(Beers4Peers.PORT);
            System.out.println("Control: Supernode created at " + myAddress);
        } catch (IOException ex) {
            System.err.println("Error: Supernode (Could not listen on port: " + Beers4Peers.PORT + "): " + ex.getMessage());
            return;
        }
        
        while(listening){
            try {
                System.out.println("Control: Listening for commands at port " + Beers4Peers.PORT);
                
                //Wait for connection, and when one starts, it starts a new thread
                new SupernodeTCPThread(supernodeSocket.accept(), supernode).start();
            } catch (IOException ex) {
                System.err.println("Error: Supernode (Accept failed): " + ex.getMessage());
                return;
            }
        }
        
        try {
            supernodeSocket.close();
        } catch (IOException ex) {
            System.err.println("Error: Server (Could close socket): " + ex.getMessage());
            return;
        }
    }
    
}
