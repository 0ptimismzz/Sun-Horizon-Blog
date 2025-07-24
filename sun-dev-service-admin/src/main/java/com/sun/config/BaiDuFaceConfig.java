package com.sun.config;

import com.baidu.aip.face.AipFace;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaiDuFaceConfig {
    /**
     * 百度人脸识别API的App ID，配置文件中注入。
     * 使用该App ID进行API请求验证。
     */
    @Value("${baidu.face.appId}")
    private String appId;

    /**
     * 百度人脸识别API的API Key，配置文件中注入。
     * 用于身份验证和访问授权。
     */
    @Value("${baidu.face.apiKey}")
    private String apiKey;

    /**
     * 百度人脸识别API的Secret Key，配置文件中注入。
     * 用于加密签名，确保API请求的安全性。
     */
    @Value("${baidu.face.secretKey}")
    private String secretKey;

    /**
     * 创建并返回一个初始化后的AipFace实例。
     * 使用从配置文件中注入的APP_ID、API_KEY和SECRET_KEY。
     *
     * @return 初始化后的AipFace实例
     */
    @Bean
    public AipFace aipFace() {
        /*
         * 创建并初始化AipFace实例：
         * 使用配置文件中提供的App ID、API Key和Secret Key进行身份验证。
         * 该实例用于与百度人脸识别API进行后续的交互。
         */

        // 初始化AipFace实例
        return new AipFace(appId, apiKey, secretKey);
    }

}
