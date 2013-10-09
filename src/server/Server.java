/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import node.ClientList;
import supernode.SupernodeList;

/**
 *
 * @author rafael
 */
public class Server {
    
    private static SupernodeList supernodeList;
    
    //Start a server in the application, it must be unique
    public static void main(String args[]) {
        
        //Create the control list, where supernodes are added and clients are added to these supernodes
        supernodeList = new SupernodeList();
        
        //Check if argument's port is valid to the application
        if( args.length != 1 ){
           System.out.println("usage: Server port");
           return;
        }
        
        // Convert the argument to ensure that is it valid
        int port = Integer.parseInt(args[0]);
        
        if (port >= 49152 && port <= 65535)
            listenConnection(port);
        else {
            System.out.println( "usage: Server port [49152-65535]" );
            return;
        }
    }
    
    private static void listenConnection(int port){
        ServerSocket serverSocket = null;
        boolean listening = true;
            
        try {
            //Create a new socket at the port passed as argument
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Error: Could not listen on port: " + port);
            System.exit(1);
        }
        
        while(listening){
            
            try {
                System.out.println("Control: Listening for connection at port " + port);
                
                //Wait for connection, and when one starts, it starts a new thread
                new ServerThread(serverSocket.accept(), supernodeList).start();
            } catch (IOException e) {
                System.err.println("Error: Accept failed.");
                System.exit(1);
            }
        }
        
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println("Error: Could close socket");
            System.exit(1);
        }
    }
    
}
