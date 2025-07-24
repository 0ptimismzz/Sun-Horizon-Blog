package com.sun.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.sun.api.service.BaseService;
import com.sun.enums.UserStatus;
import com.sun.pojo.AppUser;
import com.sun.user.mapper.AppUserMapper;
import com.sun.user.service.AppUserMngService;
import com.sun.utils.PagedGridResult;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class AppUserMngServiceImpl extends BaseService implements AppUserMngService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Override
    public PagedGridResult queryAllUserList(String nickname, Integer status, Date startDate,
                                            Date endDate, Integer page, Integer pageSize) {
        Example example = new Example(AppUser.class);
        example.orderBy("createdTime").desc();
        Example.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(nickname)) {
            criteria.andLike("nickname", "%" + nickname + "%");
        }

        if (UserStatus.isUserStatusValid(status)) {
            criteria.andEqualTo("activeStatus", status);
        }

        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo("createdTime", startDate);
        }

        if (endDate != null) {
            criteria.andLessThanOrEqualTo("createdTime", endDate);
        }

        PageHelper.startPage(page, pageSize);
        List<AppUser> list = appUserMapper.selectByExample(example);

        return setterPagedGrid(list, page);
    }

    @Transactional
    @Override
    public void freezeUserOrNot(String userId, Integer doStatus) {
        AppUser appUser = new AppUser();
        appUser.setId(userId);
        appUser.setActiveStatus(doStatus);
        appUserMapper.updateByPrimaryKeySelective(appUser);
    }
}
