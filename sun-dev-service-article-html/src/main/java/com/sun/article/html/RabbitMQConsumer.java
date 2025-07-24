package com.sun.article.html;

import com.sun.api.config.RabbitMQConfig;
import com.sun.article.html.controller.ArticleHTMLComponent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @Autowired
    private ArticleHTMLComponent articleHTMLComponent;

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_DOWNLOAD_ARTICLE})
    public void watchQueue(String payload, Message message) {
        System.out.println(payload);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        System.out.println(routingKey);
        String articleId = payload.split(",")[0];
        String articleMongoId = payload.split(",")[1];
        try {
            articleHTMLComponent.download(articleId, articleMongoId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
