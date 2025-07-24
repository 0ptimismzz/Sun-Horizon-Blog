package com.sun.user.service.impl;

import com.sun.enums.Sex;
import com.sun.enums.UserStatus;
import com.sun.exception.GraceException;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.AppUser;
import com.sun.pojo.bo.UpdateUserInfoBO;
import com.sun.user.mapper.AppUserMapper;
import com.sun.user.service.UserService;
import com.sun.utils.DateUtil;
import com.sun.utils.DesensitizationUtil;
import com.sun.utils.JsonUtils;
import com.sun.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

import static com.sun.api.BaseController.REDIS_USER_INFO;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public AppUserMapper appUserMapper;

    @Autowired
    public Sid sid;

    @Autowired
    public RedisOperator redisOperator;

    @Override
    public AppUser queryMobileIsExist(String mobile) {
        Example userExample = new Example(AppUser.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("mobile", mobile);
        AppUser user = appUserMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional
    @Override
    public AppUser createUser(String mobile) {
        /*
        业务激增 分库分表 id保证全局唯一
         */
        String userId = sid.nextShort();
        AppUser user = new AppUser();
        user.setId(userId);
        user.setMobile(mobile);
        user.setNickname("用户:" + DesensitizationUtil.commonDisplay(mobile));
        user.setFace("face");
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);
        user.setActiveStatus(UserStatus.INACTIVE.type);
        user.setTotalIncome(0);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        appUserMapper.insert(user);

        return user;
    }

    @Override
    public AppUser getUser(String userId) {
        return appUserMapper.selectByPrimaryKey(userId);
    }

//    @Override
//    public AppUser getUser(String userId) {
//        AppUser user = null;
//        //查询redis是否包含用户信息，如果包含，则查询后直接返回，不查询数据库
//        String userJson = redisOperator.get(REDIS_USER_INFO + ":" + userId);
//        if(StringUtils.isNotBlank(userJson)) {
//            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
//        }else {
//            user = appUserMapper.selectByPrimaryKey(userId);
//            //由于用户信息不怎么会变动，可以依靠redis，直接把查询后的数据放到redis中
//            redisOperator.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));
//        }
//        return user;
//    }


    @Override
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {
        String userId = updateUserInfoBO.getId();
        //保证双写一致，先删除redis中的数据，后更新数据库
        redisOperator.del(REDIS_USER_INFO + ":" + userId);

        AppUser userInfo = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBO, userInfo);
        userInfo.setUpdatedTime(new Date());
        userInfo.setActiveStatus(UserStatus.ACTIVE.type);
        int res = appUserMapper.updateByPrimaryKeySelective(userInfo);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }
        //再次查询用户的最新信息，放入redis中
        AppUser user = getUser(userId);
        redisOperator.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));

        //缓存双删策略
        try {
            Thread.sleep(100);
            redisOperator.del(REDIS_USER_INFO + ":" + userId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
