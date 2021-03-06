package supernode;

import Interface.Beers4Peers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTextArea;

/**
 *
 * @author rafael(rewgoes), matheus, andre
 *
 * Class responsible for initialize the supernode side of the application
 */
public class Supernode {

    //TextArea in interface, use it to append messages
    protected JTextArea output1;
    protected JTextArea output2;

    //Check if client is connected
    protected boolean connected = false;

    //Control interface's button
    protected boolean[] buttonContol;

    //Check if supernode has been initialized
    protected boolean initialized = false;

    protected static String myAddress;

    //TODO: Supernode: Create a list of clients
    protected ClientList clientList;

    //Hashtable of files
    protected Map<String, String> files;
     
    //Thread responsible to listen for connections
    private TCPListener tcpListener;

    //A list of all the supernodes in the application
    protected List<String> supernodes;

    //Supernode constructor, start as many objects as possible
    public Supernode(JTextArea jTextArea1, JTextArea jTextArea2, boolean[] buttonContol) {
        this.output1 = jTextArea1;
        this.output2 = jTextArea2;
        this.buttonContol = buttonContol;
        this.clientList = new ClientList();
        this.files = new HashMap<String, String>();
        this.supernodes = new ArrayList<String>();

    }

    //Initialize supernode by calling its threads
    public boolean connect() throws IOException {
        if (!connected){
            if(myAddress == null) this.getAddress();

            Socket connectionSocket;
            PrintWriter out;
            BufferedReader in;

            try {
                connectionSocket = new Socket();
                connectionSocket.connect(new InetSocketAddress(Beers4Peers.SERVER_ADDRESS, Beers4Peers.SERVER_PORT), 1000);
                out = new PrintWriter(connectionSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            } catch (UnknownHostException ex) {
                System.err.println("Error: Supernode (Don't know about host: " +
                        Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

                output1.append("Failed to connect: Server is offline\n");

                return false;
            } catch (IOException ex) {
                System.err.println("Error: Supernode (Couldn't get I/O for the connection to: " +
                        Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

                output1.append("Failed to connect: Server is offline\n");

                return false;
            }

            String fromServer;
            String fromUser;

            fromUser = "supernode";

            out.println(fromUser);

            fromServer = in.readLine();

            if(fromServer == null){
                System.err.println("Error: Supernode (Some undefined reason)");

                output1.append("Failed to connect: Server is offline\n");

                return false;
            }
            else {

                String[] supernodesTemp = fromServer.split("-", 0);

                for (int i = 0; i < supernodesTemp.length; i++){
                    if(supernodesTemp[i].length() > 5)
                        supernodes.add(supernodesTemp[i]);
                }

                connected = true;
                this.buttonsControl();

                out.close();
                in.close();
                connectionSocket.close();

                listClientsAndSupernodes();

                output1.append("Connected to server successfully\n");

                tcpListener = new TCPListener(this);
                tcpListener.start();
            }
        }
        
        return true;
    }

    //Control interface's buttons
    protected void buttonsControl(){
        if(connected){
            buttonContol[0] = false;
            buttonContol[1] = false;
            buttonContol[2] = false;
            buttonContol[3] = false;
            buttonContol[4] = true;
        } else {
            buttonContol[0] = false;
            buttonContol[1] = false;
            buttonContol[2] = false;
            buttonContol[3] = false;
            buttonContol[4] = true;
        }
    }

    //This method only gets the supernodes local address
    protected static void getAddress(){
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback())
                    continue;    // Don't want to broadcast to the loopback interface
                // If not loopback, get its broadcast address
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                        continue;
                    else{
                        myAddress = interfaceAddress.getAddress().getHostAddress();
                    }
                }
            }
            if (myAddress == null){
                System.err.println("Error: Server (There's no connection)");
            }
        } catch (Exception ex) {
            System.err.println("Error: Server (Could not retrieve address): " + ex.getMessage());
        }
    }

    //List all clients on the interface
    protected void listClientsAndSupernodes() {
        output2.setText("Clients:\n");

        for(int i = 0; i < clientList.size(); i++) {
            output2.append("-" + clientList.get(i) + "\n");
        }

        output2.append("Supernodes:\n");

        for(int i = 0; i < supernodes.size(); i++) {
            output2.append("-" + supernodes.get(i) + "\n");
        }
    }

    //Disconnect from the application, setting all object to null or new
    public boolean disconnect(){
        Socket connectionSocket;
        PrintWriter out;
        BufferedReader in;

        System.out.println("Control: Trying to disconnect");

        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(Beers4Peers.SERVER_ADDRESS, Beers4Peers.SERVER_PORT), 1000);
            out = new PrintWriter(connectionSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            String fromServer;
            String fromUser;

            fromUser = "disconnectSupernode";

            //TODO: send all file name to the supernode so it can remove from its list

            out.println(fromUser);

            fromServer = in.readLine();

            //Supernode received file
            if(fromServer != null){
                if (fromServer.equals("OK"))
                    output1.append("Disconnected\n");
                else // (fromServer.equals("disconnectionRefused"))
                {
                    System.out.println("Control: Could not disconnect, this is the last supernode");
                    output1.append("Could not disconnect\n");
                    
                    return false;
                }
            } else {
                return false;
            }
            
            files = new HashMap<String, String>();
            output2.setText(null);
            clientList = new ClientList();
            supernodes = new ArrayList<String>();

            connected = false;
            buttonsControl();

            tcpListener.closeSocket();
            

        } catch (UnknownHostException ex) {
            System.err.println("Error: Client (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Failed to disconnect\n");
        } catch (IOException ex) {
            System.err.println("Error: Client (Couldn't get I/O for the connection to: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            output1.append("Failed to connect: Faile to disconnect\n");
        }
        
        return true;
    }

}

