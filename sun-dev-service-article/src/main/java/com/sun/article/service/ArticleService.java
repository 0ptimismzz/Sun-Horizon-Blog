package com.sun.article.service;

import com.sun.pojo.Category;
import com.sun.pojo.bo.NewArticleBO;
import com.sun.utils.PagedGridResult;

import java.util.Date;

public interface ArticleService {
    /**
     * 发布文章
     */
    public void creatArticle(NewArticleBO newArticleBO, Category category);

    /**
     * 更新定时发布为即时发布
     */
    public void updateAppointToPublish();

    /**
     * 用户中心查询我的文章列表
     */
    public PagedGridResult queryMyArticleList(String userId, String keyword, Integer status,
                                              Date startDate, Date endDate, Integer page, Integer pageSize);

    /**
     * 管理员查询文章列表
     */
    public PagedGridResult queryAllArticleListAdmin(Integer status, Integer page, Integer pageSize);

    /**
     * 更改文章状态
     */
    public void updateArticleStatus(String articleId, Integer pendingStatus);

    /**
     * 关联文章和gridFs的html文件id
     */
    public void updateArticleToGidFS(String articleId, String articleMongoId);
}
