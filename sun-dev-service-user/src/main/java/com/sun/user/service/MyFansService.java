package com.sun.user.service;

import com.sun.enums.Sex;
import com.sun.pojo.vo.RegionRatioVO;
import com.sun.utils.PagedGridResult;

import java.util.List;

public interface MyFansService {
    /**
     * 查询当前用户是否关注作者
     */
    public boolean isMeFollowThisWriter(String writerId, String fanId);

    /**
     * 关注成为粉丝
     */
    public void follow(String writerId, String fanId);

    /**
     * 粉丝取消关注
     */
    public void unfollow(String writerId, String fanId);

    /*
    查询我的粉丝数
     */
    public PagedGridResult queryMyFansList(String writerId, Integer page, Integer pageSize);

    /*
    查询男女比例
    */
    public Integer queryFansCounts(String writerId, Sex sex);

    /*
    查询地域
     */
    public List<RegionRatioVO> queryRegionRatioCounts(String writerId);

}
