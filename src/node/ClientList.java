/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

/**
 *
 * @author rafael
 */

class clientInfo{
    
    public InetAddress clientAddress;
    public int clientPort;
    public InetAddress supernodeAddress;
    public int supernodePort;
    
}

public class ClientList {
    
    private List<String> clients;
    
    public ClientList() {
        clients = new ArrayList<String>();
    }
    
    public void add(String message){
        synchronized (this) {
            clients.add(message);
        }
    }
    
    public void remove(String message){
        synchronized (this) {
            clients.remove(message);
        }
    }
    
    public int size(){
        synchronized (this) {
            return clients.size();
        }
    }
    
    public String get(int index){
        synchronized (this) {
            return clients.get(index);
        }
    }

    public String getNodes() {
        synchronized (this) {
            String returner = new String();
            for (String neighbor : clients) {
                returner = returner + neighbor;
                returner += "-";
            }
            return returner;
        }
    }
    
}
