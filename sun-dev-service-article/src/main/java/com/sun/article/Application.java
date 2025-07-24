package com.sun.article;

//import com.sun.api.config.MyLoadBalanceRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.sun.article.mapper")
@ComponentScan(basePackages = {"com.sun","org.n3r.idworker"})
//@LoadBalancerClient(name = "service-user", configuration = MyLoadBalanceRule.class)

public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        // 调用文章service
    }
}
