package server;

import Interface.Beers4Peers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import supernode.ClientList;

/**
 *
 * @author rafael(rewgoes), matheus, andre
 *
 */

/*
 * Class that holds all clients known by a supernode
 */
class SupernodeInfo{

    protected ClientList clients;
    private String supernodeAddress;

    public SupernodeInfo (String supernodeAddress){
        this.supernodeAddress = supernodeAddress;
        this.clients = new ClientList();
    }

    //Compares two supernodes address
    public boolean equals(String supernodeAddress) {
        if (this.supernodeAddress.equals(supernodeAddress)){
            return true;
        }
        return false;
    }

    //Return client qTy
    public int clientQty(){
        return clients.size();
    }

    //Add new client to this supernode
    public void addClient(String clientAddress){
        clients.add(clientAddress);
    }

    //Return the supernode with less clients
    public boolean lessBusy(SupernodeInfo supernode){
        return this.clientQty() < supernode.clientQty();
    }

    @Override
    public String toString(){
        return (this.supernodeAddress);
    }

    //Remove client from this supernode
    void removeClient(String client) {
        clients.removeClient(client);
    }
}


/*
 * Class that holds all the supernode information in server side
 * Controls the supernodeList and actions that need to be taken when a node connect/disconnect
 */
public class SupernodeList {

    private List<SupernodeInfo> supernodes;

    public SupernodeList(){
        this.supernodes = new ArrayList<SupernodeInfo>();
    }
    
    public int size(){
        return this.supernodes.size();
    }

    //Add a new supernode to the application and inform other supernodes that a new supernode was added
    public String addSupernode(String supernodeAddress){
        String supernodesTemp = new String();

        for(Iterator<SupernodeInfo> i = supernodes.iterator(); i.hasNext(); ) {
            SupernodeInfo supernode = i.next();
            supernodesTemp = supernodesTemp.concat(supernode.toString() + "-");
            informNewSupernode(supernode.toString(), supernodeAddress);
        }

        supernodes.add(0, new SupernodeInfo(supernodeAddress));

        return supernodesTemp;
    }

    //Check if the supernode already exists
    public boolean containsSupernode(String supernodeAddress, int port){
        for(Iterator<SupernodeInfo> i = supernodes.iterator(); i.hasNext(); ) {
            SupernodeInfo supernode = i.next();
            if (supernode.equals(supernodeAddress))
                return true;
        }
        return false;
    }

    //Add client to the best supernode at the moment and return supernodes's address:port
    public String addClient(String clientAddress, int clientPort){
        SupernodeInfo supernode;

        //Order supernode by number of clients (less first)
        orderSupernodes();

        //As list is ordered, will get the first supernode less busy and available
        for(Iterator<SupernodeInfo> i = supernodes.iterator(); i.hasNext(); ) {
            supernode = i.next();
            try {
                //Check if supernode is available to client
                if(supernodeAvailableToClient(supernode.toString(), clientAddress)){
                    supernode.addClient(clientAddress);
                    return supernode.toString();
                }
            } catch (IOException ex) {
                System.err.println("Error: Server: " + ex.getMessage());
            }
        }

        return null;
    }

    //Order supernode by number of clients (less first)
    public void orderSupernodes(){

        SupernodeInfo supernodeTemp;

        for(int i = 0; i < supernodes.size() - 1; i++) {
            if (supernodes.get(i+1).lessBusy(supernodes.get(i))){
                supernodeTemp = supernodes.get(i);
                supernodes.set(i, supernodes.get(i+1));
                supernodes.set(i+1, supernodeTemp);
            }
        }
    }

    //Advises / checks if supernode can handle a new client (It connects to a supernode to ask)
    private boolean supernodeAvailableToClient(String supernode, String client) throws IOException {
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;

        try {
            //Try to connect with a 1,5 seconds timeout
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(supernode, Beers4Peers.PORT), 1500);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        } catch (UnknownHostException ex) {
            System.err.println("Error: Server (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
            return false;
        } catch (IOException ex) {
            System.err.println("Error: Server (Couldn't get I/O for the connection to: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
            return false;
        }

        String fromServer;
        String fromSupernode;

        fromServer = client;

        out.println(fromServer);

        fromSupernode = in.readLine();

        if(fromSupernode == null){
            System.err.println("Error: Server (Some undefined reason)");
        }
        else {
            out.close();
            in.close();
            connectionSocket.close();

            System.out.println("Control: Client " +  client + " connected to supernode " + supernode);

            return true;
        }

        out.close();
        in.close();
        connectionSocket.close();

        return false;
    }

    //Remove client from the list of clients of one supernode
    void removeClient(String supernode, String client) {

        int i;

        for(i = 0; i < supernodes.size(); i++) {
            if (supernodes.get(i).equals(supernode)){
                break;
            }
        }

        supernodes.get(i).removeClient(client);
    }

    //Remove a supernode from the list and tells to its clients that it disconnected
    void removeSupernode(String sSupernode) throws IOException{
        int i;

        for(i = 0; i < supernodes.size(); i++) {
            if (supernodes.get(i).equals(sSupernode)){
                break;
            }
        }

        SupernodeInfo supernode = supernodes.get(i);
        List<String> clients = new ArrayList<String>();

        int size = supernode.clients.size();

        int count;

        //Save list of clients from this supernode in a new list
        for(count = 0; count < size; count++) {
            clients.add(supernode.clients.get(count));
        }

        supernodes.remove(i);

        //Tell clients to reconnect to a new supernode
        for(count = 0; count < size; count++) {
            forceClientReconnect(clients.get(count));
        }

        //Tell supernodes that a supernode disconnected
        for (count = 0; count < supernodes.size(); count++) {
            disconnectSupernodeFrom(supernodes.get(count).toString(), sSupernode);
        }
    }

    //Tell clients to reconnect to a new supernode (Connects to a client)
    void forceClientReconnect(String client) throws IOException{
        System.out.println("Control: Sending reconnect to client " +  client);

        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;

        try {
            //Try to connect with a 1,5 seconds timeout
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(client, Beers4Peers.PORT), 1500);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        } catch (UnknownHostException ex) {
            System.err.println("Error: Server (Don't know about host: " +
                    client + "): " + ex.getMessage());
            return;
        } catch (IOException ex) {
            System.err.println("Error: Server (Couldn't get I/O for the connection to: " +
                    client + "): " + ex.getMessage());
            return;
        }

        String fromServer;

        fromServer = "reconnect";

        out.println(fromServer);

        out.close();
        in.close();
        connectionSocket.close();

    }

    //Inform other supernodes that a new supernode was added
    private void informNewSupernode(String supernodeAddress, String supernodeAdded) {
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;

        try {
            //Try to connect with a 1,5 seconds timeout
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(supernodeAddress, Beers4Peers.PORT), 1500);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            String fromServer;
            String fromSupernode;

            fromServer = "supernode\n" + supernodeAdded;

            out.println(fromServer);

            fromSupernode = in.readLine();

            if(fromSupernode == null){
                System.err.println("Error: Server (Some undefined reason)");
            }

            out.close();
            in.close();
            connectionSocket.close();

        } catch (UnknownHostException ex) {
            System.err.println("Error: Server (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Error: Server (Couldn't get I/O for the connection to: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
        }
    }

    //Tell supernodes that a supernode disconnected (Connects to a supernode)
    private void disconnectSupernodeFrom(String supernodeAddress, String supernodeDisconnected) {
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;

        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(supernodeAddress, Beers4Peers.PORT), 1500);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            String fromServer;
            String fromSupernode;

            fromServer = "supernodeDisconnect\n" + supernodeDisconnected;

            out.println(fromServer);

            fromSupernode = in.readLine();

            if(fromSupernode == null){
                System.err.println("Error: Server (Some undefined reason)");
            }
            else {
                out.close();
                in.close();
                connectionSocket.close();
            }

        } catch (UnknownHostException ex) {
            System.err.println("Error: Server (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Error: Server (Couldn't get I/O for the connection to: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());
        }
    }

}