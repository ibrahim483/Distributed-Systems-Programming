package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket socket;

    public Server(ServerSocket socket){
        this.socket = socket;
    }

    private void startServer(){
        try{
            while (!socket.isClosed()) {
                Socket newSocket = socket.accept();
                System.out.println("A new client has joined.");


                ClientHandler handler = new ClientHandler(newSocket);
                Thread thread = new Thread(handler);
                thread.start();
            }
        }catch(IOException e){

        }
    }
    private void closeServer(){
        
        try
        {
            if (socket != null) {
                socket.close();
            }
        }catch(IOException e)
        {

        }
    }

    public static void main(String[] args) throws IOException {
        
        ServerSocket s = new ServerSocket(1234);
        Server server = new Server(s);
        server.startServer();
        

        
 
    }
}