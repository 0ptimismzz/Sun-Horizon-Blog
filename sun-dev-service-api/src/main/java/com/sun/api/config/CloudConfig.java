//package com.sun.api.config;
//
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//@Configuration
//public class CloudConfig {
//
//    public CloudConfig() {
//    }
//
//    /**
//     * 基于okhttp3的配置来实例化RestTemplate
//     * @return
//     */
//    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//
//}
