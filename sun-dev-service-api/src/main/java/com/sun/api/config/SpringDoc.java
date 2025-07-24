package com.sun.api.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDoc {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("曦和新闻·自媒体接口api")
                        .description("转为曦和新闻·自媒体平台提供的api文档")
                        .version("v1.0.1")
                        .contact(new Contact().name("sun").email("horizon@sun.com"))
                        .license(new License().name("sun 1.0").url("http://www.sun.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("see more at")
                        .url("http://www.sun.com"));
    }


}