package Learning;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {

    private  CopyOnWriteArrayList<ConnectionHandlar> connections;
    private ConnectionHandlar admin = null;   // den enda admin på servern (null = ingen)
    private CopyOnWriteArrayList<String> bannedNames = new CopyOnWriteArrayList<>();
    private ServerSocket server;
    private boolean done = false;
    private final Object lock = new Object();

    public Server() {
        connections = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            System.out.println("Server started on port 9999");

            while (!done) {
                Socket clinet = server.accept();
                ConnectionHandlar handlar = new ConnectionHandlar(clinet, this );
                connections.add(handlar);
                new Thread(handlar).start();
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void brodcast(String message) {
        for (ConnectionHandlar ch : connections) {
            if (ch != null) {

                ch.sendMessage(message);

                }
            }
        }


    private void shutdown() {
        done = true;
        try {
            if (server != null && !server.isClosed()) {
                server.close();
            }
        } catch (IOException e) {
            // ignorera
        }
        for (ConnectionHandlar ch : connections) {
            ch.shutdown();
        }
    }

    public int dicrese(ConnectionHandlar handlar){
        connections.remove(handlar);
        removeAdmin(handlar);
        int size = connections.size();
        return size;
    }

    public Object getLock(){
        return lock;
    }

    public ConnectionHandlar findByName(String name){

        for (ConnectionHandlar ch : connections) {
            if (name.equals(ch.getNickname())) {
                return ch; 
            }
        }
        return null;
    }

    public boolean stillLive(String name){
        if (connections.contains(findByName(name))) {
            return true;
        }

        return false;
        
        
    }

    public boolean isBanned(String name){
       return bannedNames.contains(name);
    }

    public void addban(String name){
        if (!bannedNames.contains(name)) {
            bannedNames.add(name);
            
        }
    }


   

// bara EN tråd åt gången får kolla och sätta admin
public synchronized boolean tryBecomeAdmin(ConnectionHandlar ch) {
    if (admin == null) {
        admin = ch;
        return true;
    }
    return false;
}

public synchronized boolean isAdmin(ConnectionHandlar ch) {
    return admin == ch;
}

public synchronized void removeAdmin(ConnectionHandlar ch) {
    if (admin == ch) {
        admin = null;
    }
}

    public static void main(String[] args) {
        Server ser = new Server();
        ser.run();
    }

    public synchronized boolean connectPartnar(ConnectionHandlar a, ConnectionHandlar b){
        if (a.getPartnar() == null && b.getPartnar() == null ) {

            a.setPartnar(b);
            b.setPartnar(a);

            return true;
        }

        return false;
    }

    public synchronized ConnectionHandlar removePartnar(ConnectionHandlar a){

        ConnectionHandlar b = a.getPartnar();

        if (a.getPartnar() != null && b.getPartnar() != null) {

            a.setPartnar(null);
            b.setPartnar(null);

        }

        return b;

    }


}