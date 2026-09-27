package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client
 */
public class Client{

    private Socket socket;
    private String userName;
    private BufferedReader reader;
    private BufferedWriter writer;

    public Client(Socket socket, String userName){
        try{

            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName = userName;

        } catch(IOException e){
            close(socket, reader, writer);
        }

    }

    private void sendMessage(){

        try
        {
            writer.write(userName);
            writer.newLine();
            writer.flush();

            Scanner s = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = s.nextLine();
                writer.write(userName + ": " +  message);
                writer.newLine();
                writer.flush();
            }
        }catch(IOException e)
        {
            close(socket,reader,writer);
        }

    }

    private void listen(){
        new Thread(new Runnable() {

            @Override
            public void run() {
               String message;
               
               while (socket.isConnected()) {
                    try
                    {
                        message = reader.readLine();
                        System.out.println(message);
                    }catch(IOException e)
                    {
                        close(socket, reader, writer);
                    }
               }
            }
            
        }).start();
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
        }catch(IOException e)
        {
        }
    }

    public static void main(String[] args) throws IOException{
        Socket s = new Socket("localhost", 1234);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter UserName:");
        String UserName = scanner.next();

        Client c = new Client(s, UserName);
        c.listen(); 
        c.sendMessage();
    }




}