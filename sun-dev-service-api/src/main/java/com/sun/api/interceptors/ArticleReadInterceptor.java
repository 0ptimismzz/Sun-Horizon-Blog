package com.sun.api.interceptors;

import com.sun.exception.GraceException;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.utils.IPUtil;
import com.sun.utils.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

public class ArticleReadInterceptor extends BaseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String articleId = request.getParameter("articleId");
        String userIP = IPUtil.getRequestIp(request);
        // 设置针对当前用户的ip永久存在的key存入redis，表示该用户已经阅读过了
        boolean keyIsExist = redisOperator.keyIsExist(REDIS_ALREADY_READ + ":" + articleId + ":" + userIP);
        if (keyIsExist) {
            return false;
        }
        return true;
    }
}
