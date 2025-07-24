package com.sun.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.sun.api.service.BaseService;
import com.sun.enums.Sex;
import com.sun.pojo.AppUser;
import com.sun.pojo.vo.RegionRatioVO;
import com.sun.user.service.MyFansService;
import com.sun.pojo.Fans;
import com.sun.user.mapper.FansMapper;
import com.sun.user.service.UserService;
import com.sun.utils.PagedGridResult;
import com.sun.utils.RedisOperator;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyFansServiceImpl extends BaseService implements MyFansService {

    @Autowired
    private FansMapper fansMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private Sid sid;
    @Autowired
    private RedisOperator redisOperator;

    @Override
    public boolean isMeFollowThisWriter(String writerId, String fanId) {
        Fans fan = new Fans();
        fan.setFanId(fanId);
        fan.setWriterId(writerId);

        int i = fansMapper.selectCount(fan);

        return i > 0 ? true : false;
    }

    @Transactional
    @Override
    public void follow(String writerId, String fanId) {
        AppUser fanInfo = userService.getUser(fanId);

        String fanPkId = sid.nextShort();

        Fans fan = new Fans();
        fan.setId(fanPkId);
        fan.setFanId(fanId);
        fan.setWriterId(writerId);

        fan.setFace(fanInfo.getFace());
        fan.setFanNickname(fanInfo.getNickname());
        fan.setSex(fanInfo.getSex());
        fan.setProvince(fanInfo.getProvince());

        fansMapper.insert(fan);

        // redis 作家粉丝数增加
        redisOperator.increment(REDIS_WRITER_FANS_COUNTS + ":" + writerId,1);
        //redis 当前用户 我的关注数增加
        redisOperator.increment(REDIS_MY_FOLLOW_COUNTS + ":" + fanId,1);
    }

    @Transactional
    @Override
    public void unfollow(String writerId, String fanId) {
        Fans fan = new Fans();
        fan.setFanId(fanId);
        fan.setWriterId(writerId);

        fansMapper.delete(fan);

        redisOperator.decrement(REDIS_WRITER_FANS_COUNTS + ":" + writerId,1);
        redisOperator.decrement(REDIS_MY_FOLLOW_COUNTS + ":" + fanId,1);
    }

    @Override
    public PagedGridResult queryMyFansList(String writerId, Integer page, Integer pageSize) {
        Fans fan = new Fans();
        fan.setWriterId(writerId);
        PageHelper.startPage(page, pageSize);
        List<Fans> fans = fansMapper.select(fan);
        return setterPagedGrid(fans, page);
    }

    @Override
    public Integer queryFansCounts(String writerId, Sex sex) {
        Fans fan = new Fans();
        fan.setWriterId(writerId);
        fan.setSex(sex.type);

        int count = fansMapper.selectCount(fan);

        return count;
    }

    public static final String[] regions = {"北京", "天津", "上海", "重庆",
            "河北", "山西", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "台湾",
            "内蒙古", "广西", "西藏", "宁夏", "新疆",
            "香港", "澳门"};

    @Override
    public List<RegionRatioVO> queryRegionRatioCounts(String writerId) {
        Fans fan = new Fans();
        fan.setWriterId(writerId);
        List<RegionRatioVO> list = new ArrayList<RegionRatioVO>();
        for (String region : regions) {
            fan.setProvince(region);
            Integer count = fansMapper.selectCount(fan);

            RegionRatioVO vo = new RegionRatioVO();
            vo.setName(region);
            vo.setValue(count);
            list.add(vo);
        }

        return list;
    }
}


