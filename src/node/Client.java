
package node;

import Interface.Beers4Peers;
import java.io.*;
import java.net.*;
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
    
    //Client constructor
    public Client(JTextArea jTextArea1, JTextArea jTextArea2, boolean[] buttonContol) {
        this.output1 = jTextArea1;
        this.output2 = jTextArea2;
        this.buttonContol = buttonContol;
    }
    
    //Initialize client by calling its threads
    public void connect() throws IOException{
        if (!connected){
            Socket connectionSocket = new Socket();
            PrintWriter out = null;
            BufferedReader in = null;

            try {
                connectionSocket = new Socket(Beers4Peers.SERVER_ADDRESS, Beers4Peers.SERVER_PORT);
                out = new PrintWriter(connectionSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                
            } catch (UnknownHostException ex) {
                System.err.println("Control: Client (Don't know about host: " +
                        Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
                System.exit(1);
            } catch (IOException ex) {
                System.err.println("Control: Client (Couldn't get I/O for the connection to: " + 
                        Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
                System.exit(1);
            }

            String fromServer;
            String fromUser;
            
            fromUser = "client";
            
            out.println(fromUser);

            fromServer = in.readLine();
            
            if(fromServer == null)
                ;// TODO: Undo all actions taken
            else {
                supernode = fromServer;
                
                fromUser = "OK";
                
                out.println(fromUser);
                
                //Set client to connected
                connected = true;

                out.close();
                in.close();
                connectionSocket.close();

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
            buttonContol[0] = true;
            buttonContol[1] = true;
            buttonContol[2] = false;
            buttonContol[3] = false;
            buttonContol[4] = false;
        }
    }
    
}
