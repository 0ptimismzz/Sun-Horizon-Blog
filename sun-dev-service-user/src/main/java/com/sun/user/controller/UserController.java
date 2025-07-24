package com.sun.user.controller;

import com.sun.api.BaseController;
import com.sun.api.controller.user.UserControllerApi;
import com.sun.grace.result.GraceJSONResult;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.AppUser;
import com.sun.pojo.bo.UpdateUserInfoBO;
import com.sun.pojo.vo.AppUserVO;
import com.sun.pojo.vo.UserAccountInfoVO;
import com.sun.utils.FixJsonUrl;
import com.sun.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.sun.user.service.UserService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sun.api.service.BaseService.REDIS_MY_FOLLOW_COUNTS;
import static com.sun.api.service.BaseService.REDIS_WRITER_FANS_COUNTS;

@RestController
public class UserController extends BaseController implements UserControllerApi {

    @Autowired
    private UserService userService;

    @Override
    public GraceJSONResult getUserInfo(String userId) {
        //判断参数不能为空
        if(StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        //根据userId查询用户信息
        AppUser user = getUser(userId);

        //返回用户信息
        AppUserVO userVO = new AppUserVO();
        BeanUtils.copyProperties(user, userVO);

        // 查询redis中的用户的关注数和粉丝数
        userVO.setMyFansCounts(getCountsFromRedis(REDIS_WRITER_FANS_COUNTS + ":" + userId));
        userVO.setMyFollowCounts(getCountsFromRedis(REDIS_MY_FOLLOW_COUNTS + ":" + userId));

        return GraceJSONResult.ok(userVO);

    }

    @Override
    public GraceJSONResult getAccountInfo(String userId) {
        //判断参数不能为空
        if(StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        //根据userId查询用户信息
        AppUser user = getUser(userId);

        //返回用户信息
        UserAccountInfoVO userAccountInfoVO = new UserAccountInfoVO();
        BeanUtils.copyProperties(user, userAccountInfoVO);
        return GraceJSONResult.ok(userAccountInfoVO);
    }

    @Override
    public GraceJSONResult updateUserInfo(UpdateUserInfoBO updateUserInfoBO, BindingResult bindingResult) {

        //校验BO
        if(bindingResult.hasErrors()) {
            Map<String,String> map = getErrors(bindingResult);
            return GraceJSONResult.errorMap(map);
        }

        //执行更新操作
        userService.updateUserInfo(updateUserInfoBO);

        return GraceJSONResult.ok();
    }

    private AppUser getUser(String userId) {
        AppUser user = null;
        //查询redis是否包含用户信息，如果包含，则查询后直接返回，不查询数据库
        String userJson = redisOperator.get(REDIS_USER_INFO + ":" + userId);
        if(StringUtils.isNotBlank(userJson)) {
            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
        }else {
            user = userService.getUser(userId);
            //由于用户信息不怎么会变动，可以依靠redis，直接把查询后的数据放到redis中
            redisOperator.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));
        }
        return user;
    }

    @Override
    public GraceJSONResult queryByIds(String userIds) {
        if(StringUtils.isBlank(userIds)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        List<AppUserVO> publisherList = new ArrayList<>();

//        System.out.println(userIds);
//        System.out.println(FixJsonUrl.recodeURI(userIds));
        List<String> userIdList = JsonUtils.jsonToList(FixJsonUrl.recodeURI(userIds),String.class);

        for (String userId : userIdList) {
            AppUserVO userVO = getBasicUserInfo(userId);

            publisherList.add(userVO);
        }
        return GraceJSONResult.ok(publisherList);
    }

    @Override
    public GraceJSONResult queryById(String userId) {
        if(StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        AppUserVO userVO = getBasicUserInfo(userId);

        return GraceJSONResult.ok(userVO);
    }

    private AppUserVO getBasicUserInfo(String userId) {
        AppUser user = getUser(userId);
        //返回用户信息
        AppUserVO userVO = new AppUserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
