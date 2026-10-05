package lab2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * server
 */
public class Server implements Runnable {

    private ArrayList<ConnectionHandlar> connections;
    private ServerSocket server;
    private boolean done = false;
    private ExecutorService pool;

    public Server(){
        connections = new ArrayList<>();
        
    }

    @Override 
    public void run(){
        try {
            server = new ServerSocket(3333);
            pool = Executors.newCachedThreadPool();
            while (!done) {
            Socket client = server.accept();
            ConnectionHandlar handlar = new ConnectionHandlar(client);
            connections.add(handlar);
            pool.execute(handlar);
            }

        } catch (Exception e) {
            shoutDown();

        }

    }

            public void brodcast(String message){
            for (ConnectionHandlar ch : connections) {
                if (ch != null) {
                    ch.sendMessage(message);
                    
                }
                
            }
        }

        public void shoutDown(){
            
            try {
                done = true;
                if (!server.isClosed()) {
                server.close();
                
                }
                for (ConnectionHandlar ch : connections) {
                    ch.shoutDown();
                }
                
            } catch (Exception e) {
                // Ignore
            }
  

        }

    /**
     * Innerserver
     */
    class ConnectionHandlar implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public  ConnectionHandlar(Socket client){
            this.client = client;
        }



        @Override 
        public void run(){
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter a nikname:");
                nickname = in.readLine();
                System.out.println(nickname + " connected!");
                brodcast(nickname + " Joined the chat!");
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/nick ")) {
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            brodcast(nickname + " renamed themselves to " + messageSplit[1]);
                            System.out.println(nickname + " renamed themselves to " + messageSplit[1]);
                            nickname = messageSplit[1];
                            out.println("Successfully changed the nickname to " + nickname);
                            
                        }else{
                            System.out.println("No nickname provided! ");
                        }

                        
                    }else if (message.startsWith("/quit")) {
                        brodcast(nickname + " left the chat! ");
                        System.out.println("out");
                        shoutDown();
                        
                    }else {
                        brodcast(nickname+ ": " + message);
                    }
                }

            } catch (Exception e) {
                shoutDown();
            }

        }

        public void sendMessage(String Message){
            out.println(Message);
        }

        public void shoutDown(){
            try {

                in.close();
                out.close();
                if (!client.isClosed()) {
                client.close();
                
            }
                
            } catch (Exception e) {
                // Ignore
            }
 
        }
        
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
    
}