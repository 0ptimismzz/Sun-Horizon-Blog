package com.sun.api.interceptors;

import com.sun.enums.UserStatus;
import com.sun.exception.GraceException;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.AppUser;
import com.sun.utils.JsonUtils;
import com.sun.utils.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static com.sun.api.BaseController.REDIS_USER_INFO;
import static com.sun.api.BaseController.REDIS_USER_TOKEN;
/*
用户激活状态检查拦截器
 */
public class UserActiveInterceptor extends BaseInterceptor implements HandlerInterceptor {


    /*
    拦截请求，访问controller之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userId = request.getHeader("headerUserId");

        String userJson = redisOperator.get(REDIS_USER_INFO + ":" + userId);
        AppUser user = null;
        if(StringUtils.isNotBlank(userJson)) {
            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
        }else {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }

        if (user.getActiveStatus() == null || user.getActiveStatus() != UserStatus.ACTIVE.type) {
            GraceException.display(ResponseStatusEnum.USER_INACTIVE_ERROR);
            return false;
        }
        /*
        false：请求被拦截
        true：请求通过验证，放行
         */
        return true;
    }

    /*
    请求访问到controller之后，渲染视图之前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /*
    请求访问到controller之后，渲染视图之后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
