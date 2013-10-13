
package client;

import Interface.Beers4Peers;
import java.io.*;
import java.net.*;
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
    private String supernode;
    
    //This are the textArea in interface, use it to append messages
    private JTextArea output1;
    private JTextArea output2;
    
    //Check if client is connected
    private boolean connected = false;
    
    //Control interface's button
    private boolean[] buttonContol;

    //Return current client's supernode
    public String getSupernode() {
        return supernode;
    }
    
    //Hashtable of files
    private Hashtable<String, String> files;
    
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

                return;
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
            buttonContol[4] = false;
        }
    }

    //Adds a new file to its hashtable
    public void newFile(String sPath) {
        String path = sPath;
        String filename = sPath.split("/")[sPath.split("/").length - 1];
        
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;
        
        System.out.println("Control: Trying to add file " + filename);

        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(this.supernode, Beers4Peers.SERVER_PORT), 1000);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
            String fromServer;
            String fromUser;

            fromUser = "upload";
            
            out.println(fromUser);
            out.println(filename);
            
            System.out.println("Control: Waiting supernode confirmation " + filename);
            
            fromServer = in.readLine();
            
            //Supernode received file
            if(fromServer != null){
                output1.append("New file available: " + filename + "\n");
            }
                
            
        } catch (UnknownHostException ex) {
            System.err.println("Error: Client (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Failed to send file\n");

            return;
        } catch (IOException ex) {
            System.err.println("Error: Client (Couldn't get I/O for the connection to: " + 
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Faile to send file\n");

            return;
        }
    }
    
}
