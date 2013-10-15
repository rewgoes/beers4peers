
package client;

import Interface.Beers4Peers;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JTextArea;

/**
 *
 * @author rafael, matheus, andre
 * 
 * Class responsible the client side of the application
 */
public class Client {
    
    //Supernode responsible for this client
    protected String supernode;
    
    //This are the textArea in interface, use it to append messages
    protected JTextArea output1;
    protected JTextArea output2;
    
    //Check if client is connected
    private boolean connected = false;
    
    protected static String myAddress;
    
    //Control interface's button
    private boolean[] buttonContol;
    
    private TCPListener tcpListener;

    //Return current client's supernode
    public String getSupernode() {
        return supernode;
    }
    
    //Hashtable of files
    protected Hashtable<String, String> files;
    
    //Client constructor
    public Client(JTextArea jTextArea1, JTextArea jTextArea2, boolean[] buttonContol) {
        this.output1 = jTextArea1;
        this.output2 = jTextArea2;
        this.buttonContol = buttonContol;
        this.files = new Hashtable<String, String>();
    }
    
    //Initialize client by calling its threads and connect to server, finding a supernode
    public void connect() throws IOException{
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
                System.err.println("Error: Client (Don't know about host: " +
                        Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
                
                output1.append("Failed to connect: Server is offline\n");
                
                return;
            } catch (IOException ex) {
                System.err.println("Error: Client (Couldn't get I/O for the connection to: " + 
                        Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
                
                output1.append("Failed to connect: Server is offline\n");
                
                return;
            }

            String fromServer;
            String fromUser;
            
            fromUser = "client";
            
            out.println(fromUser);

            fromServer = in.readLine();
            
            if(fromServer == null){
                System.err.println("Error: Client (Some undefined reason)");
                    
                output1.append("Failed to connect: Server is offline\n");
            }
            else {
                if (fromServer.equals("serverOff")){
                    System.err.println("Error: Client (Supernode not found)");
                    
                    output1.append("Failed to connect: Server is offline\n");
                    
                    return;
                }
                    
                supernode = fromServer;
                
                fromUser = "OK";
                
                out.println(fromUser);
                
                //Set client to connected
                connected = true;
                this.buttonsControl();

                out.close();
                in.close();
                connectionSocket.close();

                System.out.println("Control: Client connected to " + fromServer);
                
                output2.setText(null);
                output2.append("Supernode:\n" + supernode + "\n");

                output1.append("Connected to: " + supernode + "\n");
            }
        }
        
        this.getAddress();
        
        tcpListener = new client.TCPListener(this);
        tcpListener.start();
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
    
    
    //Control interface's buttons
    private void buttonsControl(){
        if(connected){
            buttonContol[0] = false;
            buttonContol[1] = false;
            buttonContol[2] = true;
            buttonContol[3] = true;
            buttonContol[4] = true;
        } else {
            buttonContol[0] = false;
            buttonContol[1] = false;
            buttonContol[2] = false;
            buttonContol[3] = false;
            buttonContol[4] = true;
        }
    }

    //Adds a new file to its hashtable
    public void newFile(String sPath) {
        String path = sPath;
        String filename = sPath.split("/")[sPath.split("/").length - 1];
        filename = filename.split("\\\\")[filename.split("\\\\").length - 1];
        
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;
        
        System.out.println("Control: Trying to add file " + filename + " to " + this.supernode);

        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(this.supernode, Beers4Peers.PORT), 1000);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
            String fromServer;
            String fromUser;

            fromUser = "upload\n" + filename;
            
            out.println(fromUser);
            
            System.out.println("Control: Waiting supernode confirmation " + filename);
            
            fromServer = in.readLine();
            
            //Supernode received file
            if(fromServer != null){
                output1.append("New file available: " + filename + "\n");
            }
            
            files.put(filename, path);
                
            
        } catch (UnknownHostException ex) {
            System.err.println("Error: Client (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Failed to send file\n");
        } catch (IOException ex) {
            System.err.println("Error: Client (Couldn't get I/O for the connection to: " + 
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Faile to send file\n");
        }
    }
    
    public void downloadFile(String filename) {        
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;
        
        System.out.println("Control: Asking for " + filename + " to " + this.supernode);

        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(this.supernode, Beers4Peers.PORT), 1000);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
            String fromServer;
            String fromUser;

            fromUser = "download\n" + myAddress + "\n" + filename;
            
            out.println(fromUser);
            
            System.out.println("Control: Waiting supernode confirmation " + filename);
            
            fromServer = in.readLine();
            
            //Supernode received file
            if(fromServer != null){
                System.out.println("Control: File " + filename + " found in the network");
            }
                
            
        } catch (UnknownHostException ex) {
            System.err.println("Error: Client (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Failed to send file\n");
        } catch (IOException ex) {
            System.err.println("Error: Client (Couldn't get I/O for the connection to: " + 
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Faile to send file\n");
        }
    }

    public void disconnect() {
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;
        
        System.out.println("Control: Trying to disconnect");

        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(this.supernode, Beers4Peers.PORT), 1000);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
            String fromServer;
            String fromUser;

            fromUser = "disconnectClient";
            
            out.println(fromUser);
            
            fromServer = in.readLine();
            
            //Supernode received file
            if(fromServer != null){
                output1.append("Disconnected\n");
            }
            
            supernode = null;
            files = null;
            output2.setText(null);
            connected = false;
            
            this.buttonsControl();
            
            tcpListener.closeSocket();
            
        } catch (UnknownHostException ex) {
            System.err.println("Error: Client (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Failed to disconnect\n");
        } catch (IOException ex) {
            System.err.println("Error: Client (Couldn't get I/O for the connection to: " + 
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Failed to disconnect\n");
        }
        
    }
    
    public void forceReconnect() throws IOException{
        output1.append("Supernode " + supernode + " disconnected" + "\n"
                + "Trying to reconnect...\n");
        supernode = null;
        files = null; //TODO: change it to preserve files previous uploaded and send again to the new supernode
        output2.setText(null);
        connected = false;
        tcpListener.closeSocket();
        this.connect();
    }
    
    
}
