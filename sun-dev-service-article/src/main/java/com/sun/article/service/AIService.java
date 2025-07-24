package com.sun.article.service;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

public interface AIService {

    public GenerationResult articleAIJudge(String articleId) throws NoApiKeyException, InputRequiredException;
}
