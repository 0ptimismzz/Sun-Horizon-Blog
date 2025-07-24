package com.sun.api.controller.article;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.sun.grace.result.GraceJSONResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "AI判断", description = "AI判断的Controller")
@RequestMapping("article/AI")
public interface AIControllerApi {

    @Operation(summary = "AI判断文章是否有意义", description = "AI判断文章是否有意义", method = "GET")
    @GetMapping("/Judge")
    public GraceJSONResult articleAIJudge(@RequestParam String articleId) throws NoApiKeyException, InputRequiredException;
}
