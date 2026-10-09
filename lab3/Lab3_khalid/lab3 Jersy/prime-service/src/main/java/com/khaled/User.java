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
    

    private final Client client;
    private final WebTarget target;

    public User() {
        client = ClientBuilder.newClient();
        target = client.target("http://localhost:8080")
                       .path("/prime");
    }

    public Response getPrime(int id) {
        WebTarget getTarget = target.path("/numbers/{id}")
                .resolveTemplate("id", id);

        Invocation.Builder request =
                getTarget.request(MediaType.TEXT_PLAIN);

        return request.get();
    }

    public Response postPrime(int id, boolean prime) {
        WebTarget postTarget = target.path("/numbers/{id}/{prime}")
                .resolveTemplate("id", id)
                .resolveTemplate("prime", prime);

        Invocation.Builder request = postTarget.request();

        return request.post(null);
    }

    private boolean calculatePrimeness(int number) {
        if (number < 2) {
            return false;
        }

        for (int divisor = 2;
             divisor <= number / divisor;
             divisor++) {

            if (number % divisor == 0) {
                return false;
            }
        }

        return true;
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
