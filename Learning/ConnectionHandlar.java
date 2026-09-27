import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandlar implements Runnable {

    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;
    private Server server;

    public ConnectionHandlar(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());

            out.writeUTF("Enter your name: ");
            nickname = in.readUTF();
            System.out.println(nickname + " is connected!");
            server.brodcast(nickname + " joined the chat!");

            // Läs meddelanden från klienten tills den skriver /quit
            while (true) {
                String message = in.readUTF();

                if (message.equals("/quit")) {
                    server.brodcast(nickname + " left the chat!");
                    break;
                }

                server.brodcast(nickname + ": " + message);
            }
            shutdown();

        } catch (IOException e) {
            shutdown();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            
        }
    }

    public void shutdown() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (!client.isClosed()) client.close();
        } catch (IOException e) {
            
        }
    }
}