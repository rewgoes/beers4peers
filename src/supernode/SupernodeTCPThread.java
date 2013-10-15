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
    
    @Override
    public void run() {
        messagesIntepreter();
    }

    private void messagesIntepreter() {
        //Receive a client or supernode from server
        if (socket.getInetAddress().toString().split("/")[1].equals(Beers4Peers.SERVER_ADDRESS)){
            System.out.println("Control: New request from server");
            receiveClientFromServer();
        }
        else
            if (supernode.clientList.contains(socket.getInetAddress().toString().split("/")[1])){
                System.out.println("Control: New request from client");
                receiveClientsMessage(socket.getInetAddress().toString().split("/")[1]);
            }
            else
                if (supernode.supernodes.contains(socket.getInetAddress().toString().split("/")[1])) {
                    System.out.println("Control: New request from supernode");
                    receiveClientsMessage(socket.getInetAddress().toString().split("/")[1]);
                }
                else
                    System.out.println("Control: Unknown client");
    }

    //Receive a connection of a client from server, adding this client to its list
    private void receiveClientFromServer() {
        try {            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));
            
            inputLine = in.readLine();
            
            if(inputLine != null){
                //If it receives a supernode
                if (inputLine.equals("supernode")){
                    inputLine = in.readLine();
                    
                    if(inputLine != null){
                        supernode.supernodes.add(inputLine);
                        
                        outputLine = "OK";
                
                        out.println(outputLine);
                        
                        supernode.output1.append("Supernode " + inputLine + " added successfully\n");
                        
                        supernode.listClientsAndSupernodes();
                        
                    }
                }
                else if (inputLine.equals("supernodeDisconnect")){
                    inputLine = in.readLine();
                    
                    if(inputLine != null){
                        supernode.supernodes.remove(inputLine);
                        
                        outputLine = "OK";
                
                        out.println(outputLine);
                        
                        supernode.output1.append("Supernode " + inputLine + " removed successfully\n");
                        
                        supernode.listClientsAndSupernodes();
                        
                    }
                }
                else{
                
                    //If it receives a client
                    supernode.clientList.add(inputLine);

                    outputLine = "OK";

                    out.println(outputLine);

                    supernode.listClientsAndSupernodes();

                    supernode.output1.append("Client " + inputLine + " added to server successfully\n");
                }
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

    //Receive a client disconnection and adivises server
    //TODO: remove client's files from hashtable
    //TODO: tell other supernodes to remove files
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
                supernode.listClientsAndSupernodes();
            
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

    //Receive a file from client and add it to the hashtable
    private void receiveFile(String clitentAddress) throws IOException {
        inputLine = in.readLine();

        if(inputLine != null){
            supernode.files.put(inputLine, clitentAddress);
        }
        outputLine = "OK";

        out.println(outputLine);

        supernode.output1.append("New file from " + clitentAddress + " added: " + inputLine + "\n");
        
        //If its a file from client and not from a supernode, so spread it
        if (clitentAddress.contains(clitentAddress))
            for(int i = 0; i < supernode.supernodes.size(); i++) {
                sendFile(supernode.supernodes.get(i), inputLine);
            }
    }

    //Send new file from client to all known supernodes
    private void sendFile(String supernodeTemp, String filename) {
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;

        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(supernodeTemp, Beers4Peers.PORT), 1000);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
            String fromServer;
            String fromUser;

            fromUser = "upload\n" + filename;
            
            out.println(fromUser);
            
            System.out.println("Control: Waiting supernode confirmation " + filename);
            
            fromServer = in.readLine();
                
            
        } catch (UnknownHostException ex) {
            System.err.println("Error: Client (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Error: Client (Couldn't get I/O for the connection to: " + 
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
        }
    }
}
