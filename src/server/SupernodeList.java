/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Interface.Beers4Peers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import supernode.ClientList;

/**
 *
 * @author rafael
 */

//Class that holds all the supernode information in server
class SupernodeInfo{
    
    private ClientList clients;
    private String supernodeAddress;
    private int supernodePort;
    
    public SupernodeInfo (String supernodeAddress, int supernodePort){
        this.supernodeAddress = supernodeAddress;
        this.supernodePort = supernodePort;
        this.clients = new ClientList();
    }
    
    //Compares two supernodes
    public boolean equals(String supernodeAddress, int supernodePort) {
        if (this.supernodeAddress.equals(supernodeAddress) && this.supernodePort == supernodePort){
            return true;
        }
        return false;
    }
    
    public int clientQtd(){
        return clients.size();
    }
    
    public void addClient(String clientAddress){
        clients.add(clientAddress);
    }
    
    //Return the supernode with less clients
    public boolean lessBusy(SupernodeInfo supernode){
        return this.clientQtd() < supernode.clientQtd();
    }
    
    @Override
    public String toString(){
        
        return (this.supernodeAddress);
        
        //From now, the application has a unique port being used, so the port is not important
        //return (this.supernodeAddress.toString() + this.supernodePort);
    }
}

public class SupernodeList {
 
    private List<SupernodeInfo> supernodes;
    
    public SupernodeList(){
        this.supernodes = new ArrayList<SupernodeInfo>();
    }
    
    //Add a new supernode to the application
    public void addSupernode(String supernodeAddress, int port){
        supernodes.add(0, new SupernodeInfo(supernodeAddress, port));
        
        // TODO: Implement a new funcion to split clients
    }
    
    
    //Check if the supernode already exists
    public boolean containsSupernode(String supernodeAddress, int port){
        for(Iterator<SupernodeInfo> i = supernodes.iterator(); i.hasNext(); ) {
            SupernodeInfo supernode = i.next();
            if (supernode.equals(supernodeAddress, port))
                return true;
        }          
        return false;
    }
    
    //Add client to the best supernode at the moment and return supernodes's address:port
    public String addClient(String clientAddress, int clientPort){
        SupernodeInfo supernode;
        
        orderSupernodes();
       
        for(Iterator<SupernodeInfo> i = supernodes.iterator(); i.hasNext(); ) {
            supernode = i.next();
            try {
                if(supernodeAvailableToClient(supernode.toString(), clientAddress)){
                    supernode.addClient(clientAddress);
                    return supernode.toString();
                }
            } catch (IOException ex) {
                System.err.println("Error: Server: " + ex.getMessage());
            }
        } 
        
        return null;
    }
    
    //Select less busy supernode
    public void orderSupernodes(){
        
        SupernodeInfo supernodeTemp;
        
        for(int i = 0; i < supernodes.size() - 1; i++) {
            if (supernodes.get(i+1).lessBusy(supernodes.get(i))){
                supernodeTemp = supernodes.get(i);
                supernodes.set(i, supernodes.get(i+1));
                supernodes.set(i+1, supernodeTemp);
            }
        }
    }

    private boolean supernodeAvailableToClient(String supernode, String client) throws IOException {
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;

        try {
            //Try to connect with a 1,5 seconds timeout
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(supernode, Beers4Peers.PORT), 1500);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        } catch (UnknownHostException ex) {
            System.err.println("Error: Server (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
            return false;
        } catch (IOException ex) {
            System.err.println("Error: Server (Couldn't get I/O for the connection to: " + 
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
            return false;
        }

        String fromServer;
        String fromSupernode;

        fromServer = client;

        out.println(fromServer);

        fromSupernode = in.readLine();

        if(fromSupernode == null){
            System.err.println("Error: Server (Some undefined reason)");
        }
        else {
            out.close();
            in.close();
            connectionSocket.close();
        
            System.out.println("Control: Client " +  client + " connected to supernode " + supernode);
            
            return true;
        }
        
        out.close();
        in.close();
        connectionSocket.close();
        
        return false;
    }
    
}