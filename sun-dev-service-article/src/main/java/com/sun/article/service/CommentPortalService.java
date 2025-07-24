package com.sun.article.service;

import com.sun.pojo.Article;
import com.sun.pojo.vo.ArticleDetailVO;
import com.sun.utils.PagedGridResult;

import java.util.List;

public interface CommentPortalService {

    /**
     * 发表评论
     */
    public void creatComment(String articleId, String fatherCommentId, String content, String userId, String nickName,String face);


    /*
    查询评论列表
     */
    public PagedGridResult queryArticleComments(String articleId, Integer page, Integer pageSize);
}
