package com.sun.utils.extend;


import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomResource {   
    public String randomCode() {
        Random random = new Random();
        return random.nextInt(10) + "" + random.nextInt(10) + "" +
                random.nextInt(10) + "" + random.nextInt(10);
    }


}
