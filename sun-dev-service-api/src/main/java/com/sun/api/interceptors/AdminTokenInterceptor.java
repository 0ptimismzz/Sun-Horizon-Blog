package com.sun.api.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static com.sun.api.BaseController.REDIS_ADMIN_TOKEN;
import static com.sun.api.BaseController.REDIS_USER_TOKEN;

public class AdminTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {

    /*
    拦截请求，访问controller之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String adminUserId = request.getHeader("adminUserId");
        String adminUserToken = request.getHeader("adminUserToken");

        System.out.println("=====================================================================");
        System.out.println("AdminTokenInterceptor - adminUserId = " + adminUserId);
        System.out.println("AdminTokenInterceptor - adminUserToken = " + adminUserToken);
        System.out.println("=====================================================================");

        //判断是否放行
        boolean run = verifyAdminIdToken(adminUserId,adminUserToken,REDIS_ADMIN_TOKEN);

        return run;
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
