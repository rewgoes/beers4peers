/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import java.io.BufferedReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import Interface.Beers4Peers;
import static java.lang.Thread.sleep;

/**
 *
 * @author rafael
 */
public class ClientConnectThread extends Thread{
    
    private int broadcastPort = Beers4Peers.PORT; //Port used to receive broadcast messages
    private DatagramSocket socket = null;
    private BufferedReader in = null;
    private List<InetAddress> broadcastList; //List of possible broadcast addresses
    private byte[] message; //Message used to broadcast
    private DatagramPacket packet; //Packet to send in broadcast
    public boolean suspend;
    private String myAddress;
    private InetAddress supernode;

    public InetAddress getSupernode() {
        return supernode;
    }
    
    public String getMyAddress(){
        return myAddress;
    }
    
    public ClientConnectThread() throws InterruptedException{
        //Verity possible intefaces that are not loopback
        suspend = true;
        
        broadcastList = new ArrayList<InetAddress>();
        
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback())
                    continue;    // Don't want to broadcast to the loopback interface
                // If not loopback, get its broadcast address
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                        continue;
                    else{
                        myAddress = interfaceAddress.getAddress().getHostAddress();
                        broadcastList.add(broadcast);
                        System.out.println("Control: Broadcast address: " + broadcast);
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error: BroadcastThread: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                while(suspend)
                    sleep(1000);
                
                for (InetAddress broadcastAddress : broadcastList) {

                    this.message = "wantToConnect".getBytes();
                    socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    packet = new DatagramPacket(message, message.length, broadcastAddress, broadcastPort);
                    socket.send(packet); //send broadcast packet

                    socket.setSoTimeout(1000);
                    socket.receive(packet);

                    // Print the packet
                    System.out.println("Control: Answer from " + packet.getAddress() + ":" + packet.getPort() + " Message: " +  new String(packet.getData(), packet.getOffset(), packet.getLength()) ) ;
                    supernode = packet.getAddress();
                    
                    if (supernode != null){
                        System.out.println("Control: Supernode set to " + supernode);
                        synchronized (this){
                            this.notify();
                        }
                    }
                    else
                        System.err.println("Error: Problem setting supernode");

                    socket.close();
                    
                    sleep(1000);
               }
            } catch (Exception ex) {
                System.err.println("Error: BroadcastThread: " + ex.getMessage());            
            }
        }
    }
    
}
