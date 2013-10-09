/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import Interface.Beers4Peers;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author rafael
 */
public class SupernodeListenerClientThread extends Thread{
    
    private int broadcastPort = Beers4Peers.PORT; //Port used to receive broadcast messages
    private byte[] message; //Message used to broadcast
    private DatagramSocket socket;
    private DatagramPacket packet;
    private byte[] packetBytes;
    
    private JTextArea output1;
    private JTextArea output2;
    
    public SupernodeListenerClientThread(JTextArea output1, JTextArea output2) {
        
        this.output1 = output1;
        this.output2 = output2;
        
        try {
           socket = new DatagramSocket(broadcastPort);
           packetBytes = new byte["wantToConnect".length()*2];
           packet = new DatagramPacket(packetBytes, packetBytes.length);
           System.out.println("Control: The server is ready...") ;
        } catch (Exception ex) {
           System.err.println("Error: ListenBroadcastThread " + ex.getMessage());         
        }
        
        
    }
    
    @Override
    public void run() {
        while(true){
            try {
                // Receive message
                socket.receive(packet);
                
                if (new String(packet.getData(), packet.getOffset(), packet.getLength()).equals("wantToConnect")){
                    // Print the packet
                    System.out.println("Control: Broadcast from " + packet.getAddress() + ":" + packet.getPort() + " Message: " + new String(packet.getData(), packet.getOffset(), packet.getLength())) ;

                    message = "OK".getBytes();

                    packet.setData(message);
                }

                // Return the packet to the sender
                socket.send(packet);
                
                socket.receive(packet);
                System.out.println("Control: I'm here");
            } catch (IOException ex) {
                Logger.getLogger(SupernodeListenerClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
