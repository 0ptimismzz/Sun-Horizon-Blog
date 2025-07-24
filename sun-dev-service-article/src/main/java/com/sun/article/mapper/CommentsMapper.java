package com.sun.article.mapper;

import com.sun.my.mapper.MyMapper;
import com.sun.pojo.Comments;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsMapper extends MyMapper<Comments> {
}