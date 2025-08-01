package com.sun.user.controller;

import com.sun.api.BaseController;
import com.sun.api.controller.user.MyFansControllerApi;
import com.sun.enums.Sex;
import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.vo.FansCountsVO;
import com.sun.user.service.MyFansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyFansController extends BaseController implements MyFansControllerApi {

    @Autowired
    private MyFansService myFansService;

    @Override
    public GraceJSONResult isMeFollowThisWriter(String writerId, String fanId) {
        boolean res = myFansService.isMeFollowThisWriter(writerId, fanId);
        return GraceJSONResult.ok(res);
    }

    @Override
    public GraceJSONResult follow(String writerId, String fanId) {
        myFansService.follow(writerId, fanId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult unfollow(String writerId, String fanId) {
        myFansService.unfollow(writerId, fanId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryAll(String writerId, Integer page, Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        return GraceJSONResult.ok(myFansService.queryMyFansList(writerId, page, pageSize));
    }

    @Override
    public GraceJSONResult queryRatio(String writerId) {

        int manCounts = myFansService.queryFansCounts(writerId, Sex.man);
        int womanCounts = myFansService.queryFansCounts(writerId, Sex.woman);

        FansCountsVO fansCountsVO = new FansCountsVO();
        fansCountsVO.setManCounts(manCounts);
        fansCountsVO.setWomanCounts(womanCounts);
        return GraceJSONResult.ok(fansCountsVO);
    }

    @Override
    public GraceJSONResult queryRatioByRegion(String writerId) {

        return GraceJSONResult.ok(myFansService.queryRegionRatioCounts(writerId));
    }
}
