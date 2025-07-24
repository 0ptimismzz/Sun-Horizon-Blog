package com.sun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@EnableEurekaServer  // 开启注册中心
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
