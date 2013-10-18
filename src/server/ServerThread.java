package server;

import java.io.*;
import java.net.*;

/**
 *
 * @author rafael(rewgoes), matheus, andre
 *
 * Class/thread responsible for handling connections
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

            //Verify/interpret message
            if(inputLine != null){
                switch (inputLine) {
                    case "client":
                        connectClient();
                        break;
                    case "supernode":
                        connectSupernode();
                        break;
                    case "disconnectClient":
                        disconnectClient();
                        break;
                    case "disconnectSupernode":
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

    //Connect a new client to the application
    private void connectClient() throws IOException {
        //It must be synchronized as it changes the control list
        synchronized(this){
            //Call supernodeList, what controls the supernodeList and actions that need to be taken
            outputLine = supernodeList.addClient(socket.getInetAddress().toString().split("/")[1], socket.getPort());
        }

        if (outputLine == null){
            System.err.println("Error: ServerThread (Could not add client)");
            outputLine = "serverOff";
            out.println(outputLine);
        }
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
                }
            else{
                System.err.println("Error: ServerThread (Could not add client)");
            }
        }
    }

    //Connect a new supernode to the application
    private void connectSupernode() {
        String supernodes;

        //It must be synchronized as it changes the control list
        synchronized(this){
            //Call supernodeList, what controls the supernodeList and actions that need to be taken
            supernodes = supernodeList.addSupernode(socket.getInetAddress().toString().split("/")[1]);
        }

        //Send confirmation to supernode
        outputLine = supernodes;
        out.println(outputLine);

        //Success
        System.out.println("Control: Supernode " + socket.getInetAddress().toString().split("/")[1] + ":" +
                socket.getPort() + " added");
    }

    //Disconnect a client from the application
    private void disconnectClient() throws IOException {

        String client = in.readLine();
        String supernode = socket.getInetAddress().toString().split("/")[1];

        //Call supernodeList, what controls the supernodeList and actions that need to be taken
        supernodeList.removeClient(supernode, client);

        System.out.println("Control: Client " + client + " disconnected");

        //Send confirmation to supernode
        outputLine = "OK";
        out.println(outputLine);
    }

    //Disconnect a supernode from the application
    private void disconnectSupernode() throws IOException {

        String supernode = socket.getInetAddress().toString().split("/")[1];
        
        //There is only one supernode
        if(supernodeList.size() == 1){
            outputLine = "disconnectionRefused";
            out.println(outputLine);
            return;
        }

        //Call supernodeList, what controls the supernodeList and actions that need to be taken
        supernodeList.removeSupernode(supernode);

        System.out.println("Control: Supernode " + supernode + " disconnected");

        //Send confirmation to supernode
        outputLine = "OK";
        out.println(outputLine);
    }


}
