package com.sun.user.mapper;

import com.sun.my.mapper.MyMapper;
import com.sun.pojo.AppUser;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserMapper extends MyMapper<AppUser> {
}