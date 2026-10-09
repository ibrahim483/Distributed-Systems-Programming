package com.khaled;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class RegistryApp {

    public static void main(String[] args) throws Exception {

        Registry registry = new Registry();

        ResourceConfig config = new ResourceConfig();
        config.register(registry);

        URI address = URI.create("http://localhost:8090/");

        HttpServer server =
                GrizzlyHttpServerFactory.createHttpServer(address, config);

        System.out.println("Registry running at " + address);
        System.out.println("Press Enter to stop.");

        try {
            System.in.read();
        } finally {
            server.shutdownNow();
        }
    }
}