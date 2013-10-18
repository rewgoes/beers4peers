package Interface;

import java.io.IOException;
import client.Client;
import java.io.File;
import supernode.Supernode;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author rafael(rewgoes), matheus, andre
 *
 * Class/thread responsible for handling connections
 */
public class Beers4Peers extends javax.swing.JFrame {

    // basic config
    public static final int PORT = 50001;
    public static int SERVER_PORT;
    public static String SERVER_ADDRESS;

    // the other nodes
    private Client client;
    private Supernode supernode;

    // the ButtonsControlThread will manage which buttons are enabled and which
    // are disabled, using the buttonControl array
    private ButtonsControlThread control;
    private boolean[] buttonControl;

    // whether we are connected to client or supernode
    private String type;


    public Beers4Peers(){
        initComponents();

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            // When closing the window, also disconnect from client or server
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (!connectDisconnect.getText().equals("Connect") && !clientConnect.isEnabled()){
                    if (type.equals("client")){
                        if(client.disconnect())
                            System.exit(0);
                    }
                    else {
                        if (supernode.disconnect())
                            System.exit(0);
                    }
                }
            }
        });

        // Each button n is related to the position n-1 in buttonControl
        // If more buttons are added, the array size must be increased
        buttonControl = new boolean[5];

        buttonControl[0] = true;
        buttonControl[1] = true;
        buttonControl[2] = false;
        buttonControl[3] = false;
        buttonControl[4] = false;


        // instantiate client and supernode
        client = new Client(jTextArea1, jTextArea2, buttonControl);
        supernode = new Supernode(jTextArea1, jTextArea2, buttonControl);

        // startup the thread to control the buttons
        control = new ButtonsControlThread(buttonControl, clientConnect, supernodeConnect, upload, download, connectDisconnect);

        control.start();
    }

    // connect to client
    public void initClient() throws InterruptedException, SocketException{

        try {
            client.connect();
            if (!buttonControl[0])
                jLabel1.setText("Messages [Client]");
            type = "client";
        } catch (IOException ex) {
            Logger.getLogger(Beers4Peers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // connect to supernode
    public void initSupernode() throws InterruptedException, SocketException{

        try {
            supernode.connect();
            if (!buttonControl[0])
                jLabel1.setText("Messages [Supernode]");
            type = "supernode";
        } catch (IOException ex) {
            Logger.getLogger(Beers4Peers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jFileChooser2 = new javax.swing.JFileChooser();
        jFileChooser3 = new javax.swing.JFileChooser();
        jFileChooser4 = new javax.swing.JFileChooser();
        clientConnect = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        supernodeConnect = new javax.swing.JButton();
        upload = new javax.swing.JButton();
        download = new javax.swing.JButton();
        connectDisconnect = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        clientConnect.setText("Client");
        clientConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientConnectActionPerformed(evt);
            }
        });

        jLabel1.setText("Messages");

        jLabel2.setText("Network");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setFocusable(false);
        jScrollPane2.setViewportView(jTextArea1);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(10);
        jTextArea2.setRows(5);
        jTextArea2.setFocusable(false);
        jScrollPane4.setViewportView(jTextArea2);

        supernodeConnect.setText("Supernode");
        supernodeConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supernodeConnectActionPerformed(evt);
            }
        });

        upload.setText("Upload");
        upload.setEnabled(false);
        upload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadActionPerformed(evt);
            }
        });

        download.setText("Download");
        download.setEnabled(false);
        download.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadActionPerformed(evt);
            }
        });

        connectDisconnect.setText("Disconnect");
        connectDisconnect.setEnabled(false);
        connectDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectDisconnectActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(clientConnect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(supernodeConnect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(upload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(connectDisconnect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(download)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(upload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(connectDisconnect)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(clientConnect)
                        .addComponent(supernodeConnect)
                        .addComponent(download)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clientConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientConnectActionPerformed
        try {
            initClient();
        } catch (InterruptedException ex) {
            Logger.getLogger(Beers4Peers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Beers4Peers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_clientConnectActionPerformed

    private void supernodeConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supernodeConnectActionPerformed
        try {
            // TODO add your handling code here:
            initSupernode();
        } catch (InterruptedException ex) {
            Logger.getLogger(Beers4Peers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Beers4Peers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_supernodeConnectActionPerformed

    private void downloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadActionPerformed
        // TODO add your handling code here:
        client.downloadFile(jTextField1.getText());
    }//GEN-LAST:event_downloadActionPerformed

    private void uploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(null);
        File f = fc.getSelectedFile();

        String filename = f.getAbsolutePath();

        if (filename != null)
            client.newFile(filename);

    }//GEN-LAST:event_uploadActionPerformed

    private void connectDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectDisconnectActionPerformed
        // TODO add your handling code here:
        try {
            if (connectDisconnect.getText().equals("Connect")){
                if (type.equals("client")){
                    if (client.connect())
                        connectDisconnect.setText("Disconnect");
                }
                else
                    if (supernode.connect()){
                        connectDisconnect.setText("Disconnect");
                    }
            } else {
                if (type.equals("client")){
                    if (client.disconnect()){
                        connectDisconnect.setText("Connect");
                    }
                }
                else
                    if (supernode.disconnect()){
                        connectDisconnect.setText("Connect");
                    }
            }
        } catch (IOException ex) {
            Logger.getLogger(Beers4Peers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_connectDisconnectActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     *
     * The first argument is the address of the server the user want's to
     * connect to, and the second is the port number of the server.
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */

        //Check if argument's port is valid to the application
        if( args.length != 2 ){
           System.err.println("Usage: Beers4Peers <address> <port>");
           return;
        }

        SERVER_ADDRESS = args[0];

        // Convert the argument to ensure that is it valid
        SERVER_PORT = Integer.parseInt(args[1]);

        if (!(SERVER_PORT >= 49152 && SERVER_PORT <= 65535)){
            System.err.println( "Error: the port must be between 49152 and 65535." );
            return;
        }

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Beers4Peers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Beers4Peers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Beers4Peers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Beers4Peers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Beers4Peers().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clientConnect;
    private javax.swing.JButton connectDisconnect;
    private javax.swing.JButton download;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JFileChooser jFileChooser2;
    private javax.swing.JFileChooser jFileChooser3;
    private javax.swing.JFileChooser jFileChooser4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton supernodeConnect;
    private javax.swing.JButton upload;
    // End of variables declaration//GEN-END:variables

}
