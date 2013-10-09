/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supernode;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author rafael
 */

//Class that holds all the client information in its own supernode in server
class ClientInfo{
    
    private InetAddress clientAddress;
    private int clientPort;
    
    public ClientInfo (InetAddress clientAddress, int clientPort){
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }
    
}

//Class that holds all the supernode information in server
class SupernodeInfo{
    
    private List<ClientInfo> clients;
    private InetAddress supernodeAddress;
    private int supernodePort;
    
    public SupernodeInfo (InetAddress supernodeAddress, int supernodePort){
        this.supernodeAddress = supernodeAddress;
        this.supernodePort = supernodePort;
        this.clients = new ArrayList<ClientInfo>();
    }
    
    //Compares two supernodes
    public boolean equals(InetAddress supernodeAddress, int supernodePort) {
        if (this.supernodeAddress == supernodeAddress && this.supernodePort == supernodePort){
            return true;
        }
        return false;
    }
    
    public int clientQtd(){
        return clients.size();
    }
    
    public void addClient(InetAddress clientAddress, int clientPort){
        clients.add(new ClientInfo(clientAddress, clientPort));
    }
    
    @Override
    public String toString(){
        
        return (this.supernodeAddress.toString().split("/")[1]);
        
        //From now, the application has a unique port being used, so the port is not important
        //return (this.supernodeAddress.toString() + this.supernodePort);
    }
}

public class SupernodeList {
 
    private List<SupernodeInfo> supernodes;
    private int maxClientQtd;
    
    public SupernodeList(){
        this.supernodes = new ArrayList<SupernodeInfo>();
        this.maxClientQtd = 0;
    }
    
    //Add a new supernode to the application
    public void addSupernode(InetAddress supernodeAddress, int port){
        supernodes.add(new SupernodeInfo(supernodeAddress, port));
        
        // TODO: Implement a new funcion to split clients
    }
    
    public boolean containsSupernode(InetAddress supernodeAddress, int port){
        for(Iterator<SupernodeInfo> i = supernodes.iterator(); i.hasNext(); ) {
            SupernodeInfo supernode = i.next();
            if (supernode.equals(supernodeAddress, port))
                return true;
        }          
        return false;
    }
    
    //Add client to the best supernode at the moment and return supernodes's address:port
    public String addClient(InetAddress clientAddress, int clientPort){
        this.maxClientQtd++;
        
        SupernodeInfo supernodeTemp = selectSupernode();
        
        supernodeTemp.addClient(clientAddress, clientPort);
        
        return supernodeTemp.toString();
    }
    
    //Select less busy supernode
    public SupernodeInfo selectSupernode(){
        
        SupernodeInfo supernodeTemp = null;
        int tempQtd = maxClientQtd;
        
        for(Iterator<SupernodeInfo> i = supernodes.iterator(); i.hasNext(); ) {
            SupernodeInfo supernode = i.next();
            if (supernode.clientQtd() <= tempQtd)
                supernodeTemp = supernode;
        } 
        
        return supernodeTemp;
    }
    
}