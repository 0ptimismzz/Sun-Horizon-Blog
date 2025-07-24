package com.sun.article.service.impl;

import com.github.pagehelper.PageHelper;
import com.sun.api.service.BaseService;
import com.sun.article.mapper.ArticleMapper;
import com.sun.article.mapper.CommentsMapper;
import com.sun.article.mapper.CommentsMapperCustom;
import com.sun.article.service.ArticlePortalService;
import com.sun.article.service.CommentPortalService;
import com.sun.enums.ArticleReviewStatus;
import com.sun.enums.YesOrNo;
import com.sun.pojo.Article;
import com.sun.pojo.Comments;
import com.sun.pojo.vo.ArticleDetailVO;
import com.sun.pojo.vo.CommentsVO;
import com.sun.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentPortalServiceImpl extends BaseService implements CommentPortalService {

    @Autowired
    private Sid sid;

    @Autowired
    private ArticlePortalService articlePortalService;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Transactional
    @Override
    public void creatComment(String articleId, String fatherCommentId,
                                                 String content, String userId, String nickName,String face) {

        String commentId = sid.nextShort();
        ArticleDetailVO article = articlePortalService.queryDetail(articleId);
        Comments comments = new Comments();
        comments.setId(commentId);
        comments.setWriterId(article.getPublishUserId());
        comments.setArticleTitle(article.getTitle());
        comments.setArticleCover(article.getCover());
        comments.setArticleId(articleId);

        comments.setFatherId(fatherCommentId);
        comments.setCommentUserId(userId);
        comments.setCommentUserNickname(nickName);
        comments.setCommentUserFace(face);

        comments.setContent(content);
        comments.setCreateTime(new Date());

        commentsMapper.insert(comments);

        // 评论数累加
        redis.increment(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId,1);
    }

    @Override
    public PagedGridResult queryArticleComments(String articleId, Integer page, Integer pageSize) {
        Map<String,Object> map = new HashMap<>();

        map.put("articleId", articleId);

        PageHelper.startPage(page,pageSize);
        List<CommentsVO> list = commentsMapperCustom.queryArticleCommentList(map);

        return setterPagedGrid(list,page);
    }
}


