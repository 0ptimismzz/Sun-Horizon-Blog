package com.sun.api.controller.admin;

import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.SaveFriendLinkBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "首页友情链接维护", description = "首页友情链接维护的Controller")
@RequestMapping("friendLinkMng")
public interface FriendLinkControllerApi {

    @Operation(summary = "新增或者修改友情链接", description = "新增或者修改友情链接", method = "POST")
    @PostMapping("/saveOrUpdateFriendLink")
    public GraceJSONResult saveOrUpdateFriendLink(@Valid @RequestBody SaveFriendLinkBO saveFriendLinkBO,
                                                  BindingResult bindingResult);

    @Operation(summary = "查询友情链接", description = "查询友情链接", method = "POST")
    @PostMapping("/getFriendLinkList")
    public GraceJSONResult getFriendLinkList();

    @Operation(summary = "删除友情链接", description = "删除友情链接", method = "POST")
    @PostMapping("/delete")
    public GraceJSONResult delete(@RequestParam String linkId);

    @Operation(summary = "门户端查询友情链接", description = "门户端查询友情链接", method = "GET")
    @GetMapping("/portal/list")
    public GraceJSONResult queryPortalAllFriendLinkList();



}
