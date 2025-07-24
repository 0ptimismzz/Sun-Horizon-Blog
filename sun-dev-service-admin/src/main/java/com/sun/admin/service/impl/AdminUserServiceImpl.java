package com.sun.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.admin.mapper.AdminUserMapper;
import com.sun.admin.service.AdminUserService;
import com.sun.api.service.BaseService;
import com.sun.exception.GraceException;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.AdminUser;
import com.sun.pojo.bo.NewAdminBO;
import com.sun.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class AdminUserServiceImpl extends BaseService implements AdminUserService {

    @Autowired
    public AdminUserMapper adminUserMapper;

    @Autowired
    private Sid sid;

    @Override
    public AdminUser queryAdminUserByUsername(String username) {

        Example adminExample = new Example(AdminUser.class);
        Example.Criteria criteria = adminExample.createCriteria();
        criteria.andEqualTo("username", username);

        AdminUser admin = adminUserMapper.selectOneByExample(adminExample);
        return admin;
    }

    @Transactional
    @Override
    public void createAdminUser(NewAdminBO newAdminBO) {
        String adminId = sid.nextShort();

        AdminUser adminUser = new AdminUser();
        adminUser.setId(adminId);
        adminUser.setUsername(newAdminBO.getUsername());
        adminUser.setAdminName(newAdminBO.getAdminName());
        adminUser.setPassword(BCrypt.hashpw(newAdminBO.getPassword(), BCrypt.gensalt()));
        // 如果人脸上传以后 则有faceId 入库
        if (StringUtils.isNotBlank(newAdminBO.getFaceId())) {
            adminUser.setFaceId(newAdminBO.getFaceId());
        }
        adminUser.setCreatedTime(new Date());
        adminUser.setUpdatedTime(new Date());
        int res = adminUserMapper.insert(adminUser);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.ADMIN_CREATE_ERROR);
        }
    }

    @Override
    public PagedGridResult queryAdminList(Integer page, Integer pageSize) {
        Example adminExample = new Example(AdminUser.class);
        adminExample.orderBy("createdTime").desc();

        PageHelper.startPage(page, pageSize);
        List<AdminUser> adminUserList = adminUserMapper.selectByExample(adminExample);

        return setterPagedGrid(adminUserList,page);
    }

//    private PagedGridResult setterPagedGrid(List<?> adminUserList,
//                                            Integer page) {
//        PageInfo<?> pageList = new PageInfo<>(adminUserList);
//        PagedGridResult gridResult = new PagedGridResult();
//        gridResult.setRows(adminUserList);
//        gridResult.setPage(page);
//        gridResult.setRecords(pageList.getPages());
//        gridResult.setTotal(pageList.getTotal());
//
//        return gridResult;
//    }
}
