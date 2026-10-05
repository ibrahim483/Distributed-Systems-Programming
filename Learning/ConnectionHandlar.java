package Learning;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandlar implements Runnable {

    private static final String ADMIN_PASSWORD = "kalb";


    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;
    private Server server;

    private volatile boolean kicked = false;



    public ConnectionHandlar(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

   @Override
    public void run() {
        try {
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());
 
            sendMessage("Enter your name: ");
            nickname = in.readUTF();
 
            // NYTT: stoppa bannade användare direkt vid inloggning
            if (server.isBanned(nickname)) {
                sendMessage("You are banned from this server.");
                server.dicrese(this);
                shutdown();
                return;
            }
 
            System.out.println(nickname + " is connected!");
            server.brodcast(nickname + " joined the chat!");
 
            while (true) {
                String message = in.readUTF();
 
                if (message.equals("/quit")) {
                    int rest = server.dicrese(this);
                    server.brodcast(nickname + " left the chat!");
                    System.out.println(nickname + " left the server");
                    System.out.println("There is " + rest + " in this server");
                    break;
 
                } else if (message.startsWith("/admin")) {
                    handleAdmin(message);
 
                } else if (message.startsWith("/ban")) {
                    handleBan(message);
 
                } else {
                    server.brodcast(nickname + ": " + message);
                }
            }
            shutdown();
 
        } catch (IOException e) {
            int rest = server.dicrese(this);
            // om vi blev bannade har admin redan berättat det för alla
            if (nickname != null && !kicked) {
                server.brodcast(nickname + " lost connection");
                System.out.println(nickname + " lost connection");
                System.out.println("There is " + rest + " in this server");
            }
            shutdown();
        }
    }

    private void handleAdmin(String message){
        String[] parts = message.split(" ", 2);

        if (parts.length == 2 && parts[1].equals(ADMIN_PASSWORD)) {

            if (server.tryBecomeAdmin(this)) {

                sendMessage("You are admin");
                System.out.println(nickname + " is now admin");
                
            }else {
                sendMessage("There is already an admin on this server");
   }
        }else{

            sendMessage("You enterd a wrong password");
        }
    }

    private void handleBan(String message){

        if (!server.isAdmin(this)) {
            sendMessage("You are not the admin");
            return;
        }
        String[] parts = message.split(" ", 2);
        if (parts.length < 2) {
            sendMessage("To ban somebody type: /ban <name>");
            return; 
        }

        String target = parts[1];

        if (target.equals(nickname)) {
            sendMessage("You are the admin you cant ban yourself!! ");
            return;
        
        }

        if(server.stillLive(target)){

            server.addban(target);

            ConnectionHandlar victim = server.findByName(target);
            if (victim != null) {
                victim.kick();
            
        }

        server.brodcast(target + " was banned by " + nickname);
        System.out.println(target + " was banned by " + nickname);

        }else{
            sendMessage("There is no such user in this server! ");
        }


    }

    private void kick(){
        kicked = true;
        sendMessage("You have been kicked! ");
        server.dicrese(this);
        shutdown();
    }
    public void sendMessage(String message) {

        synchronized(server.getLock()){
                    try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Error while sending a message");
            
        }



        }

    }

    public void shutdown() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (!client.isClosed()) client.close();
        } catch (IOException e) {
            System.out.println("Error while closing connection");
            
        }
    }

    public String getNickname(){
        return nickname;
    }

}

