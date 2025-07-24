package com.sun.user.controller;

import com.sun.api.BaseController;
import com.sun.api.config.DateConverterConfig;
import com.sun.api.controller.user.AppUserMngControllerApi;
import com.sun.enums.UserStatus;
import com.sun.grace.result.GraceJSONResult;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.user.service.AppUserMngService;
import com.sun.user.service.UserService;
import com.sun.utils.PagedGridResult;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class AppUserMngController extends BaseController implements AppUserMngControllerApi {

    @Autowired
    private AppUserMngService appUserMngService;

    @Autowired
    private UserService userService;

    @Autowired
    private DateConverterConfig dateConverterConfig;

    @Override
    public GraceJSONResult queryAll(String nickname, Integer status, String startDate,
                                    String endDate, Integer page, Integer pageSize) {

//        System.out.println(startDate);
//        System.out.println(endDate);

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult result = appUserMngService.queryAllUserList(nickname, status, dateConverterConfig.convert(startDate),
                                                            dateConverterConfig.convert(endDate), page, pageSize);
        return GraceJSONResult.ok(result);
    }

    @Override
    public GraceJSONResult userDetail(String userId) {
        return GraceJSONResult.ok(userService.getUser(userId));
    }

    @Override
    public GraceJSONResult freezeUserOrNot(String userId, Integer doStatus) {
        if (!UserStatus.isUserStatusValid(doStatus)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_STATUS_ERROR);
        }

        appUserMngService.freezeUserOrNot(userId, doStatus);

        // 刷新用户状态
        //1. 删除用户会话，从而保证用户需要重新登录之后再来刷新他的会话状态
        //2. 查询最新用户的信息，重新放入redis中
        redisOperator.del(REDIS_USER_INFO + ":" + userId);

        return GraceJSONResult.ok();
    }
}
