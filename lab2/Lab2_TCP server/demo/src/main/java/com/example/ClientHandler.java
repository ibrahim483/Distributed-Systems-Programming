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
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientUserName;

    public ClientHandler(Socket s){

            try
            {
                this.socket = s;
                System.out.println(s);
                this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                this.writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                this.clientUserName = reader.readLine();
                synchronized(clients){
                    clients.add(this);
                }
                broadcastMessage("SERVER: " + clientUserName + " has joined the chat.");
            }catch(IOException e)
            {
                close(socket, reader, writer);
                System.out.println("close called in constructor");
            }
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
        
        if (messageToBroadCast.substring(this.clientUserName.length() + 2, this.clientUserName.length() + 6).equals("/ban") && this.superUser) 
            {
                banUser(messageToBroadCast);
                return;
            }
            try
            {
                    synchronized(clients){
                    for (ClientHandler c: clients) {
                        
                        if (!c.clientUserName.equals(clientUserName)) {
                            c.writer.write(messageToBroadCast);
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

    private void banUser(String messageToBroadCast) {
        String userToBan = messageToBroadCast.substring(7 + clientUserName.length());
        synchronized(clients){
            for (ClientHandler c : clients) {
                if (c.clientUserName.equals(userToBan) && !this.clientUserName.equals(userToBan)) {
                    System.out.println("close called in Ban");
                    c.close(c.socket, c.reader, c.writer);
                    return;
                }
            }
        }

    }


    private boolean chehcPrivilage(String password) {
        String code = password.substring(this.clientUserName.length() + 2);
        return this.password.equals(code);
    }


    private void removeClientHandler()
    {
        synchronized(clients){

            clients.remove(this);
            broadcastMessage("SERVER: " + clientUserName + " has left the chat.");
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
