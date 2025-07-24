package com.sun.api.controller.user;
import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.UpdateUserInfoBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户信息相关", description = "用户信息相关的Controller")
@RequestMapping("user")
public interface UserControllerApi {

    @Operation(summary = "获得用户基本信息", description = "获得用户基本信息", method = "POST")
    @PostMapping("/getUserInfo")
    public GraceJSONResult getUserInfo(@RequestParam String userId);

    @Operation(summary = "获得用户账户信息", description = "获得用户账户信息", method = "POST")
    @PostMapping("/getAccountInfo")
    public GraceJSONResult getAccountInfo(@RequestParam String userId);

    @Operation(summary = "完善用户信息", description = "完善用户信息", method = "POST")
    @PostMapping("/updateUserInfo")
    public GraceJSONResult updateUserInfo(@RequestBody @Valid UpdateUserInfoBO updateUserInfoBO,
                                          BindingResult bindingResult);

    @Operation(summary = "根据用户的id查询列表", description = "根据用户的id查询列表", method = "GET")
    @GetMapping("/queryByIds")
    public GraceJSONResult queryByIds(@RequestParam String userIds);

    @Operation(summary = "根据用户的id查询", description = "根据用户的id查询查询", method = "GET")
    @GetMapping("/queryById")
    public GraceJSONResult queryById(@RequestParam String userId);
}
