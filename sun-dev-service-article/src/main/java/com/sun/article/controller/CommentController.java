package com.sun.article.controller;

import com.sun.api.BaseController;
import com.sun.api.controller.article.CommentControllerApi;
import com.sun.api.controller.user.HelloControllerApi;
import com.sun.article.service.CommentPortalService;
import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.CommentReplyBO;
import com.sun.pojo.vo.AppUserVO;
import com.sun.utils.JsonUtils;
import com.sun.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class CommentController extends BaseController implements CommentControllerApi {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommentPortalService commentPortalService;

    @Override
    public GraceJSONResult adminLogin(CommentReplyBO commentReplyBO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = getErrors(bindingResult);
            return GraceJSONResult.errorMap(errors);
        }

        // 根据留言用户的id查询他的昵称,用于存入到数据表中进行字段的冗余处理
        String userId = commentReplyBO.getCommentUserId();
        // 发起远程调用，获得用户昵称
        String userServerUrlExecute =
                "http://user.sun.com:8003/user/queryById?userId=" + userId;
        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(userServerUrlExecute, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        String userJson = "";
        String nickname = "";
        AppUserVO userVO = null;
        if (bodyResult.getStatus() == 200) {
            userJson = JsonUtils.objectToJson(bodyResult.getData());
            userVO = JsonUtils.jsonToPojo(userJson, AppUserVO.class);
        }
        if (userVO != null ) {
            nickname = userVO.getNickname();
        }else {
            return GraceJSONResult.errorMsg("未获得用户信息");
        }
        String face = userVO.getFace();

        //保存用户评论信息到数据库
        commentPortalService.creatComment(commentReplyBO.getArticleId(),
                                          commentReplyBO.getFatherId(),
                                          commentReplyBO.getContent(),
                                          userId,nickname,face);


        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult commentCounts(String articleId) {
        Integer counts = getCountsFromRedis(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId);
        return GraceJSONResult.ok(counts);
    }

    @Override
    public GraceJSONResult commentCounts(String articleId, Integer page, Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult result = commentPortalService.queryArticleComments(articleId, page, pageSize);

        return GraceJSONResult.ok(result);
    }
}

