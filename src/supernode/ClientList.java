/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supernode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author rafael
 */

class ClientInfo{
    
    private String clientAddress;
    private int clientPort;
    
    public ClientInfo (String clientAddress, int clientPort){
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }
 
    //Compares two supernodes
    public boolean equals(String supernodeAddress, int supernodePort) {
        if (this.clientAddress.equals(supernodeAddress) && this.clientPort == supernodePort){
            return true;
        }
        return false;
    }
    
    @Override
    public String toString(){
        return clientAddress;
    }
}

public class ClientList {
    
    private List<ClientInfo> clients;
    
    public ClientList(){
        this.clients = new ArrayList<ClientInfo>();
    }
    
    //Add a new supernode to the application
    public void add(String clientAddress, int clientPort){
        clients.add(new ClientInfo(clientAddress, clientPort));
    }
    
    public boolean contains(String supernodeAddress, int port){
        for(Iterator<ClientInfo> i = clients.iterator(); i.hasNext(); ) {
            ClientInfo client = i.next();
            if (client.equals(supernodeAddress, port))
                return true;
        }          
        return false;
    }
    
    public String get(int i){
        return this.clients.get(i).toString();
    }
    
    public int size(){
        return clients.size();
    }
    
}
