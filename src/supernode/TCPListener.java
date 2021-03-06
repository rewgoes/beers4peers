package supernode;

import Interface.Beers4Peers;
import java.io.IOException;
import java.net.ServerSocket;
import static supernode.Supernode.myAddress;

/**
 *
 * @author rafael(rewgoes), matheus, andre
 *
 * Class/thread responsible for listen for connections
 */
public class TCPListener extends Thread{

    Supernode supernode;
    ServerSocket supernodeSocket;
    boolean listening;

    public TCPListener(Supernode aThis) {
        supernode = aThis;
    }

    //When client disconnect, socket is closed, stopping accepting connections
    //It makes the method run() return, what stops thread while not in use, so it need to be start again
    public void closeSocket(){
        try {
            synchronized(this){
                listening = false;
            }
            supernodeSocket.close();
        } catch (IOException ex) {
            System.err.println("Error: TCPListener (Could not close socket): " + ex.getMessage());
        }
    }

    @Override
    public void run(){
        supernodeSocket = null;

        listening = true;

        try {
            //Create a new socket at the port passed as argument
            supernodeSocket = new ServerSocket(Beers4Peers.PORT);
            System.out.println("Control: Supernode created at " + myAddress);
        } catch (IOException ex) {
            System.err.println("Error: TCPListener (Could not listen on port: " + Beers4Peers.PORT + "): " + ex.getMessage());
            return;
        }

        while(listening){
            try {
                System.out.println("Control: Listening for commands at port " + Beers4Peers.PORT);

                //Wait for connection, and when one starts, it starts a new thread
                new SupernodeTCPThread(supernodeSocket.accept(), supernode).start();
            } catch (IOException ex) {
                System.err.println("Error: TCPListener (Accept failed): " + ex.getMessage());
                return;
            }
        }

        try {
            supernodeSocket.close();
        } catch (IOException ex) {
            System.err.println("Error: TCPListener (Couldn't close socket): " + ex.getMessage());
        }
    }

}
