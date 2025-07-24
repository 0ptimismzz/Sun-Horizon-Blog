package com.sun.article.mapper;

import com.sun.my.mapper.MyMapper;
import com.sun.pojo.Article;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper extends MyMapper<Article> {
}