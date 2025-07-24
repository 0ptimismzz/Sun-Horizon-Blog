package com.sun.article.service.impl;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.github.pagehelper.PageHelper;
import com.sun.api.controller.article.ArticleControllerApi;
import com.sun.api.service.BaseService;
import com.sun.article.mapper.ArticleMapper;
import com.sun.article.mapper.ArticleMapperCustom;
import com.sun.article.service.ArticleService;
import com.sun.enums.ArticleAppointType;
import com.sun.enums.ArticleReviewLevel;
import com.sun.enums.ArticleReviewStatus;
import com.sun.enums.YesOrNo;
import com.sun.exception.GraceException;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.Article;
import com.sun.pojo.Category;
import com.sun.pojo.bo.NewArticleBO;
import com.sun.utils.BaiDuArticleUtils;
import com.sun.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {

    @Autowired
    private Sid sid;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleMapperCustom articleMapperCustom;

    @Autowired
    private AipContentCensor aipContentCensor;

    @Override
    public void creatArticle(NewArticleBO newArticleBO, Category category) {
        String articleId = sid.nextShort();
        Article article = new Article();
        BeanUtils.copyProperties(newArticleBO, article);
        article.setId(articleId);
        article.setCategoryId(category.getId());
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
        article.setCommentCounts(0);
        article.setReadCounts(0);
        article.setIsDelete(YesOrNo.NO.type);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        if (article.getIsAppoint() == ArticleAppointType.TIMING.type){
            article.setPublishTime(newArticleBO.getPublishTime());
        } else if (article.getIsAppoint() == ArticleAppointType.IMMEDIATELY.type) {
            article.setPublishTime(new Date());
        }

        int insert = articleMapper.insert(article);
        if (insert != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }

        // 通过百度智能ai自动审核
        JSONObject jsonObject = BaiDuArticleUtils.detectionArticle(aipContentCensor, newArticleBO.getContent());
        System.out.println(jsonObject.get("conclusionType"));
        if (jsonObject.get("conclusionType").equals(ArticleReviewLevel.PASS.type)) {
            // 修改文章，状态标记通过
            this.updateArticleStatus(articleId, ArticleReviewStatus.SUCCESS.type);
        } else if (jsonObject.get("conclusionType").equals(ArticleReviewLevel.REVIEW.type) ||
                jsonObject.get("conclusionType").equals(ArticleReviewLevel.Error.type)) {
            // 修改文章，状态标记需要人工审核
            this.updateArticleStatus(articleId, ArticleReviewStatus.WAITING_MANUAL.type);
        } else if (jsonObject.get("conclusionType").equals(ArticleReviewLevel.BLOCK.type)) {
            // 修改文章，状态标记未通过
            this.updateArticleStatus(articleId, ArticleReviewStatus.FAILED.type);
        }
    }

    @Transactional
    @Override
    public void updateAppointToPublish() {
        articleMapperCustom.updateAppointToPublish();
    }

    @Override
    public PagedGridResult queryMyArticleList(String userId, String keyword, Integer status,
                                              Date startDate, Date endDate, Integer page, Integer pageSize) {

        Example example = new Example(Article.class);
        example.orderBy("createTime").desc();
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("publishUserId", userId);

        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("title", "%" + keyword + "%");
        }

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }

        if (status != null && status ==12) {
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }

        criteria.andEqualTo("isDelete", YesOrNo.NO.type);

        if (startDate != null) {
            criteria.andGreaterThan("publishTime", startDate);
        }

        if (endDate != null) {
            criteria.andLessThanOrEqualTo("publishTime", endDate);
        }

        PageHelper.startPage(page, pageSize);
        List<Article> articles = articleMapper.selectByExample(example);

        return setterPagedGrid(articles, page);
    }

    @Override
    public PagedGridResult queryAllArticleListAdmin(Integer status, Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        example.orderBy("createTime").desc();

        Example.Criteria criteria = example.createCriteria();

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }

        // 审核中是机审和人审两个阶段
        if (status != null && status ==12) {
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);

        PageHelper.startPage(page, pageSize);

        List<Article> articles = articleMapper.selectByExample(example);

        return setterPagedGrid(articles, page);
    }

    @Transactional
    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("id", articleId);

        Article pendingArticle = new Article();
        pendingArticle.setArticleStatus(pendingStatus);

        int res = articleMapper.updateByExampleSelective(pendingArticle, example);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
    }

    @Transactional
    @Override
    public void updateArticleToGidFS(String articleId, String articleMongoId) {
        Article article = new Article();
        article.setId(articleId);
        article.setMongoFileId(articleMongoId);
        articleMapper.updateByPrimaryKeySelective(article);
    }
}



//{
//        "conclusion":"不合规",
//        "log_id":17447772389918934,
//        "phoneRisk":{},
//        "data":[{
//            "msg":"存在低俗辱骂不合规",
//            "conclusion":"不合规",
//            "hits":[{
//                "probability":1,
//                "datasetName":"百度默认文本反作弊库",
//                "words":[],
//                "modelHitPositions":[[3,10,1]]
//            }],
//                "subType":5,
//                "conclusionType":2,
//                "type":12
//        }],
//        "isHitMd5":false,
//        "conclusionType":2
//}


