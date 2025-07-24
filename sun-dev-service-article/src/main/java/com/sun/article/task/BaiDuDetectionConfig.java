package com.sun.article.task;

import com.baidu.aip.contentcensor.AipContentCensor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaiDuDetectionConfig {

    @Value("${baidu.detection.appId}")
    private String appId;

    @Value("${baidu.detection.apiKey}")
    private String apiKey;


    @Value("${baidu.detection.secretKey}")
    private String secretKey;

    @Bean
    public AipContentCensor aipContentCensor(){
        return new AipContentCensor(appId, apiKey, secretKey);
    }
}
