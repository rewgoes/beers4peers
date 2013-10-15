/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Interface.Beers4Peers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author rafael
 */
class ClientTCPThread extends Thread{
    
    private Socket socket;
    private Client client;
    private PrintWriter out;
    private BufferedReader in;
    private String inputLine, outputLine;

    public ClientTCPThread(Socket socket, Client client){
        this.client = client;
        this.socket = socket;
    }
    
    @Override
    public void run() {
        messagesIntepreter();
    }

    private void messagesIntepreter() {
        try {            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));
            
            //TODO: SupernodeTCPListenerThread: Answer server, accepting client
            inputLine = in.readLine();
            
            if(inputLine != null){
                switch (inputLine) {
                    case "reconnect":
                        client.forceReconnect();
                        client.output1.append("Client " + inputLine + " reconnected to server successfully\n");
                        break;
                    case "download":
                        String clientToSend = in.readLine();
                        if(clientToSend != null){
                            String file = in.readLine();

                            if(file != null){

                                if (client.files.contains(file)){
                                    out.println("OK");

                                    sendFileTo(clientToSend, file);

                                    client.output1.append("File " + file + 
                                            " sent to " + clientToSend + "\n");
                                }
                            }
                        }
                        break;
                }
                
            }

            in.close();
            out.close();
            
        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Problem reading or writing in socket): " + ex.getMessage());
        } 
    }

    private void sendFileTo(String clientToSend, String file) {
        
        //send file to client interested on it
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
