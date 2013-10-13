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

/**
 *
 * @author rafael
 */
public class SupernodeTCPListenerThread extends Thread{
    
    private Socket socket;
    private Supernode supernode;

    SupernodeTCPListenerThread(Socket socket, Supernode aThis) {
        this.socket = socket;
        this.supernode = aThis;
    }
    
    @Override
    public void run() {
        messagesIntepreter();
    }

    private void messagesIntepreter() {
        if (socket.getInetAddress().toString().split("/")[1].equals(Beers4Peers.SERVER_ADDRESS)){
            System.out.println("Control: New request from server");
            receiveClientFromServer();
        }
        else
            if (supernode.clientList.contains(socket.getInetAddress().toString().split("/")[1])){
                System.out.println("Control: New request from client");
                receiveClientsMessage(socket.getInetAddress().toString().split("/")[1]);
            }
            else{
                System.out.println("Control: Unknown client");
            }
    }

    //Receive a connection of a client from server, adding this client to its list
    private void receiveClientFromServer() {
        try {            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));
            
            String inputLine, outputLine;
            
            //TODO: SupernodeTCPListenerThread: Answer server, accepting client
            inputLine = in.readLine();
            
            if(inputLine != null){
                supernode.clientList.add(inputLine);
                
                outputLine = "OK";
                
                out.println(outputLine);
                
                supernode.listClients();

                supernode.output1.append("Client " + inputLine + " to server successfully\n");
            }

            in.close();
            out.close();
            
        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Problem reading or writing in socket): " + ex.getMessage());
        }  
    }

    private void receiveClientsMessage(String clitenAddress) {
        try {            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));
            
            String inputLine, outputLine;
            
            //TODO: SupernodeTCPListenerThread: Answer server, accepting client
            inputLine = in.readLine();
            
            if(inputLine != null){
                if (inputLine.equals("upload")){
                    inputLine = in.readLine();
                
                    if(inputLine != null){
                        supernode.files.put(inputLine, clitenAddress);
                    }
                }
                
                outputLine = "OK";
                
                out.println(outputLine);
                
                supernode.output1.append("New file from " + clitenAddress + " added: " + inputLine + "\n");
            }
            
            in.close();
            out.close();
            
        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Problem reading or writing in socket): " + ex.getMessage());
        }
    }
}
