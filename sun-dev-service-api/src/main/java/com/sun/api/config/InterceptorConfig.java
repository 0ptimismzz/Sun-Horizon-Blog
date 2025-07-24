package com.sun.api.config;

import com.sun.api.interceptors.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor(){
        return new PassportInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor(){
        return new UserTokenInterceptor();
    }

    @Bean
    public UserActiveInterceptor userActiveInterceptor(){
        return new UserActiveInterceptor();
    }

    @Bean
    public AdminTokenInterceptor adminTokenInterceptor(){return new AdminTokenInterceptor();}

    @Bean
    public ArticleReadInterceptor articleReadInterceptor(){return new ArticleReadInterceptor();}

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getSMSCode");

        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/user/getAccountInfo")
                .addPathPatterns("/user/updateUserInfo")
                .addPathPatterns("/fs/uploadFace")
                .addPathPatterns("/fs/uploadSomeFiles");

        registry.addInterceptor(adminTokenInterceptor())
                .addPathPatterns("/adminMng/adminIsExist")
                .addPathPatterns("/adminMng/addNewAdmin")
                .addPathPatterns("/adminMng/getAdminList")
                .addPathPatterns("/fs/uploadToGridFS")
                .addPathPatterns("/fs/readInGridFS")
                .addPathPatterns("/friendLinkMng/saveOrUpdateFriendLink")
                .addPathPatterns("/friendLinkMng/getFriendLinkList")
                .addPathPatterns("/friendLinkMng/delete");

        registry.addInterceptor(userActiveInterceptor())
                .addPathPatterns("/fs/uploadSomeFiles");

        registry.addInterceptor(articleReadInterceptor())
                .addPathPatterns("/portal/article/readArticle");
    }
}
