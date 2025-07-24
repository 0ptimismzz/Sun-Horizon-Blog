package com.sun.user.controller;

import com.sun.api.BaseController;
import com.sun.api.controller.user.PassportControllerApi;
import com.sun.enums.UserStatus;
import com.sun.grace.result.GraceJSONResult;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.AppUser;
import com.sun.pojo.bo.RegistLoginBO;
import com.sun.user.service.UserService;
import com.sun.utils.IPUtil;
import com.sun.utils.JsonUtils;
import com.sun.utils.extend.RandomResource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.UUID;

@RestController
public class PassportController extends BaseController implements PassportControllerApi {

    final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private RandomResource randomResource;

    @Autowired
    private UserService userService;

    @Override
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {
        //获得用户ip
        String userIp = IPUtil.getRequestIp(request);
        //根据用户的ip进行限制，限制60s内只能获得一次验证码
        redisOperator.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);
        String code = randomResource.randomCode();
        logger.warn(code);
        redisOperator.set(MOBILE_SMSCODE + ":" + mobile, code,30*60);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult doLogin(RegistLoginBO registLoginBO, BindingResult bindingResult,
                                   HttpServletRequest request, HttpServletResponse response) {
        //判断BindingResult中是否保存了错误的验证信息，如果有需要返回
        if(bindingResult.hasErrors()) {
            Map<String,String> map = getErrors(bindingResult);
            return GraceJSONResult.errorMap(map);
        }

        String mobile = registLoginBO.getMobile();
        String smsCode = registLoginBO.getSmsCode();

        //校验验证码是否匹配
        String redisSMSCode = redisOperator.get(MOBILE_SMSCODE + ":" + mobile);
        if(StringUtils.isBlank(redisSMSCode) || !redisSMSCode.equals(smsCode)) {
            return  GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        //查询数据库 该用户是否注册
        AppUser user = userService.queryMobileIsExist(mobile);
        if(user != null && user.getActiveStatus() == UserStatus.FROZEN.type){
            //如果用户不为空，并且状态为冻结，则直接抛出异常，禁止登录
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        } else if (user == null) {
            //如果用户没有注册过，则为null，需要信息注册入库
            user = userService.createUser(mobile);
        }

        //保存用户分布式会话的操作
        int activeStatus = user.getActiveStatus();
        if(activeStatus != UserStatus.FROZEN.type){
            //保存token到redis
            String uToken = UUID.randomUUID().toString();
            redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(),uToken);
            redisOperator.set(REDIS_USER_INFO + ":" + user.getId(), JsonUtils.objectToJson(user));
            //保存用户id和token到cookie中
            setCookie(request, response, "utoken",uToken, COOKIE_MONTH);
            setCookie(request, response, "uid", user.getId(), COOKIE_MONTH);
        }

        //用户登录或注册成功后，需要删除redis中的短信验证码，验证码只能用一次。用过后需要删除
        redisOperator.del(MOBILE_SMSCODE + ":" + mobile);

        //返回用户状态
        return GraceJSONResult.ok(activeStatus);
    }

    @Override
    public GraceJSONResult logout(String userId, HttpServletRequest request, HttpServletResponse response) {
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        setCookie(request, response, "utoken","", COOKIE_DELETE);
        setCookie(request, response, "uid","", COOKIE_DELETE);
        return GraceJSONResult.ok();
    }
}
