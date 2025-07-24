package com.sun.admin.mapper;

import com.sun.my.mapper.MyMapper;
import com.sun.pojo.AdminUser;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserMapper extends MyMapper<AdminUser> {
}