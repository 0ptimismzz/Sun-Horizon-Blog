package com.sun.article.service;

import com.sun.pojo.Article;
import com.sun.pojo.Category;
import com.sun.pojo.bo.NewArticleBO;
import com.sun.pojo.vo.ArticleDetailVO;
import com.sun.utils.PagedGridResult;

import java.util.Date;
import java.util.List;

public interface ArticlePortalService {

    /**
     * 首页查询文章列表
     */
    public PagedGridResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize);

    /*
    首页查询热文列表
     */
    public List<Article> queryHotList();

    /*
    查询文章详情
    */
    public ArticleDetailVO queryDetail(String articleId);

}
