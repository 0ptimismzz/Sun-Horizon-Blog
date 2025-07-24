package com.sun.api.controller.article;

import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.NewArticleBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Tag(name = "静态化文章业务", description = "静态化文章业务的Controller")
@RequestMapping("article/html")
public interface ArticleHTMLControllerApi {

    @Operation(summary = "下载Html", description = "下载Html", method = "GET")
    @GetMapping("/download")
    public Integer download(String articleId,String articleMongoId) throws Exception;

}
