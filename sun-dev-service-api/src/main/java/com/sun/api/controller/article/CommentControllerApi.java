package com.sun.api.controller.article;

import com.sun.grace.result.GraceJSONResult;
import com.sun.pojo.bo.CommentReplyBO;
import com.sun.pojo.bo.NewArticleBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Tag(name = "文章评论业务", description = "文章评论业务的Controller")
@RequestMapping("comment")
public interface CommentControllerApi {

    @Operation(summary = "用户评论", description = "用户评论", method = "POST")
    @PostMapping("/createComment")
    public GraceJSONResult adminLogin(@RequestBody @Valid CommentReplyBO commentReplyBO,
                                      BindingResult bindingResult);


    @Operation(summary = "评论数量", description = "评论数量", method = "GET")
    @GetMapping("/counts")
    public GraceJSONResult commentCounts(@RequestParam String articleId);

    @Operation(summary = "查询文章所有的评论列表", description = "查询文章所有的评论列表", method = "GET")
    @GetMapping("/list")
    public GraceJSONResult commentCounts(@RequestParam String articleId,
                                         @RequestParam Integer page,
                                         @RequestParam Integer pageSize);



}
