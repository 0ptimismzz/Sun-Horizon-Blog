package com.sun.user.service;

import com.sun.pojo.AppUser;
import com.sun.pojo.bo.UpdateUserInfoBO;

public interface UserService {

    /*
    判断用户是否存在，如果存在返回user信息
     */
    public AppUser queryMobileIsExist(String mobile);

    /*
    创建用户，新增用户到数据库
     */
    public AppUser createUser(String mobile);

    /*
    根据id查询用户信息
     */
    public AppUser getUser(String userId);

    /*
    用户修改信息，完善资料，并且激活
     */
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO);
}
