package com.sun.admin.controller;

import com.sun.admin.service.FriendLinkService;
import com.sun.api.BaseController;
import com.sun.api.controller.admin.FriendLinkControllerApi;
import com.sun.api.controller.user.HelloControllerApi;
import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.SaveFriendLinkBO;
import com.sun.pojo.mo.FriendLinkMO;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class FriendLinkController extends BaseController implements FriendLinkControllerApi {

    @Autowired
    private FriendLinkService friendLinkService;

    @Override
    public GraceJSONResult saveOrUpdateFriendLink(@Valid SaveFriendLinkBO saveFriendLinkBO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = getErrors(bindingResult);
            return GraceJSONResult.errorMap(errors);
        }

        FriendLinkMO friendLinkMO = new FriendLinkMO();
        BeanUtils.copyProperties(saveFriendLinkBO,friendLinkMO);
        friendLinkMO.setCreateTime(new Date());
        friendLinkMO.setUpdateTime(new Date());
        friendLinkMO.setId(UUID.randomUUID().toString());


        friendLinkService.saveOrUpdateFriendLink(friendLinkMO);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getFriendLinkList() {
        return GraceJSONResult.ok(friendLinkService.queryAllFriendLinkList());
    }

    @Override
    public GraceJSONResult delete(String linkId) {
        friendLinkService.delete(linkId);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryPortalAllFriendLinkList() {
        List<FriendLinkMO> list = friendLinkService.queryPortalAllFriendLinkList();
        return GraceJSONResult.ok(list);
    }
}
