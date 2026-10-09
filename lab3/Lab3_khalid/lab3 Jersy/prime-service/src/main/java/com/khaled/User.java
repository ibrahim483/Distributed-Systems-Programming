package com.khaled;

import java.util.Scanner;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
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

        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.print("Enter a number to check if it is prime, or 'exit': ");
                System.out.println();
                if (!scanner.hasNext()) {
                    break;
                }

                if (scanner.hasNext("exit")) {
                    scanner.next();
                    break;
                }

                if (!scanner.hasNextInt()) {
                    String invalid = scanner.next();
                    System.out.println(
                            "'" + invalid + "' is not a valid int. Try again."
                    );
                    continue;
                }

                int number = scanner.nextInt();

                try {
                    // First ask the server for an existing result.
                    try (Response response = u.getPrime(number)) {
                        int status = response.getStatus();

                        if (status == 200) {
                            String result =
                                    response.readEntity(String.class);

                            System.out.println(
                                    "Stored result: " + number
                                    + " is prime = " + result
                            );
                            continue;
                        }

                        if (status != 404) {
                            System.out.println(
                                    "Lookup failed. HTTP status: " + status
                            );
                            continue;
                        }
                    }

                    // Only calculate when the server reported 404.
                    boolean prime = u.calculatePrimeness(number);

                    System.out.println(
                            "Calculated result: " + number
                            + " is prime = " + prime
                    );

                    // Submit the result to the server.
                    try (Response response = u.postPrime(number, prime)) {
                        int status = response.getStatus();

                        if (status >= 200 && status < 300) {
                            System.out.println("Result saved.");
                        } else {
                            System.out.println(
                                    "Saving failed. HTTP status: " + status
                            );
                        }
                    }

                } catch (jakarta.ws.rs.ProcessingException e) {
                    System.out.println(
                            "Could not complete the HTTP request: "
                            + e.getMessage()
                    );
                }
            }

        } finally {
            u.client.close();
        }
    }
}