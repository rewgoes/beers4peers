/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

/**
 *
 * @author rafael
 */
public class ClientList {
    
    private JTextArea saida2;
    private List<String> clients;
    
    public ClientList(JTextArea s2) {
        saida2 = s2;
        clients = new ArrayList<String>();
    }
    
    public void add(String message){
        synchronized (this) {
            clients.add(message);
            showNeighbors();
        }
    }
    
    public void remove(String message){
        synchronized (this) {
            clients.remove(message);
            showNeighbors();
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
    
    public void showNeighbors(){
        synchronized (this) {
            saida2.setText(null);
            for (String neighbor : clients) {
                saida2.append(neighbor.toString() + "\n");
            }
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
