package com.sun.api.controller.user;

import com.sun.grace.result.GraceJSONResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "hello", description = "hello")
public interface HelloControllerApi {
    @Operation(summary = "hello方法的接口", description = "hello方法的接口", method = "GET")
    @GetMapping("/hello")
    public Object hello();

}
