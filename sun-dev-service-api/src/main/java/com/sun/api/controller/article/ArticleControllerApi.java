package com.sun.api.controller.article;

import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.AdminLoginBO;
import com.sun.pojo.bo.NewAdminBO;
import com.sun.pojo.bo.NewArticleBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Tag(name = "文章业务", description = "文章业务的Controller")
@RequestMapping("article")
public interface ArticleControllerApi {
    @Operation(summary = "用户发文", description = "用户发文", method = "POST")
    @PostMapping("/createArticle")
    public GraceJSONResult adminLogin(@RequestBody @Valid NewArticleBO newArticleBO,
                                      BindingResult bindingResult);

    @Operation(summary = "查询用户所有的文章列表", description = "查询用户所有的文章列表", method = "POST")
    @PostMapping("/queryMyList")
    public GraceJSONResult queryMyList(@RequestParam String userId,
                                       @RequestParam String keyword,
                                       @RequestParam Integer status,
                                       @RequestParam Date startDate,
                                       @RequestParam Date endDate,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize);

    @Operation(summary = "管理员查询用户所有的文章列表", description = "管理员查询用户所有的文章列表", method = "POST")
    @PostMapping("/queryAllList")
    public GraceJSONResult queryMyList(@RequestParam Integer status,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize);

    @Operation(summary = "管理员对文章进行审核", description = "管理员对文章进行审核", method = "POST")
    @PostMapping("/doReview")
    public GraceJSONResult doReview(@RequestParam String articleId,
                                    @RequestParam Integer passOrNot);

}
