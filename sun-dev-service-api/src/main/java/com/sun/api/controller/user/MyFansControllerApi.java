package com.sun.api.controller.user;

import com.sun.grace.result.GraceJSONResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "粉丝管理", description = "粉丝管理的Controller")
@RequestMapping("fans")
public interface MyFansControllerApi {
    @Operation(summary = "查询当前用户是否关注作者", description = "查询当前用户是否关注作者", method = "POST")
    @PostMapping("/isMeFollowThisWriter")
    public GraceJSONResult isMeFollowThisWriter(@RequestParam String writerId,
                                                @RequestParam String fanId);

    @Operation(summary = "用户关注作家，成为粉丝", description = "用户关注作家，成为粉丝", method = "POST")
    @PostMapping("/follow")
    public GraceJSONResult follow(@RequestParam String writerId,
                                                @RequestParam String fanId);

    @Operation(summary = "取消关注，损失粉丝", description = "取消关注，损失粉丝", method = "POST")
    @PostMapping("/unfollow")
    public GraceJSONResult unfollow(@RequestParam String writerId,
                                  @RequestParam String fanId);

    @Operation(summary = "查询我的所有粉丝列表", description = "查询我的所有粉丝列表", method = "POST")
    @PostMapping("/queryAll")
    public GraceJSONResult queryAll(@RequestParam String writerId,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize);

    @Operation(summary = "查询男女粉丝数目", description = "查询男女粉丝数目", method = "POST")
    @PostMapping("/queryRatio")
    public GraceJSONResult queryRatio(@RequestParam String writerId);

    @Operation(summary = "查询粉丝地域", description = "查询粉丝地域", method = "POST")
    @PostMapping("/queryRatioByRegion")
    public GraceJSONResult queryRatioByRegion(@RequestParam String writerId);

}
