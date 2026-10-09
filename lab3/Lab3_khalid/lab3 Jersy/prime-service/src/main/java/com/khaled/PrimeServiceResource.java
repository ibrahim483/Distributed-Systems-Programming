package com.khaled;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path ("/prime")
public class PrimeServiceResource {
    
    private PrimeServiceRepo repo;

    public PrimeServiceResource(PrimeServiceRepo repo){
        this.repo = repo;
    }


    @GET
    @Path("/numbers/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPrimeness(@PathParam("id") int id) {

        Boolean prime = repo.getPrimeness(id);

        if (prime == null) {
            return Response.status(404)
                    .entity("Number not registered")
                    .build();
        }

        return Response.ok(prime.toString()).build();
    }

    @POST 
    @Path("/numbers/{id}/{prime}")
    public void postPrime(@PathParam("id") int id, @PathParam("prime") boolean prime)
    {
        repo.postPrime(id, prime);

    }

}
