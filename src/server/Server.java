/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;

/**
 *
 * @author rafael
 */
public class Server {
    
    private static SupernodeList supernodeList;
    private static String myAddress;
    
    //Start a server in the application, it must be unique
    public static void main(String args[]) {
        
        //Get local address
        getAddress();
        
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
    
    private static void getAddress(){
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback())
                    continue;    // Don't want to broadcast to the loopback interface
                // If not loopback, get its broadcast address
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                        continue;
                    else{
                        myAddress = interfaceAddress.getAddress().getHostAddress();
                    }
                }
            }
            if (myAddress == null){
                System.err.println("Error: Server (There's no connection)");
                System.exit(1);
            }
        } catch (Exception ex) {
            System.err.println("Error: Server (Could not retrieve address): " + ex.getMessage());
            System.exit(1);
        }
    }
    
    private static void listenConnection(int port){
        ServerSocket serverSocket = null;
        boolean listening = true;
            
        try {
            //Create a new socket at the port passed as argument
            serverSocket = new ServerSocket(port);
            System.out.println("Control: Server created at " + myAddress);
        } catch (IOException ex) {
            System.err.println("Error: Server (Could not listen on port: " + port + "): " + ex.getMessage());
            System.exit(1);
        }
        
        while(listening){
            
            try {
                System.out.println("Control: Listening for connection at port " + port);
                
                //Wait for connection, and when one starts, it starts a new thread
                new ServerThread(serverSocket.accept(), supernodeList).start();
            } catch (IOException ex) {
                System.err.println("Error: Server (Accept failed): " + ex.getMessage());
                System.exit(1);
            }
        }
        
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println("Error: Server (Could close socket): " + ex.getMessage());
            System.exit(1);
        }
    }
    
}
