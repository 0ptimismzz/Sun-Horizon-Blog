package com.sun.article.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.sun.api.BaseController;
import com.sun.api.config.RabbitMQConfig;
import com.sun.api.controller.article.ArticleControllerApi;
import com.sun.article.service.ArticleService;
import com.sun.enums.ArticleCoverType;
import com.sun.enums.ArticleReviewStatus;
import com.sun.enums.YesOrNo;
import com.sun.exception.GraceException;
import com.sun.grace.result.GraceJSONResult;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.Category;
import com.sun.pojo.bo.NewArticleBO;
import com.sun.pojo.vo.AppUserVO;
import com.sun.pojo.vo.ArticleDetailVO;
import com.sun.utils.JsonUtils;
import com.sun.utils.PagedGridResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Override
    public GraceJSONResult adminLogin(@Valid NewArticleBO newArticleBO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> map = getErrors(bindingResult);
            return GraceJSONResult.errorMap(map);
        }

        // 判断文章封面类型
        if (newArticleBO.getArticleType() == ArticleCoverType.ONE_IMAGE.type){
            if (StringUtils.isBlank(newArticleBO.getArticleCover())){
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
            }
        } else if (newArticleBO.getArticleType() == ArticleCoverType.WORDS.type) {
            newArticleBO.setArticleCover("");
        }

        // 判断分类id是否存在
        String allCatJson = redisOperator.get(REDIS_ALL_CATEGORY);
        Category temp = null;
        if (StringUtils.isBlank(allCatJson)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }else{
            List<Category> catList = JsonUtils.jsonToList(allCatJson, Category.class);
            for (Category c : catList) {
                if (c.getId() == newArticleBO.getCategoryId()) {
                    temp = c;
                    break;
                }
            }
            if (temp == null) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
            }
        }

        articleService.creatArticle(newArticleBO,temp);


        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryMyList(String userId, String keyword, Integer status,
                                       Date startDate, Date endDate, Integer page, Integer pageSize) {
        System.out.println(status);

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        // 查询列表
        PagedGridResult result = articleService.queryMyArticleList(userId, keyword, status, startDate, endDate, page, pageSize);

        return GraceJSONResult.ok(result);
    }


    @Override
    public GraceJSONResult queryMyList(Integer status, Integer page, Integer pageSize) {
        System.out.println("状态为："+status);
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult gridResult = articleService.queryAllArticleListAdmin(status, page, pageSize);
        return GraceJSONResult.ok(gridResult);
    }

    @Override
    public GraceJSONResult doReview(String articleId, Integer passOrNot) {
        Integer pendingStatus;

        if (passOrNot == YesOrNo.YES.type) {
            // 审核成功
            pendingStatus = ArticleReviewStatus.SUCCESS.type;
        } else if (passOrNot == YesOrNo.NO.type) {
            // 审核失败
            pendingStatus = ArticleReviewStatus.FAILED.type;
        }else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        articleService.updateArticleStatus(articleId, pendingStatus);

        if (pendingStatus == ArticleReviewStatus.SUCCESS.type) {
            // 审核成功，生成文章详情页
            try {
//                creatArticleHtml(articleId);
                String articleMongoId = creatArticleHtmlToGridFS(articleId);
                //存储到文章进行关联保存
                articleService.updateArticleToGidFS(articleId, articleMongoId);
                //调用消费端执行下载html
//                doDownloadArticleHtml(articleId, articleMongoId);
                //使用mq，让消费者监听并且执行下载html
//                doDownloadArticleHtmlByMQ(articleId, articleMongoId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return GraceJSONResult.ok();
    }

    private void doDownloadArticleHtml(String articleId, String articleMongoId) {

        String url =
                "http://html.sun.com:8002/article/html/download?articleId=" + articleId + "&articleMongoId=" + articleMongoId;
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url, Integer.class);
        Integer status = responseEntity.getBody();
        if (status != HttpStatus.OK.value()) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
    }

    @Value("${freemarker.html.article}")
    private String articlePath;

    public void creatArticleHtml(String articleId) throws  Exception{
        Configuration cfg = new Configuration(Configuration.getVersion());
        String classPath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classPath + "templates"));

        Template template = cfg.getTemplate("detail.ftl", "UTF-8");

        // 获得文章的详情数据
        String userServerUrlExecute =
                "http://www.sun.com:8001/portal/article/detail?articleId=" + articleId;
        System.out.println("远程服务地址"+userServerUrlExecute);
        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(userServerUrlExecute, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        String detailJson = "";
        ArticleDetailVO articleDetailVO = null;
        if (bodyResult.getStatus() == 200) {
            detailJson = JsonUtils.objectToJson(bodyResult.getData());
            articleDetailVO = JsonUtils.jsonToPojo(detailJson, ArticleDetailVO.class);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("articleDetail", articleDetailVO);

        File tempDic = new File(articlePath);
        if (!tempDic.exists()) {
            tempDic.mkdirs();
        }
        articlePath = articlePath + File.separator + articleDetailVO.getId() + ".html";

        Writer out = new FileWriter(articlePath);
        template.process(map, out);
        out.close();
    }

    public String creatArticleHtmlToGridFS(String articleId) throws  Exception{
        Configuration cfg = new Configuration(Configuration.getVersion());
        String classPath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classPath + "templates"));

        Template template = cfg.getTemplate("detail.ftl", "UTF-8");

        // 获得文章的详情数据
        String userServerUrlExecute =
                "http://www.sun.com:8001/portal/article/detail?articleId=" + articleId;
        System.out.println("远程服务地址"+userServerUrlExecute);
        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(userServerUrlExecute, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        String detailJson = "";
        ArticleDetailVO articleDetailVO = null;
        if (bodyResult.getStatus() == 200) {
            detailJson = JsonUtils.objectToJson(bodyResult.getData());
            articleDetailVO = JsonUtils.jsonToPojo(detailJson, ArticleDetailVO.class);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("articleDetail", articleDetailVO);

        String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        InputStream inputStream = IOUtils.toInputStream(htmlContent, "UTF-8");
        ObjectId fileId = gridFSBucket.uploadFromStream(articleDetailVO.getId() + ".html", inputStream);
        return fileId.toString();
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private void doDownloadArticleHtmlByMQ(String articleId, String articleMongoId) {

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.download.do", articleId + "," + articleMongoId);

    }
}
