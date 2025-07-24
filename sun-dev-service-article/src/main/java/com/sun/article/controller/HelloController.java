package com.sun.article.controller;

import com.sun.api.config.RabbitMQConfig;
import com.sun.api.controller.user.HelloControllerApi;
import com.sun.grace.result.GraceJSONResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

@RestController
//@RequestMapping("free")
@RequestMapping("produce")
public class HelloController {

    @Value("${freemarker.html.target}")
    private String htmlTarget;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/helloha")
    public Object hello(){

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE, "article.hello", "这是从生产者发送的消息~");
        return GraceJSONResult.ok();
    }

    @ResponseBody
    @GetMapping("/creatHtml")
    public String creatHtml(Model model) throws  Exception{
        // 0. 配置freemarker基本环境
        Configuration cfg = new Configuration(Configuration.getVersion());
        // 声明freemarker模板所需要加载的目录的位置
        String classPath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classPath + "templates"));
        System.out.println(htmlTarget);
        System.out.println(classPath + "templates");
        // 1. 获得现有的模板ftl文件
        Template template = cfg.getTemplate("stu.ftl", "UTF-8");
        // 2. 获得动态数据
//        model = makeModel(model);
        // 3. 融合动态数据和ftl生成html
        File tempDic = new File(htmlTarget);
        if (!tempDic.exists()) {
            tempDic.mkdirs();
        }
        Writer out = new FileWriter(htmlTarget + File.separator + "10010" + ".html");
        template.process(model, out);
        out.close();
        return "ok";
    }

    @GetMapping("/hello")
    public String hello(Model model) {
        // 定义输出到模板的内容
        // 输出字符串
        String msg = "lalala";
        model.addAttribute("there", msg);
        return "stu";
    }
}
