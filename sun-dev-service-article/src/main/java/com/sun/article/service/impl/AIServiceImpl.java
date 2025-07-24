package com.sun.article.service.impl;

import com.alibaba.dashscope.aigc.conversation.Conversation;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.sun.article.mapper.ArticleMapper;
import com.sun.article.service.AIService;
import com.sun.enums.AIContent;
import com.sun.pojo.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AIServiceImpl implements AIService {

    @Autowired
    private ArticleMapper articleMapper;

    @Value("${alibaba.api}")
    private String apiKey;

    @Override
    public GenerationResult articleAIJudge(String articleId) throws NoApiKeyException, InputRequiredException {
        Article article = articleMapper.selectByPrimaryKey(articleId);
        String content = article.getContent();
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        System.out.println(AIContent.ARTICLE_REVIEW.value + content);
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(AIContent.ARTICLE_REVIEW.value + content)
                .build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(apiKey)
                // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);

    }
}
