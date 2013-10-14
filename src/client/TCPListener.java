/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import supernode.*;
import Interface.Beers4Peers;
import java.io.IOException;
import java.net.ServerSocket;
import static client.Client.myAddress;

/**
 *
 * @author rafael
 */
public class TCPListener extends Thread{

    Client client;
    ServerSocket supernodeSocket;
    boolean listening;
    
    public TCPListener(Client aThis) {
        client = aThis;
    }
    
    public void closeSocket(){
        try {
            synchronized(this){
                listening = true;
            }
            supernodeSocket.close();
        } catch (IOException ex) {
            System.err.println("Error: TCPListener (Could not close socket): " + ex.getMessage());
        }
    }
    
    @Override
    public void run(){
        supernodeSocket = null;
        
        listening = true;
            
        try {
            //Create a new socket at the port passed as argument
            supernodeSocket = new ServerSocket(Beers4Peers.PORT);
            System.out.println("Control: Client created at " + myAddress);
        } catch (IOException ex) {
            System.err.println("Error: TCPListener (Could not listen on port: " + Beers4Peers.PORT + "): " + ex.getMessage());
            return;
        }
        
        while(listening){
            try {
                System.out.println("Control: Listening for commands at port " + Beers4Peers.PORT);
                
                //Wait for connection, and when one starts, it starts a new thread
                new ClientTCPThread(supernodeSocket.accept(), client).start();
            } catch (IOException ex) {
                System.err.println("Error: TCPListener (Accept failed): " + ex.getMessage());
                return;
            }
        }
        
        try {
            supernodeSocket.close();
        } catch (IOException ex) {
            System.err.println("Error: TCPListener (Couldn't close socket): " + ex.getMessage());
        }
    }
    
}
