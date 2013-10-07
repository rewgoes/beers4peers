/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import java.net.InetAddress;
import javax.swing.JTextArea;

/**
 *
 * @author rafael
 */
public final class Client {

    static private FindSupernodeThread findSupernode;
    private InetAddress supernode;
    private JTextArea saida1;
    private JTextArea saida2;

    public InetAddress getSupernode() {
        return supernode;
    }
    
    public Client(JTextArea s1, JTextArea s2) throws InterruptedException{
        saida1 = s1;
        saida2 = s2;
    }
    
    public void init() throws InterruptedException{
        findSupernode = new FindSupernodeThread();
         
        findSupernode.start();
        findSupernode.suspend();
        
        lookForSupernode();
        
        saida2.setText(null);
        saida2.append("Supernode:\n" + supernode);
        
        saida1.append("Connected to:\n" + supernode);
    }

    //When there's no connection to a supernode
    public void lookForSupernode() throws InterruptedException{
        synchronized (findSupernode){
            findSupernode.suspend = false;
            findSupernode.resume();
            findSupernode.wait();
            supernode = findSupernode.getSupernode();
            System.out.print("Supernode found: " + supernode);
            findSupernode.suspend();
        }
    }
    
}
