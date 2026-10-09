package com.khaled;

import java.util.ArrayList;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("reg")
public class Registry {

    private final ArrayList<Integer> ports = new ArrayList<>();

    @GET
    @Path("copy")
    @Produces(MediaType.TEXT_PLAIN)
    public synchronized String getServers() {
        StringBuilder result = new StringBuilder();

        for (int port : ports) {
            result.append(port).append("\n");
        }

        return result.toString();
    }

    @POST
    @Path("add/{port}")
    public synchronized void addPort(@PathParam("port") int port) {
        if (!ports.contains(port)) {
            ports.add(port);
        }
    }
}