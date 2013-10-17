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
 * @author rafael(rewgoes), matheus, andre
 * 
 * Class/thread responsible for handling connections
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
    
    //Receive a message and interpret it
    @Override
    public void run() {
        try {
            String clitentAddress = socket.getInetAddress().toString().split("/")[1];
            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));
            
            inputLine = in.readLine();
            
            if(inputLine != null){
                switch (inputLine) {
                    //If it receives a supernode to connect
                    case "supernode":
                        inputLine = in.readLine();
                        if(inputLine != null){
                            supernode.supernodes.add(inputLine);
                            
                            outputLine = "OK";
                    
                            out.println(outputLine);
                            
                            supernode.output1.append("Supernode " + inputLine + " added successfully\n");
                            
                            supernode.listClientsAndSupernodes();
                            
                        }
                        break;
                    //If a supernode is disconnected
                    case "supernodeDisconnect":
                        inputLine = in.readLine();
                        if(inputLine != null){
                            supernode.supernodes.remove(inputLine);
                            
                            outputLine = "OK";
                    
                            out.println(outputLine);
                            
                            supernode.output1.append("Supernode " + inputLine + " removed successfully\n");
                        
                            
                            supernode.listClientsAndSupernodes();
                            
                        }
                        break;
                    //If a client uploaded a file
                    case "upload":
                        receiveFile(clitentAddress);
                        break;
                    //If a client is disconnected
                    case "disconnectClient":             
                        disconnectClient();
                        outputLine = "OK";
                        out.println(outputLine);
                        break;
                    //If a client/supernode is requesting to download something
                    case "download":
                        findFileOwner();
                        outputLine = "OK";
                        out.println(outputLine);
                        break;
                    default:
                        //If it receives a client's IP from server to connect
                        supernode.clientList.add(inputLine);
                        outputLine = "OK";
                        out.println(outputLine);
                        supernode.listClientsAndSupernodes();
                        supernode.output1.append("Client " + inputLine + " added to server successfully\n");
                        break;
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
    private void receiveFile(String clientAddress) throws IOException {
        inputLine = in.readLine();

        if(inputLine != null){
            //Add file to the hashtable
            supernode.files.put(inputLine, clientAddress);
        }
        outputLine = "OK";

        out.println(outputLine);

        supernode.output1.append("New file from " + clientAddress + " added: " + inputLine + "\n");
        
        //If its a file from client and not from a supernode, so spread it
        if (supernode.clientList.contains(clientAddress))
            for(int i = 0; i < supernode.supernodes.size(); i++) {
                //Send this file to other supernodes, but now using supernode's address as argument
                sendFile(supernode.supernodes.get(i), inputLine);
            }
    }

    //Send new file from client to all known supernodes, but now using supernode's address as argument
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

    //Look for file owner but looking at the hashtable and redirecting message to other supernodes
    private void findFileOwner() throws IOException {
        String client = in.readLine();
        
        if (client != null){
            
            String filename = in.readLine();
            
            if (filename != null) {
                
                String fileOwner = supernode.files.get(filename);
        
                if (fileOwner != null){
                    Socket connectionSocket;
                    PrintWriter outTemp;
                    BufferedReader inTemp;

                    try {
                        connectionSocket = new Socket();
                        connectionSocket.connect(new InetSocketAddress(fileOwner, Beers4Peers.PORT), 1000);
                        outTemp = new PrintWriter(connectionSocket.getOutputStream(), true);
                        inTemp = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                        String fromServer;
                        String fromUser;

                        fromUser = "download\n" + client + "\n" + filename;

                        outTemp.println(fromUser);

                        System.out.println("Control: Waiting supernode confirmation " + filename);

                        fromServer = inTemp.readLine();

                        System.out.println("Control: Another node found file " + filename);

                    } catch (UnknownHostException ex) {
                        System.err.println("Error: Client (Don't know about host: " +
                                Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
                    } catch (IOException ex) {
                        System.err.println("Error: Client (Couldn't get I/O for the connection to: " + 
                                Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
                    }
                }
            }
        }
    }
}
