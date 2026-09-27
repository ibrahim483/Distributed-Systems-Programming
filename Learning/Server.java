import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {

    private ArrayList<ConnectionHandlar> connections;
    private ServerSocket server;
    private boolean done = false;

    public Server() {
        connections = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            System.out.println("Server started on port 9999");

            while (!done) {
                Socket clinet = server.accept();
                ConnectionHandlar handlar = new ConnectionHandlar(clinet, this);
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

    public static void main(String[] args) {
        Server ser = new Server();
        ser.run();
    }
}