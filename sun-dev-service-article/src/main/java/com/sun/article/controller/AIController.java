package com.sun.article.controller;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.sun.api.controller.article.AIControllerApi;
import com.sun.article.service.AIService;
import com.sun.grace.result.GraceJSONResult;
import com.alibaba.dashscope.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIController implements AIControllerApi {

    @Autowired
    private AIService aiService;

    @Override
    public GraceJSONResult articleAIJudge(String articleId) throws NoApiKeyException, InputRequiredException {
        GenerationResult result = aiService.articleAIJudge(articleId);
        return GraceJSONResult.ok(JsonUtils.toJson(result));
    }
}
