import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EhcoServer {

    public static void main(String[] args) {
        try {
            System.out.println("Waiting for clinets !");
            ServerSocket server = new ServerSocket(9999);
            Socket soc =  server.accept();
            System.out.println("Connection establish");
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String str = in.readLine();
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
            out.println("server says " + str);
            
           



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }s

        
    }
    
}
