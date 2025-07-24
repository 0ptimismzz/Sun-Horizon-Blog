package com.sun.eureka.controller;

import com.sun.api.controller.user.HelloControllerApi;
import com.sun.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public Object hello() {
        return GraceJSONResult.ok("hello");
    }
}
