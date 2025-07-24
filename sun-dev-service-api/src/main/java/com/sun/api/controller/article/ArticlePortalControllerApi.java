package com.sun.api.controller.article;

import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.NewArticleBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Tag(name = "门户端文章业务", description = "门户端文章业务的Controller")
@RequestMapping("portal/article")
public interface ArticlePortalControllerApi {

    @Operation(summary = "门户端查询的文章列表", description = "门户端查询的文章列表", method = "GET")
    @GetMapping("/list")
    public GraceJSONResult list(@RequestParam String keyword,
                                       @RequestParam Integer category,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize);

    @Operation(summary = "门户端查询热文列表", description = "门户端查询热文列表", method = "GET")
    @GetMapping("/hotList")
    public GraceJSONResult hotList();

    @Operation(summary = "文章详情查询", description = "文章详情查询", method = "GET")
    @GetMapping("/detail")
    public GraceJSONResult detail(@RequestParam String articleId);

    @Operation(summary = "阅读文章，文章阅读量累加", description = "阅读文章，文章阅读量累加", method = "POST")
    @PostMapping("/readArticle")
    public GraceJSONResult readArticle(@RequestParam String articleId, HttpServletRequest request);

    @Operation(summary = "获得文章阅读数", description = "获得文章阅读数", method = "GET")
    @GetMapping("/readCounts")
    public Integer readCounts(@RequestParam String articleId, HttpServletRequest request);
}
