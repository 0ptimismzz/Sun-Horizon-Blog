package com.sun.user.service;

import com.sun.utils.PagedGridResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

public interface AppUserMngService {
    /*
    查询用户列表
     */
    public PagedGridResult queryAllUserList(String nickname, Integer status, Date startDate,
                                            Date endDate, Integer page, Integer pageSize);

    public void freezeUserOrNot(String userId, Integer doStatus);

}
