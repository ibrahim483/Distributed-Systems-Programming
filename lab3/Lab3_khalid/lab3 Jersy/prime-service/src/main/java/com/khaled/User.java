package com.khaled;

import java.util.Scanner;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class User {
    

    private Client client;
    private WebTarget target; 
    private WebTarget getTarget; 
    private WebTarget postTarget;
    private Invocation.Builder request;
    private Response response; 


    public User(){
        client = ClientBuilder.newClient();
        target = client.target("http://localhost:8080").path("/prime");
        getTarget = target.path("/numbers/{id}");
        postTarget = target.path("/numbers/{id}/{prime}");
        request = getTarget.request(MediaType.TEXT_PLAIN);
    }
    
    public Response getPrime(int id){
        getTarget = target.path("/numbers/{id}").resolveTemplate("id", id);
        return response = request.get();
        
        
    }
    public Response postPrime(int id, boolean prime){
        postTarget = target.path("/numbers/{id}/{prime}")
        .resolveTemplate("id", id)
        .resolveTemplate("prime", prime);
        return response= request.post(null);
    }

    private boolean calculatePrimeness() {
        //to be implemented later
        return false;
    } 

    public static void main(String[] args) {
        User u = new User();
        Scanner s = new Scanner(System.in);
        Response rs;
        while (true) {
            int number = Integer.parseInt(s.next());
            if (number.) {//check if this is actually a number othrewise skip or exit or return a rispons
                
            }
            rs = u.getPrime(number);
            if (rs.getStatus() == 404) {
                boolean prime = u.calculatePrimeness();
                u.postPrime(number, prime);
            }         
        }
        
        System.out.println(u.response.readEntity(String.class));

        u.response.close();
        u.client.close();
   



    }
}
