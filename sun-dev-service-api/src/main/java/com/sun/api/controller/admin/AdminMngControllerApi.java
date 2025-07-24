package com.sun.api.controller.admin;

import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.AdminLoginBO;
import com.sun.pojo.bo.NewAdminBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员admin维护", description = "管理员admin维护的Controller")
@RequestMapping("adminMng")
public interface AdminMngControllerApi {
    @Operation(summary = "管理员登录的接口", description = "管理员登录的接口", method = "POST")
    @PostMapping("/adminLogin")
    public GraceJSONResult adminLogin(@RequestBody AdminLoginBO adminLoginBO, HttpServletRequest request,
                                      HttpServletResponse response);

    @Operation(summary = "查询admin用户名是否存在", description = "查询admin用户名是否存在", method = "POST")
    @PostMapping("/adminIsExist")
    public GraceJSONResult adminIsExist(@RequestParam String username);

    @Operation(summary = "创建admin", description = "创建admin", method = "POST")
    @PostMapping("/addNewAdmin")
    public GraceJSONResult addNewAdmin(@RequestBody NewAdminBO newAdminBO);

    @Operation(summary = "查询admin列表", description = "查询admin列表", method = "POST")
    @PostMapping("/getAdminList")
    public GraceJSONResult getAdminList(@Parameter(name = "page", description = "查询下一页的第几页", required = false)
                                        @RequestParam Integer page,
                                        @Parameter(name = "pageSize", description = "分页查询每一页显示的条数", required = false)
                                        @RequestParam Integer pageSize);

    @Operation(summary = "admin退出登录", description = "admin退出登录", method = "POST")
    @PostMapping("/adminLogout")
    public GraceJSONResult adminLogout(@RequestParam String adminId,
                                       HttpServletRequest request,
                                       HttpServletResponse response);

    @Operation(summary = "admin管理员的人脸登录", description = "admin管理员的人脸登录", method = "POST")
    @PostMapping("/adminFaceLogin")
    public GraceJSONResult adminFaceLogin(@RequestBody AdminLoginBO adminLoginBO,
                                       HttpServletRequest request,
                                       HttpServletResponse response);

}
