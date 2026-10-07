package com.khaled;

import java.util.ArrayList;
import java.util.List;

public class PrimeServiceRepo {

    List<PrimeValue> primes = new ArrayList<>();
    

   public Boolean getPrimeness(int id) {
    for (PrimeValue p : primes) {
        if (id == p.getNumber()) {
            return p.getPrime();
        }
    }
    return null;
}

    public void postPrime(int id, boolean prime) {
        PrimeValue p = new PrimeValue();
        p.setNumber(id);
        p.setPrime(prime);

        primes.add(p);
    }
    
}
