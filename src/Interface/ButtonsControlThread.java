/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import node.Client;
import node.Supernode;

/**
 *
 * @author rafael
 */
public class ButtonsControlThread extends Thread{
    
    Client client;
    JButton jButton1;
    JButton jButton2;
    JButton jButton3;
    JButton jButton4;
    JButton jButton5;

    public ButtonsControlThread(Client client, Supernode supernode, JButton jButton1, JButton jButton2, JButton jButton3, JButton jButton4, JButton jButton5) throws InterruptedException{
        this.client = client;
        this.jButton1 = jButton1;
        this.jButton2 = jButton2;
        this.jButton3 = jButton3;
        this.jButton4 = jButton4;
        this.jButton5 = jButton5;
    }
    
    @Override
    public void run() {
        while(true){
            if (client != null){
                if(client.getSupernode() != null){
                    connectButtons();
                }
                else{
                    isDesconnected();
                }
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ButtonsControlThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void connectButtons() {
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
        jButton3.setEnabled(true);
        jButton4.setEnabled(true);
        jButton5.setEnabled(true);
        jButton5.setText("Disconnect");
    }
    
    public void isDesconnected() {
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
        jButton4.setEnabled(false);
        jButton5.setEnabled(true);
        jButton5.setText("Connect");
    }
    
}
