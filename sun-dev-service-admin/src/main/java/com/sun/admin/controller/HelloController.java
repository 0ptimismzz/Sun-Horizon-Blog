package com.sun.admin.controller;

import com.sun.api.controller.user.HelloControllerApi;
import com.sun.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController implements HelloControllerApi {
    @Override
    public Object hello() {
        return GraceJSONResult.ok("hello");
    }
}
