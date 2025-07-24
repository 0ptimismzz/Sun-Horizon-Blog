package com.sun.article.controller;

import com.sun.api.BaseController;
import com.sun.api.controller.article.ArticlePortalControllerApi;
import com.sun.api.controller.user.HelloControllerApi;
import com.sun.article.service.ArticlePortalService;
import com.sun.article.service.ArticleService;
import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.Article;
import com.sun.pojo.vo.AppUserVO;
import com.sun.pojo.vo.ArticleDetailVO;
import com.sun.pojo.vo.IndexArticleVO;
import com.sun.utils.FixJsonUrl;
import com.sun.utils.IPUtil;
import com.sun.utils.JsonUtils;
import com.sun.utils.PagedGridResult;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.beans.Encoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class ArticlePortalController extends BaseController implements ArticlePortalControllerApi {

    @Autowired
    private ArticlePortalService articlePortalService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public GraceJSONResult list(String keyword, Integer category, Integer page, Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = articlePortalService.queryIndexArticleList(keyword, category, page, pageSize);


        List<Article> articleList = (List<Article>)gridResult.getRows();
        Set<String> idSet = new HashSet<>();
        List<String> idList = new ArrayList<>();
        for (Article article : articleList) {
            idSet.add(article.getPublishUserId());
            //构建文章id的List
            idList.add(REDIS_ARTICLE_READ_COUNTS + ":" + article.getId());
        }

        // 发起redis的mget批量查询api
        List<String> readCountsRedisList = redisOperator.mget(idList);

        //发起远程服务，请求用户服务获得用户列表
        String userServerUrlExecute =
                "http://user.sun.com:8003/user/queryByIds?userIds=" + FixJsonUrl.encodeURI(JsonUtils.objectToJson(idSet));
        System.out.println("远程服务地址"+userServerUrlExecute);
        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(userServerUrlExecute, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        List<AppUserVO> publisherList = null;
        if (bodyResult.getStatus() == 200) {
            String userJson = JsonUtils.objectToJson(bodyResult.getData());
            publisherList = JsonUtils.jsonToList(userJson, AppUserVO.class);
            System.out.println("文章作者："+ publisherList.toString());
        }

        List<IndexArticleVO> indexArticleVOList = new ArrayList<>();
//        for (Article article : articleList) {
//            IndexArticleVO indexArticleVO = new IndexArticleVO();
//            BeanUtils.copyProperties(article, indexArticleVO);
//            // 获得发布者的基本信息
//            AppUserVO publisher = getUserIfPublisher(article.getId(), publisherList);
//            indexArticleVO.setPublisherVO(publisher);
//            int readCounts = getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + article.getId());
//            indexArticleVO.setReadCounts(readCounts);
//            indexArticleVOList.add(indexArticleVO);
//        }
        for (int i = 0; i < articleList.size(); i++) {
            IndexArticleVO indexArticleVO = new IndexArticleVO();
            Article article = articleList.get(i);
            BeanUtils.copyProperties(article, indexArticleVO);
            // 获得发布者的基本信息
            AppUserVO publisher = getUserIfPublisher(article.getId(), publisherList);
            indexArticleVO.setPublisherVO(publisher);

            String redisCountStr = readCountsRedisList.get(i);
            int readCount = 0;
            if (StringUtils.isNotBlank(redisCountStr)) {
                readCount = Integer.valueOf(redisCountStr);
            }
            indexArticleVO.setReadCounts(readCount);
            indexArticleVOList.add(indexArticleVO);
        }
        gridResult.setRows(indexArticleVOList);
        return GraceJSONResult.ok(gridResult);
    }

    @Override
    public GraceJSONResult hotList() {
        return GraceJSONResult.ok(articlePortalService.queryHotList());
    }

    private AppUserVO getUserIfPublisher(String publishUserId, List<AppUserVO> publisherList) {
        for (AppUserVO publisher : publisherList) {
            if (publisher.getId().equalsIgnoreCase(publishUserId)) {
                return publisher;
            }
        }
        return null;
    }

//    @Autowired
//    private DiscoveryClient discoveryClient;

    @Override
    public GraceJSONResult detail(String articleId) {
        String serviceId = "SERVICE-USER";
//        List<ServiceInstance> instanceList = discoveryClient.getInstances(serviceId);
//        ServiceInstance userService = instanceList.get(0);
        ArticleDetailVO articleDetailVO = articlePortalService.queryDetail(articleId);

        //发起远程服务，请求用户服务获得用户列表
//        String userServerUrlExecute =
//                "http://localhost:" + userService.getPort() + "/user/queryById?userId=" + articleDetailVO.getPublishUserId();
//        String userServerUrlExecute =
//                "http://" + userService.getHost() + ":" + userService.getPort() + "/user/queryById?userId=" + articleDetailVO.getPublishUserId();
        String userServerUrlExecute =
                "http://user.sun.com:8003/user/queryById?userId=" + articleDetailVO.getPublishUserId();
        System.out.println("远程服务地址"+userServerUrlExecute);
//        System.out.println(userService.getHost());
        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(userServerUrlExecute, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        String userJson = "";
        AppUserVO userVO = null;
        if (bodyResult.getStatus() == 200) {
            userJson = JsonUtils.objectToJson(bodyResult.getData());
            userVO = JsonUtils.jsonToPojo(userJson, AppUserVO.class);
        }
//        System.out.println(userVO.toString());
        if (userJson != null && !userJson.equals("")) {
            articleDetailVO.setPublishUserName(userVO.getNickname());
        }

        articleDetailVO.setReadCounts(getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId));

        return GraceJSONResult.ok(articleDetailVO);
    }

    @Override
    public GraceJSONResult readArticle(String articleId, HttpServletRequest request) {

        String userIP = IPUtil.getRequestIp(request);
        // 设置针对当前用户的ip永久存在的key存入redis，表示该用户已经阅读过了
        redisOperator.setnx(REDIS_ALREADY_READ + ":" + articleId + ":" + userIP, userIP);

        redisOperator.increment(REDIS_ARTICLE_READ_COUNTS + ":" + articleId,1);
        return GraceJSONResult.ok();
    }

    @Override
    public Integer readCounts(String articleId, HttpServletRequest request) {
        return getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS + ":" + articleId);
    }
}


