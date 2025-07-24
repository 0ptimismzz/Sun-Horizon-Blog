package com.sun.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestLoadBalance {

    @GetMapping("/load")
    public String loadBalance() {
        System.out.println("user1");
        return "user1";
    }
}
