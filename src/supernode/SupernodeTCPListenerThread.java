/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supernode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author rafael
 */
public class SupernodeTCPListenerThread extends Thread{
    
    private Socket socket;
    private Supernode supernode;

    SupernodeTCPListenerThread(Socket accept, Supernode aThis) {
        this.socket = socket;
        this.supernode = aThis;
    }
    
    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));

            String inputLine, outputLine;
            
            //TODO: SupernodeTCPListenerThread: Answer server, accepting client
            
        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Problem reading or writing in socket): " + ex.getMessage());
        }
        
    
    }
}
