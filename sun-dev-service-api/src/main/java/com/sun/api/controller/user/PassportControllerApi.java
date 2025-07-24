package com.sun.api.controller.user;


import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.RegistLoginBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户注册登录", description = "用户注册登录的Controller")
@RequestMapping("passport")
public interface PassportControllerApi {

    @Operation(summary = "获得短信验证码", description = "获得短信验证码", method = "GET")
    @GetMapping("/getSMSCode")
    public GraceJSONResult getSMSCode(@RequestParam String mobile, HttpServletRequest request);

    @Operation(summary = "一键注册登录接口", description = "一键注册登录接口", method = "POST")
    @PostMapping("/doLogin")
    public GraceJSONResult doLogin(@Valid @RequestBody RegistLoginBO registLoginBO,
                                   BindingResult bindingResult,
                                   HttpServletRequest request,
                                   HttpServletResponse response);

    @Operation(summary = "用户退出登录", description = "用户退出登录", method = "POST")
    @PostMapping("/logout")
    public GraceJSONResult logout(@RequestParam String userId,
                                   HttpServletRequest request,
                                   HttpServletResponse response);

}
