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
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JTextArea;

/**
 *
 * @author rafael
 */
public class Supernode {
    
    private JTextArea output1;
    private JTextArea output2;
    
    //Check if client is connected
    private boolean connected = false;
    
    //Control interface's button
    private boolean[] buttonContol;
    
    //Check if supernode has been initialized
    private boolean initialized = false;
    

    public Supernode(JTextArea jTextArea1, JTextArea jTextArea2, boolean[] buttonContol) {
        this.output1 = jTextArea1;
        this.output2 = jTextArea2;
        this.buttonContol = buttonContol;
    }
    
    
    //Initialize client by calling its threads
    public void connect() throws IOException {
        if (!connected){
            Socket connectionSocket = new Socket();
            PrintWriter out = null;
            BufferedReader in = null;

            try {
                connectionSocket = new Socket(Beers4Peers.SERVER_ADDRESS, Beers4Peers.SERVER_PORT);
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

                out.close();
                in.close();
                connectionSocket.close();

                output2.setText(null);
                output2.append("Clients:\n");

                output1.append("Connected to successfully\n");
            }
            
        }
        
        this.buttonsControl();
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

