/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.*;
import java.net.*;

/**
 *
 * @author rafael
 */
public class ServerThread extends Thread {
    
    private Socket socket;
    private SupernodeList supernodeList;
    private PrintWriter out;
    private BufferedReader in;
    private String inputLine, outputLine;
 
    public ServerThread(Socket socket, SupernodeList supernodeList) throws IOException {
        this.socket = socket;
        this.supernodeList = supernodeList;
    }
 
    @Override
    public void run() {
 
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));

            String inputLine, outputLine;

            inputLine = in.readLine();
            
            System.out.println("Control: Message from " + socket.getInetAddress() + ":" + 
                    socket.getPort() + " content: " + inputLine);
            
            //Verify message
            if(inputLine != null){
                switch (inputLine) {
                    case "client":
                        connectClient();
                        break;
                    case "supernode":
                        connectSupernode();
                        break;
                    case "disconectClient":
                        disconnectClient();
                        break;
                    case "disconectSupernode":
                        disconnectSupernode();
                        break;
                }
            }
            else {
                System.err.println("Error: ServerThread (Could not add client)");
            }
            
            out.close();
            in.close();
            socket.close();

        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Problem reading or writing in socket): " + ex.getMessage());
        }
    }
    
    public String addClient(){
        return supernodeList.addClient(socket.getInetAddress().toString().split("/")[1], socket.getPort());
    }
    
    public void addSupernode(){
        supernodeList.addSupernode(socket.getInetAddress().toString().split("/")[1], socket.getPort());
    }

    private void connectClient() throws IOException {
        //It must be synchronized as it changes the control list
        synchronized(this){
            outputLine = addClient();
        }

        if (outputLine == null){
            System.err.println("Error: ServerThread (Could not add client)");// TODO: Undo all actions taken, and maybe change addClient() function, in case of error
            outputLine = "serverOff";
            out.println(outputLine);
        }
        else{
            //Send supernodes's address that is responsible for this client
            out.println(outputLine);

            // TODO: advise supernode that it got a new client

            //Wait for client confirmation
            inputLine = in.readLine();
            if(inputLine != null)
                if (inputLine.equals("OK"))
                    //Success
                    System.out.println("Control: Client " + socket.getInetAddress().toString().split("/")[1] + ":" +
                            socket.getPort() + " added to " + outputLine);
                else{
                    System.err.println("Error: ServerThread (Could not add client)");
                    ;// TODO: Undo all actions taken
                }
            else{
                System.err.println("Error: ServerThread (Could not add client)");
                ;// TODO: Undo all actions taken
            }
        }
    }

    private void connectSupernode() {
        //It must be synchronized as it changes the control list
        synchronized(this){
            addSupernode();
            ;// TODO: Undo all actions taken, and maybe change addSupernode() function, in case of error
        }

        //Send confirmation to supernode
        outputLine = "OK";
        out.println(outputLine);

        //Success
        System.out.println("Control: Supernode " + socket.getInetAddress().toString().split("/")[1] + ":" +
                socket.getPort() + " added");
    }

    private void disconnectClient() {
    }

    private void disconnectSupernode() {
    }

    
}
