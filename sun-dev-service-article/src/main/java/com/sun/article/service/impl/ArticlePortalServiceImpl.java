package com.sun.article.service.impl;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.github.pagehelper.PageHelper;
import com.sun.api.service.BaseService;
import com.sun.article.mapper.ArticleMapper;
import com.sun.article.mapper.ArticleMapperCustom;
import com.sun.article.service.ArticlePortalService;
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
import com.sun.pojo.vo.ArticleDetailVO;
import com.sun.utils.BaiDuArticleUtils;
import com.sun.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
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
public class ArticlePortalServiceImpl extends BaseService implements ArticlePortalService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PagedGridResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        example.orderBy("publishTime").desc();
        Example.Criteria criteria = example.createCriteria();
        /**
         * 查询首页文章自带的隐性查询条件
         * 1. isAppoint定时发布
         * 2. isDelete是否删除
         * 3. articleStatus审核通过后才能展示
         */
        criteria.andEqualTo("isAppoint", YesOrNo.NO.type);
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);

        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("title", "%" + keyword + "%");
        }

        if (category != null) {
            criteria.andEqualTo("categoryId", category);
        }

        PageHelper.startPage(page, pageSize);

        List<Article> list = articleMapper.selectByExample(example);
//        for (Article article : list) {
//            System.out.println(article.toString());
//        }

        return setterPagedGrid(list, page);
    }

    @Override
    public List<Article> queryHotList() {
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = setDefaultArticleExample(articleExample);
        PageHelper.startPage(1, 5);
        List<Article> list = articleMapper.selectByExample(articleExample);
        return list;
    }

    private Example.Criteria setDefaultArticleExample(Example example) {

        example.orderBy("publishTime").desc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        criteria.andEqualTo("isAppoint", YesOrNo.NO.type);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);
        return criteria;
    }

    @Override
    public ArticleDetailVO queryDetail(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(YesOrNo.NO.type);
        article.setIsDelete(YesOrNo.NO.type);
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.type);

        Article result = articleMapper.selectOne(article);

        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        System.out.println(result.toString());
        BeanUtils.copyProperties(result, articleDetailVO);
        articleDetailVO.setCover(result.getArticleCover());
        System.out.println(articleDetailVO.toString());
        return articleDetailVO;
    }
}


