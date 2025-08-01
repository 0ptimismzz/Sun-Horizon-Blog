package com.sun.admin.service;

import com.sun.pojo.AdminUser;
import com.sun.pojo.bo.NewAdminBO;
import com.sun.utils.PagedGridResult;

public interface AdminUserService {

    /*
    获得管理员的用户信息
     */
    public AdminUser queryAdminUserByUsername(String username);

    /*
    新增管理员
     */
    public void createAdminUser(NewAdminBO newAdminBO);

    /*
    分页查询admin列表
     */
    public PagedGridResult queryAdminList(Integer page, Integer pageSize);

}
