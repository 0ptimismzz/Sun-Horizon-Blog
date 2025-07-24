package com.sun.article.task;

import com.sun.article.service.ArticleService;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
//@EnableScheduling
public class TaskPublishArticles {

    @Autowired
    private ArticleService articleService;

    @Scheduled(cron = "0/3 * * * * ?")
    private void publishArticles(){

        // 调用文章service，把当前时间应该发布的文章，状态改为即时
        articleService.updateAppointToPublish();
    }

}
