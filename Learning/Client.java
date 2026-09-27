import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class Client implements Runnable {

    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean done = false;

    @Override
    public void run() {
        try {
            client = new Socket("localhost", 9999);
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());

            // Del 1: en egen tråd som läser det du skriver på tangentbordet
            Thread inputThread = new Thread(new InputHandler());
            inputThread.setDaemon(true); // så att programmet kan avslutas även om tråden väntar på input
            inputThread.start();

            // Del 2: huvudtråden lyssnar hela tiden efter meddelanden från servern
            while (!done) {
                String message = in.readUTF();
                System.out.println(message);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        done = true;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (client != null && !client.isClosed()) client.close();
        } catch (IOException e) {
            // ignorera, vi stänger ändå
        }
    }

    // Inre klass som läser från tangentbordet och skickar till servern
    class InputHandler implements Runnable {

    @Override
    public void run() {
        try {
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            while (!done) {
                String message = keyboard.readLine();
                out.writeUTF(message);

                if (message.equals("/quit")) {
                    shutdown();
                }
            }
        } catch (IOException e) {
            shutdown();
        }
    }
}

    public static void main(String[] args) {
        Client clie = new Client();
        clie.run();
    }

}