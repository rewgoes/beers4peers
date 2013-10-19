package client;

import Interface.Beers4Peers;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
class ClientTCPThread extends Thread{

    private Socket socket;
    private Client client;
    private PrintWriter out;
    private BufferedReader in;
    private String inputLine, outputLine;

    public ClientTCPThread(Socket socket, Client client){
        this.client = client;
        this.socket = socket;
    }

    //Interprets first line of the message received
    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));

            //TODO: SupernodeTCPListenerThread: Answer server, accepting client
            inputLine = in.readLine();
            
            System.out.println("Control: Message from " + socket.getInetAddress() + ":" + 
                    socket.getPort() + " content: " + inputLine);
            
            if(inputLine != null){
                switch (inputLine) {
                    //Server sends a reconnect message to client when its supernode disconnects
                    case "reconnect":
                        client.forceReconnect();
                        client.output1.append("Client " + inputLine + " reconnected to server successfully\n");
                        break;
                    //Supernode send a message to client asking to client to send a file
                    case "download":
                        System.out.println("Control: Preparing to upload file");
                        String clientToSend = in.readLine();
                        if(clientToSend != null){
                            String file = in.readLine();

                            if(file != null){

                                if (client.files.containsKey(file)){
                                    out.println("OK");

                                    sendFileTo(clientToSend, file);

                                    client.output1.append("File " + file +
                                            " sent to " + clientToSend + "\n");
                                }
                            }
                        }
                        break;
                    //Another client is sending the file
                    case "newFile":
                        receiveFile();
                        break;
                }

            }

            in.close();
            out.close();

        } catch (IOException ex) {
            System.err.println("Error: ServerThread (Problem reading or writing in socket): " + ex.getMessage());
        }
    }


    //send file to client interested on it
    private void sendFileTo(String clientToSend, String filename) {

        Socket connectionSocket;
        OutputStream outStream;
        PrintWriter outPrint;
        BufferedReader inBuffer;

        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(clientToSend, Beers4Peers.PORT), 1000);
            System.out.println("Control: Connected to client");
            
            File file = new File(client.files.get(filename));
            
            outPrint = new PrintWriter(connectionSocket.getOutputStream(), true);
            inBuffer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
            outStream = connectionSocket.getOutputStream();
            
            outPrint.println("newFile\n" + filename);

            //Wait for answer
            if(inBuffer.readLine() != null){
                System.out.println("Control: Sending file...");
                int count;
                byte[] buffer = new byte[1024];
                BufferedInputStream inTemp = new BufferedInputStream(new FileInputStream(file));
                while ((count=inTemp.read(buffer, 0, buffer.length)) != -1) {
                    outStream.write(buffer, 0, count);
                }
            } else {
                System.err.println("Error: Client (Client didn't answer)");
            }
            
            connectionSocket.close();
            
        } catch (UnknownHostException ex) {
            System.err.println("Error: Client (Don't know about host: " +
                    clientToSend + "): " + ex.getMessage());

            client.output1.append("Failed to connect: Client is offline\n");

        } catch (IOException ex) {
            System.err.println("Error: Client (Couldn't get I/O for the connection to: " + 
                    clientToSend + "): " + ex.getMessage());

            client.output1.append("Failed to connect: Client is offline\n");

        }

    }

    private void receiveFile() {
        try {
            String filename = in.readLine();
            
            if (filename != null){
                out.println("OK");
                
                System.out.println("Control: Receiveing file: " + filename);
                
                FileOutputStream fos = new FileOutputStream("download/" + filename);

                byte[] buffer = new byte[1024];
                int count;
                InputStream inStream = socket.getInputStream();
                while((count=inStream.read(buffer, 0, buffer.length)) != -1){
                    fos.write(buffer, 0, count);
                }
                
                client.output1.append("New file available: download/" + filename + "\n");

                fos.close();
            } else {
                System.err.println("Error: Client (Filename can't be null)");
            }

            socket.close();
         } catch (UnknownHostException ex) {
            System.err.println("Error: Client (Don't know about host: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            client.output1.append("Failed to connect: Client is offline\n");

        } catch (IOException ex) {
            System.err.println("Error: Client (Couldn't get I/O for the connection to: " +
                    Beers4Peers.SERVER_ADDRESS + "): " + ex.getMessage());

            client.output1.append("Failed to connect: Client is offline\n");

        }
    }

}
