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
        if (socket.getInetAddress().toString().split("/")[1].equals(Beers4Peers.SERVER_ADDRESS)){
            System.out.println("Control: New request from server");
            forceReconnect();
        }
        else
            if (client.supernode.equals(socket.getInetAddress().toString().split("/")[1])){
                System.out.println("Control: New request from supernode");
                
                //TODO: implement sendFileToClient
                waitForDownload();
            }
            else{
                System.out.println("Control: Unknown supernode");
            }
    }

    private void forceReconnect() {
        try {            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));
            
            //TODO: SupernodeTCPListenerThread: Answer server, accepting client
            inputLine = in.readLine();
            
            if(inputLine != null){
                if (inputLine.equals("reconnect")){
                    client.forceReconnect();
                }

                client.output1.append("Client " + inputLine + " reconnected to server successfully\n");
            }

            in.close();
            out.close();
            
        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Problem reading or writing in socket): " + ex.getMessage());
        } 
    }

    private void waitForDownload() {
        try {            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));
            
            inputLine = in.readLine();
            
            if(inputLine != null){
                if (inputLine.equals("download")){
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
