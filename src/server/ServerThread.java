/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.*;
import java.net.*;
import supernode.SupernodeList;

/**
 *
 * @author rafael
 */
public class ServerThread extends Thread {
    
    private Socket socket = null;
    private SupernodeList supernodeList;
 
    public ServerThread(Socket socket, SupernodeList supernodeList) throws IOException {
        this.socket = socket;
        this.supernodeList = supernodeList;
    }
 
    @Override
    public void run() {
 
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));

            String inputLine, outputLine;

            inputLine = in.readLine();
            
            System.out.println("Control: Message from " + socket.getInetAddress() + ":" + 
                    socket.getPort() + " content: " + inputLine);
            
            if(inputLine != null){
                if (inputLine.equals("client")){
                    //It must be synchronized as it changes the control list
                    synchronized(this){
                        outputLine = addClient();
                    }
                    
                    if (outputLine == null)
                        ;// TODO: Undo all actions taken, and maybe change addClient() function, in case of error
                    else{
                        //Send supernodes's address that is responsible for this client
                        out.println(outputLine);

                        //Wait for client confirmation
                        inputLine = in.readLine();
                        if(inputLine != null)
                            if (inputLine.equals("OK"))
                                //Success
                                System.out.println("Control: Client " + socket.getInetAddress().toString().split("/")[1] + ":" +
                                        socket.getPort() + " added to " + outputLine);
                            else{
                                System.err.println("Error: ServerThread (Could not add client)");
                                ;// TODO: Undo all axtions taken
                            }
                        else{
                            System.err.println("Error: ServerThread (Could not add client)");
                            ;// TODO: Undo all ations taken
                        }
                    }
                }
                else if (inputLine.equals("supernode")){
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
            }
            
            out.close();
            in.close();
            socket.close();

        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Problem reading or writing in socket): " + ex.getMessage());
        }
    }
    
    public String addClient(){
        return supernodeList.addClient(socket.getInetAddress(), socket.getPort());
    }
    
    public void addSupernode(){
        supernodeList.addSupernode(socket.getInetAddress(), socket.getPort());
    }

    
}
