package com.sun.api.interceptors;

import com.sun.exception.GraceException;
import com.sun.grace.result.GraceJSONResult;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.utils.IPUtil;
import com.sun.utils.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static com.sun.api.BaseController.MOBILE_SMSCODE;

public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redisOperator;

    /*
    拦截请求，访问controller之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userIp = IPUtil.getRequestIp(request);

        boolean keyIsExist = redisOperator.keyIsExist(MOBILE_SMSCODE + ":" + userIp);

        if (keyIsExist) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
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
