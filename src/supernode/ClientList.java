package supernode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author rafael(rewgoes), matheus, andre
 * 
 */

//Represent the client information
class ClientInfo{
    
    private String clientAddress;
    
    public ClientInfo (String clientAddress){
        this.clientAddress = clientAddress;
    }
 
    //Compares two supernodes
    public boolean equals(String supernodeAddress) {
        if (this.clientAddress.equals(supernodeAddress)){
            return true;
        }
        return false;
    }
    
    @Override
    public String toString(){
        return clientAddress;
    }
}


//Class that holds all clients known by a supernode
public class ClientList {
    
    private List<ClientInfo> clients;
    
    public ClientList(){
        this.clients = new ArrayList<ClientInfo>();
    }
    
    //Add a new supernode to the application
    public void add(String clientAddress){
        clients.add(new ClientInfo(clientAddress));
    }
    
    //Check if client's addres is known
    public boolean contains(String supernodeAddress){
        for(Iterator<ClientInfo> i = clients.iterator(); i.hasNext(); ) {
            ClientInfo client = i.next();
            if (client.equals(supernodeAddress))
                return true;
        }          
        return false;
    }
    
    //Get client address in the index i
    public String get(int i){
        return this.clients.get(i).toString();
    }
    
    //Return the number of known clients
    public int size(){
        return clients.size();
    }

    //Remove client from list
    public void removeClient(String client) {
        
        for(Iterator<ClientInfo> i = clients.iterator(); i.hasNext(); ) {
            ClientInfo clientTemp = i.next();
            if (clientTemp.equals(client)){
                clients.remove(clientTemp);
                break;
            }
        }
    }
    
}
