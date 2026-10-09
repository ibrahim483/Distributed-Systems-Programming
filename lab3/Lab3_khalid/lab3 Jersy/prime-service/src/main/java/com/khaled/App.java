package com.khaled;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

public class App {

    public static void main(String[] args) throws Exception {

        //get port number on creation
        int port = Integer.parseInt(args[0]);
        
        PrimeServiceRepo repo = new PrimeServiceRepo();
        PrimeServiceResource resource = new PrimeServiceResource(repo);

        Client client = ClientBuilder.newClient();
        WebTarget registryTarget = client.target("http://localhost:8090").path("reg");
        
        ResourceConfig config = new ResourceConfig();
        config.register(resource);
    

        URI address = URI.create("http://localhost:" + port + "/");

        HttpServer server =
                GrizzlyHttpServerFactory.createHttpServer(address, config);


      
        try {
              try(Response response = registryTarget.path("add/{port}")
                                .resolveTemplate("port", port)
                                .request()
                                .post(null))
        {
            int status = response.getStatus();
            if (status >= 200 && status < 300) {
                System.out.println("Registered port " + port);
            } else {
                System.out.println("Registration failed: HTTP " + status);
            }
        }

        


        
        System.out.println("Server running at " + address);
        System.out.println("Press Enter to stop.");
        System.in.read();
        } finally {
            client.close();
            server.shutdownNow();
        }
    }
}