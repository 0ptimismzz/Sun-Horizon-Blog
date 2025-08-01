package com.sun.admin.service;

import com.sun.pojo.mo.FriendLinkMO;

import java.util.List;

public interface FriendLinkService {

    /**
     * 新增或者更新友情链接
     * @param friendLinkMO
     */
    public void saveOrUpdateFriendLink(FriendLinkMO friendLinkMO);

    /**
     * 查询友情链接
     */
    public List<FriendLinkMO> queryAllFriendLinkList();

    /**
     * 删除友情链接
     * @param linkId
     */
    public void delete(String linkId);

    /*
    首页查询友情链接
     */
    public List<FriendLinkMO> queryPortalAllFriendLinkList();

}
