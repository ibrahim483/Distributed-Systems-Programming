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
            clients.add(this);
            broadcastMessage("SERVER: " + clientUserName + " has joined the chat.");
        }catch(IOException e)
        {
            close(socket, reader, writer);
        }
    }

    private void broadcastMessage(String messageToBroadCast){

        try
        {
            for (ClientHandler c: clients) {
                
                if (!c.clientUserName.equals(clientUserName)) {
                    c.writer.write(messageToBroadCast);
                    c.writer.newLine();
                    c.writer.flush();
                }
            }
        }catch(IOException e)
        {
            close(socket, reader, writer);
        }

    }

    private void removeClientHandler()
    {
        clients.remove(this);
        broadcastMessage("SERVER: " + clientUserName + " has left the chat.");
    }

    private void close(Socket s, BufferedReader br, BufferedWriter bw){
        removeClientHandler();
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
        }catch(IOException e)
        {
        }
    }

    @Override
    public void run() {
        String message;

        while(socket.isConnected())
        {
            try{
                message = reader.readLine();
                broadcastMessage(message);
            }catch(IOException e)
            {
                close(socket, reader, writer);
                break;
            }
        }
    }
}
