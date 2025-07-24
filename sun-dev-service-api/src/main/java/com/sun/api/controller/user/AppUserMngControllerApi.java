package com.sun.api.controller.user;

import com.sun.grace.result.GraceJSONResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Tag(name = "用户管理相关的接口定义", description = "用户管理相关功能的Controller")
@RequestMapping("appUser")
public interface AppUserMngControllerApi {
    @Operation(summary = "查询所有网站用户", description = "查询所有网站用户", method = "POST")
    @PostMapping("/queryAll")
    public GraceJSONResult queryAll(@RequestParam String nickname,
                                    @RequestParam Integer status,
                                    @RequestParam String startDate,
                                    @RequestParam String endDate,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize);

    @Operation(summary = "查询用户详情", description = "查询用户详情", method = "GET")
    @GetMapping("/userDetail")
    public GraceJSONResult userDetail(@RequestParam String userId);

    @Operation(summary = "解冻或冻结用户", description = "解冻或冻结用户", method = "POST")
    @PostMapping("/freezeUserOrNot")
    public GraceJSONResult freezeUserOrNot(@RequestParam String userId,
                                           @RequestParam Integer doStatus);
}
