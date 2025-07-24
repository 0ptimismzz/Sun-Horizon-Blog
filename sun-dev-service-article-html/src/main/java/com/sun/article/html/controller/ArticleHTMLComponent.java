package com.sun.article.html.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.sun.api.BaseController;
import com.sun.api.controller.article.ArticleHTMLControllerApi;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Component
public class ArticleHTMLComponent{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Value("${freemarker.html.article}")
    private String articlePath;

    public Integer download(String articleId, String articleMongoId) throws Exception{
        String path = articlePath + File.separator + articleId + ".html";

        File file = new File(path);

        OutputStream outputStream = new FileOutputStream(file);

        gridFSBucket.downloadToStream(new ObjectId(articleMongoId), outputStream);
        return HttpStatus.OK.value();
    }
}
