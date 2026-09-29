package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    
    private static ArrayList<ClientHandler> clients = new  ArrayList<>();
    private Socket socket;
    private boolean superUser = false;
    private String password = "/Admin";
    private String privateConnection = "Public";
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientUserName;

    public ClientHandler(Socket s){

            try
            {
                this.socket = s;
                this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                this.writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                this.clientUserName = reader.readLine();
                synchronized(clients){
                    clients.add(this);
                }
                broadcastMessage("has joined the chat.");
            }catch(IOException e)
            {
                close(socket, reader, writer);
                System.out.println("close called in constructor");
            }
    }

    private void routMessages(String messageToRout, String adress){
        if (this.privateConnection.equals("Public")) {
            broadcastMessage(messageToRout);
        }
        else
        {
            sendPrivateMessage(messageToRout, adress);
        }
    }

    private void sendPrivateMessage(String messageToRout, String adress) {
        
    }

    private void broadcastMessage(String messageToBroadCast){
        
        try{
            
            if (chehcPrivilage(messageToBroadCast)) {
                superUser = true;
                this.writer.write("You Are now an Admin");
                this.writer.newLine();
                this.writer.flush();
                return;
            }
        }catch(IOException e) {
            System.out.println("something went wrong in admin right in brodcast");
        }
        
        if (messageToBroadCast.startsWith("/ban") && this.superUser) 
            {
                messageToBroadCast = banUser(messageToBroadCast);
            }
            try
            {
                    synchronized(clients){
                    for (ClientHandler c: clients) {
                        
                        if (!c.clientUserName.equals(clientUserName)) {
                            c.writer.write(clientUserName + ": " + messageToBroadCast);
                            c.writer.newLine();
                            c.writer.flush();
                        }
                    }
                }
                }catch(IOException e)
                {
                    System.out.println("close called in broadcast");
                    close(socket, reader, writer);
                }
    }

    private String banUser(String messageToBroadCast) {
        String userToBan = messageToBroadCast.substring("/ban".length() + 1);// +1 since the string starts from index 0
        synchronized(clients){
            for (ClientHandler c : clients) {
                if (c.clientUserName.equals(userToBan) && !this.clientUserName.equals(userToBan)) {
                    c.close(c.socket, c.reader, c.writer);
                    return (c.clientUserName  + " was banned from the server!");
                }
            }
            return null;
        }

    }


    private boolean chehcPrivilage(String password) {
        return password.startsWith("/Admin");
    }


    private void removeClientHandler()
    {
        synchronized(clients){

            clients.remove(this);
            broadcastMessage(clientUserName + " has left the chat.");
        }
    }

    private void close(Socket s, BufferedReader br, BufferedWriter bw){
        try
        {
            if (s != null) {
                s.close();
            }
            if (br != null) {
                br.close();
            }
            if(bw != null){
                bw.close();
            }
            removeClientHandler();
        }catch(IOException e)
        {
        }
    }

    @Override
    public void run() {
        String message;

        while(!socket.isClosed())
        {
            try{
                message = reader.readLine();
                broadcastMessage(message);
            }catch(IOException e)
            {
                if (!socket.isClosed()) close(socket, reader, writer);
                break;
            }
        }
    }
}
