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

    public ClientTCPThread(Socket accept, Client client){
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
                System.out.println("Control: New request from client");
                
                //TODO: implement sendFileToClient
                
                sendFileToClient();
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

                client.output1.append("Client " + inputLine + " to server successfully\n");
            }

            in.close();
            out.close();
            
        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Problem reading or writing in socket): " + ex.getMessage());
        } 
    }

    private void sendFileToClient() {
        //TODO: implement sendFileToClient
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
