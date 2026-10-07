package com.khaled;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class App {

    public static void main(String[] args) throws Exception {

        // 1. Create your existing objects.
        PrimeServiceRepo repo = new PrimeServiceRepo();
        PrimeServiceResource resource = new PrimeServiceResource(repo);

        // 2. Tell Jersey which resource to use.
        ResourceConfig config = new ResourceConfig();
        config.register(resource);

        // 3. Choose the server's address.
        URI address = URI.create("http://localhost:8080/");

        // 4. Create AND start the HTTP server.
        HttpServer server =
                GrizzlyHttpServerFactory.createHttpServer(address, config);

        System.out.println("Server running at " + address);
        System.out.println("Press Enter to stop.");

        try {
            // Keep the program running until you press Enter.
            System.in.read();
        } finally {
            server.shutdownNow();
        }
    }
}