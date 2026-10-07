package lab2_TCP_server_ibrahim;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class Client implements Runnable {

    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean done = false;

    @Override
    public void run() {
        try {
            client = new Socket("localhost", 9999);
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());

            
            Thread inputThread = new Thread(new InputHandler());
            inputThread.setDaemon(true); 
            inputThread.start();

            while (!done) {
                String message = in.readUTF();
                System.out.println(message);
            }
        } catch (IOException e) {
                    
            shutdown();
        }
    }

    public void shutdown() {
        done = true;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (client != null && !client.isClosed()) client.close();
        } catch (IOException e) {

        }
    }

    class InputHandler implements Runnable {

    @Override
    public void run() {
        try {
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            while (!done) {
                String message = keyboard.readLine();
                if (message == null) {
                    shutdown();
                    break;
                }
                out.writeUTF(message);

                if (message.equals("/quit")) {
                    shutdown();
                }

            }
        } catch (IOException e) {
            shutdown();
        }
    }

}


    public static void main(String[] args) {
        Client clie = new Client();
        clie.run();
    }

}