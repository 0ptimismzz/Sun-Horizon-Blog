package com.sun.admin.controller;

import com.baidu.aip.face.AipFace;
import com.sun.admin.service.AdminUserService;
import com.sun.api.BaseController;
import com.sun.api.controller.admin.AdminMngControllerApi;
import com.sun.api.controller.user.HelloControllerApi;
import com.sun.exception.GraceException;
import com.sun.grace.result.GraceJSONResult;
import com.sun.grace.result.ResponseStatusEnum;
import com.sun.pojo.AdminUser;
import com.sun.pojo.bo.AdminLoginBO;
import com.sun.pojo.bo.NewAdminBO;
import com.sun.utils.BaiDuFaceUtils;
import com.sun.utils.PagedGridResult;
import com.sun.utils.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
public class AdminMngController extends BaseController implements AdminMngControllerApi {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AipFace aipFace;

    @Override
    public GraceJSONResult adminLogin(AdminLoginBO adminLoginBO,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        // 查询admin用户的信息
        AdminUser admin = adminUserService.queryAdminUserByUsername(adminLoginBO.getUsername());
        // 判断admin不为空
        if (admin == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }

        // 判断密码是否匹配
        boolean isPwdMatch = BCrypt.checkpw(adminLoginBO.getPassword(), admin.getPassword());
        if (isPwdMatch) {
            doLoginSettings(admin, request, response);
            return GraceJSONResult.ok();
        }else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }
    }

    @Override
    public GraceJSONResult adminIsExist(String username) {
        checkAdminExist(username);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult addNewAdmin(NewAdminBO newAdminBO) {

        // 密码不为空，则必须判断两次输入一致
        if (StringUtils.isNotBlank(newAdminBO.getPassword())) {
            if (!newAdminBO.getPassword().equalsIgnoreCase(newAdminBO.getConfirmPassword())) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
            }
        }

        // 校验用户名唯一
        checkAdminExist(newAdminBO.getUsername());

        // 存入admin信息
        adminUserService.createAdminUser(newAdminBO);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getAdminList(Integer page, Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult result = adminUserService.queryAdminList(page, pageSize);

        return GraceJSONResult.ok(result);
    }

    @Override
    public GraceJSONResult adminLogout(String adminId, HttpServletRequest request, HttpServletResponse response) {
        // 从redis中删除admin的会话token
        redisOperator.del(REDIS_ADMIN_TOKEN + ":" + adminId);

        // 从cookie中清理admin登录的相关信息
        deleteCookie(request, response, "atoken");
        deleteCookie(request, response, "aid");
        deleteCookie(request, response, "aname");

        return GraceJSONResult.ok();
    }

    private void doLoginSettings(AdminUser adminUser,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        String token = UUID.randomUUID().toString();
        // 保存token到redis中
        redisOperator.set(REDIS_ADMIN_TOKEN + ":" + adminUser.getId(), token);
        // 保存admin登录基本信息到cookie中
        setCookie(request, response, "atoken", token, COOKIE_MONTH);
        setCookie(request, response, "aid", adminUser.getId(), COOKIE_MONTH);
        setCookie(request, response, "aname", adminUser.getAdminName(), COOKIE_MONTH);

    }

    @Override
    public GraceJSONResult adminFaceLogin(AdminLoginBO adminLoginBO,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        // 0.判断用户名和人脸信息不能为空
        if (StringUtils.isBlank(adminLoginBO.getUsername())) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_USERNAME_NULL_ERROR);
        }
        String tempFace64 = adminLoginBO.getImg64();
        if (StringUtils.isBlank(tempFace64)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_NULL_ERROR);
        }
        // 1.从数据库中查询出faceId
        AdminUser admin = adminUserService.queryAdminUserByUsername(adminLoginBO.getUsername());
        String adminFaceId = admin.getFaceId();
        if (StringUtils.isBlank(adminFaceId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);
        }
        // 2.请求文件服务，获得人脸数据的base64数据
        String fileServerUrlExecute = "http://files.sun.com:8004/fs/readFace64InGridFS?faceId=" + adminFaceId;
        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(fileServerUrlExecute,GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        String base64DB = (String) bodyResult.getData();
        // 3.调用百度云ai进行人脸对比识别，判断可信度，从而实现人脸登录
        boolean result = BaiDuFaceUtils.verifyUser(aipFace, base64DB, tempFace64,80);
        if (!result) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);
        }
        // 4.admin登录后的数据设置，redis和cookie
        doLoginSettings(admin, request, response);


        return GraceJSONResult.ok();
    }

    private void checkAdminExist(String username) {
        AdminUser admin = adminUserService.queryAdminUserByUsername(username);
        if (admin != null) {
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }

    }
}
