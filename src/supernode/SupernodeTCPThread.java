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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 *
 * @author rafael
 */
public class SupernodeTCPThread extends Thread{
    
    private Socket socket;
    private Supernode supernode;
    private PrintWriter out;
    private BufferedReader in;
    private String inputLine, outputLine;

    SupernodeTCPThread(Socket socket, Supernode aThis) {
        this.socket = socket;
        this.supernode = aThis;
    }
    
    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Could not close socket): " + ex.getMessage());
        }  
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
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));
            
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

    private void receiveClientsMessage(String clitentAddress) {
        try {            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));
            
            //TODO: SupernodeTCPListenerThread: Answer server, accepting client
            inputLine = in.readLine();
            
            if(inputLine != null){
                if (inputLine.equals("upload")){
                        receiveFile(clitentAddress);
                }
                else {
                    if (inputLine.equals("disconnectClient")){             
                        disconnectClient();
                    }
                }
            }
            
            in.close();
            out.close();
            
        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Problem reading or writing in socket): " + ex.getMessage());
        }
    }

    private void disconnectClient() {
        
        String client = socket.getInetAddress().toString().split("/")[1];
                
        Socket connectionSocket;
        PrintWriter outTemp;
        BufferedReader inTemp;
        
        System.out.println("Control: Trying to disconnect");

        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(Beers4Peers.SERVER_ADDRESS, Beers4Peers.SERVER_PORT), 1000);
            outTemp = new PrintWriter(connectionSocket.getOutputStream(), true);
            inTemp = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
            String fromServer;
            String fromUser;

            fromUser = "disconnectClient\n"
                    + client;
            
            outTemp.println(fromUser);
            
            fromServer = inTemp.readLine();
            
            if (fromServer != null){
                synchronized(this){
                    supernode.clientList.removeClient(client);
                    supernode.output1.append("Client " + client + " disconnected\n");
                }

                outTemp.println(outputLine);
                supernode.listClients();
            
                System.out.println("Control: Client " + client +
                    " disconnected");
                
                this.out.print("OK");
            }
            
        } catch (UnknownHostException ex) {
            System.err.println("Error: Client (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Error: Client (Couldn't get I/O for the connection to: " + 
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
        }
    }

    private void receiveFile(String clitentAddress) throws IOException {
        inputLine = in.readLine();

        if(inputLine != null){
            supernode.files.put(inputLine, clitentAddress);
        }
        outputLine = "OK";

        out.println(outputLine);

        supernode.output1.append("New file from " + clitentAddress + " added: " + inputLine + "\n");
    }
}
