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

    private final static int PACKETSIZE = 100 ;

    static private SupernodeListenerClientThread listenBroadcast;
    static private byte[] message;

    public static void main(String[] args){
        listenBroadcast = new SupernodeListenerClientThread();
        
        listenBroadcast.start();
    }

    public Supernode(JTextArea jTextArea1, JTextArea jTextArea2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

