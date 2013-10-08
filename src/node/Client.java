
package node;

import java.net.InetAddress;
import javax.swing.JTextArea;

/**
 *
 * @author rafael, matheus, andre
 * 
 * Class responsible the client side of the application
 */
public final class Client {

    //Thread responsible for broadcast a message to all supernodes, and find one
    static private FindSupernodeThread findSupernode;
    
    //Supernode responsible for this client
    private InetAddress supernode;
    
    //This are the textArea in interface, use it to append messages
    private JTextArea output1;
    private JTextArea output2;
    
    //Check if client has been initialized
    private boolean initialized = false;

    //Return current client's supernode
    public InetAddress getSupernode() {
        return supernode;
    }
    
    //Client constructor
    public Client(JTextArea jTextArea1, JTextArea jTextArea2) throws InterruptedException{
        output1 = jTextArea1;
        output2 = jTextArea2;
    }
    
    //Initialize client by calling its threads
    public void init() throws InterruptedException{
        if (!initialized){
            findSupernode = new FindSupernodeThread();

            findSupernode.start();
            findSupernode.suspend();

            lookForSupernode();

            output2.setText(null);
            output2.append("Supernode:\n" + supernode);

            output1.append("Connected to: " + supernode + "\n");
        }
    }

    //When there's no connection to a supernode
    public void lookForSupernode() throws InterruptedException{
        synchronized (findSupernode){
            findSupernode.suspend = false;
            findSupernode.resume();
            findSupernode.wait();
            supernode = findSupernode.getSupernode();
            System.out.print("Control: Supernode found: " + supernode);
            findSupernode.suspend();
        }
    }
    
}
