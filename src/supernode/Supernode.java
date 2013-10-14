/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supernode;

import Interface.Beers4Peers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JTextArea;

/**
 *
 * @author rafael
 */
public class Supernode {
    
    protected JTextArea output1;
    protected JTextArea output2;
    
    //Check if client is connected
    protected boolean connected = false;
    
    //Control interface's button
    protected boolean[] buttonContol;
    
    //Check if supernode has been initialized
    protected boolean initialized = false;
    
    protected static String myAddress;
    
    //TODO: Supernode: Create a list of clients
    protected ClientList clientList;
    
    //Hashtable of files
    protected Hashtable<String, String> files;
            
    private TCPListener tcpListener;
    

    public Supernode(JTextArea jTextArea1, JTextArea jTextArea2, boolean[] buttonContol) {
        this.output1 = jTextArea1;
        this.output2 = jTextArea2;
        this.buttonContol = buttonContol;
        this.clientList = new ClientList();
        this.files = new Hashtable<String, String>();
        
    }
    
    //Initialize client by calling its threads
    public void connect() throws IOException {
        if (!connected){
            Socket connectionSocket;
            PrintWriter out;
            BufferedReader in;

            try {
                connectionSocket = new Socket();
                connectionSocket.connect(new InetSocketAddress(Beers4Peers.SERVER_ADDRESS, Beers4Peers.SERVER_PORT), 1000);
                out = new PrintWriter(connectionSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                
            } catch (UnknownHostException ex) {
                System.err.println("Error: Supernode (Don't know about host: " +
                        Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
                
                output1.append("Failed to connect: Server is offline\n");
                
                return;
            } catch (IOException ex) {
                System.err.println("Error: Supernode (Couldn't get I/O for the connection to: " + 
                        Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
                
                output1.append("Failed to connect: Server is offline\n");
                
                return;
            }

            String fromServer;
            String fromUser;
            
            fromUser = "supernode";
            
            out.println(fromUser);

            fromServer = in.readLine();
            
            if(fromServer == null){
                System.err.println("Error: Supernode (Some undefined reason)");
                    
                output1.append("Failed to connect: Server is offline\n");

                return;
            }
            else {
                connected = true;
                this.buttonsControl();

                out.close();
                in.close();
                connectionSocket.close();

                output2.setText(null);
                output2.append("Clients:\n");

                output1.append("Connected to server successfully\n");
            }
        }
        
        this.getAddress();
        
        tcpListener = new TCPListener(this);
        tcpListener.start();
        //SupernodeUDPCheckAlive udpAlive = new SupernodeUDPCheckAlive(this);
    }
    
    //Control interface's buttons
    protected void buttonsControl(){
        if(connected){
            buttonContol[0] = false;
            buttonContol[1] = false;
            buttonContol[2] = false;
            buttonContol[3] = false;
            buttonContol[4] = true;
        } else {
            buttonContol[0] = false;
            buttonContol[1] = false;
            buttonContol[2] = false;
            buttonContol[3] = false;
            buttonContol[4] = true;
        }
    }
    
    //This method only gets the supernodes local address
    protected static void getAddress(){
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
                    }
                }
            }
            if (myAddress == null){
                System.err.println("Error: Server (There's no connection)");
                System.exit(1);
            }
        } catch (Exception ex) {
            System.err.println("Error: Server (Could not retrieve address): " + ex.getMessage());
            System.exit(1);
        }
    }
    
    //List all clients on the interface
    protected void listClients() {
        output2.setText("Clients:\n");
        
        for(int i = 0; i < clientList.size(); i++) {
            output2.append(" " + clientList.get(i) + "\n");
        }
    }

    public void disconnect(){ 
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;
        
        System.out.println("Control: Trying to disconnect");

        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(Beers4Peers.SERVER_ADDRESS, Beers4Peers.SERVER_PORT), 1000);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
            String fromServer;
            String fromUser;

            fromUser = "disconnectSupernode";
            
            out.println(fromUser);
            
            fromServer = in.readLine();
            
            //Supernode received file
            if(fromServer != null){
                output1.append("Disconnected\n");
            }
            
            files = null;
            output2.setText(null);
            
            connected = false;
            buttonsControl();
        
            tcpListener.closeSocket();
            
        } catch (UnknownHostException ex) {
            System.err.println("Error: Client (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Failed to disconnect\n");
        } catch (IOException ex) {
            System.err.println("Error: Client (Couldn't get I/O for the connection to: " + 
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Faile to disconnect\n");
        }
    }
    
}

