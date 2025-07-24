package com.sun.api.controller.admin;

import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.AdminLoginBO;
import com.sun.pojo.bo.SaveCategoryBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "文章分类维护", description = "文章分类的Controller")
@RequestMapping("categoryMng")
public interface CategoryMngControllerApi {

    @Operation(summary = "新增或分类修改", description = "新增或分类修改", method = "POST")
    @PostMapping("/saveOrUpdateCategory")
    public GraceJSONResult saveOrUpdateCategory(@RequestBody @Valid SaveCategoryBO saveCategoryBO,
                                                BindingResult bindingResult);

    @Operation(summary = "查询分类列表", description = "查询分类列表", method = "POST")
    @PostMapping("/getCatList")
    public GraceJSONResult getFriendLinkList();

    @GetMapping("/getCats")
    @Operation(summary = "用户端查询分类列表", description = "用户端查询分类列表", method = "GET")
    public GraceJSONResult getCats();

}
