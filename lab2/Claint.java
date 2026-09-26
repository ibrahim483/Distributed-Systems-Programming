import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Claint implements Runnable {

    private Socket clinet;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try {
            Socket client = new Socket("localhost", 3333);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }
        } catch (IOException e) {
            shutdown();

        }
    }

    public void shutdown(){
        done = true;
        try {
            in.close();
            out.close();
            if (!clinet.isClosed()) {
                clinet.close();
                
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    class InputHandler implements Runnable{

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String massege = inReader.readLine();
                    if (massege.equals("/quit")) {
                        inReader.close();
                        shutdown();
                        
                    }else{
                        out.println(massege);
                    }
                    
                }
            } catch (IOException e) {
                shutdown();
            }
        }
        
    }
    public static void main(String[] args) {
        Claint client = new Claint();
        client.run();
    }
    
}
