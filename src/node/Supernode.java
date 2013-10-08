/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import javax.swing.JTextArea;

/**
 *
 * @author rafael
 */
public class Supernode {
    
    private JTextArea output1;
    private JTextArea output2;

    static private SupernodeListenerClientThread listenBroadcast;
    
    //Check if supernode has been initialized
    private boolean initialized = false;
    
    //Initialize client by calling its threads
    public void init() throws InterruptedException{
        if (!initialized){
            listenBroadcast = new SupernodeListenerClientThread(output1, output2);

            listenBroadcast.start();
        }
    }

    public Supernode(JTextArea jTextArea1, JTextArea jTextArea2) {
        output1 = jTextArea1;
        output2 = jTextArea2;
    }
    
}

